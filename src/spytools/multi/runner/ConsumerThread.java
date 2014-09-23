package spytools.multi.runner;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import spytools.multi.execplan.AbstractExecutionPlan;
import spytools.multi.execplan.consumer.AbstractExecutionConsumer;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;
import spytools.multi.storage.AbstractGuessObject;

/**
 * Handles making a guess and judging correctness, notifying if necessary
 * 
 * one or more consumers take from the guessQueue, execute the guess 
 * in their own context using the ExecutionPlan, and, if correct, may begin 
 * the shutdown procedure, if the ExecutionPlan calls for it.
 * 
 * @author smitc
 */
public class ConsumerThread implements Runnable{
	private final AbstractExecutionPlan exType;
	private final AbstractExecutionConsumer exConsume;
	
	private final int threadNum;
	
	private BlockingQueue<AbstractGuessObject> queue;
	
	//counter handles notifying clients of total guesses, shared across ConsumerManagement and all threads
	private final AtomicLong counter;
	private final long countMax = 100000;
	private final Logger log = new Logger();
	private final ThreadNotifier notifier = ThreadNotifier.getInstance();

	public ConsumerThread(AbstractExecutionPlan exType, int curThread, AtomicLong counter){
		this.exType = exType;
		this.exConsume = this.exType.getConsumer();
		this.threadNum = curThread;
		this.queue = this.exType.getGuessQueue();
		this.counter = counter;
	}
	
	@Override
	public void run() {
		try {
			long iterationCount = 0;
			while(!this.notifier.shouldHalt(ThreadType.CONSUMERS)){
				
				if(this.queue.isEmpty()){
					this.log.debug(this.toString() + " reports empty guess queue");
				}
				
				AbstractGuessObject go = this.queue.take();
				
				this.log.debug(this.toString() + " taken " + go.toString());
				if(this.exConsume.isCorrect(go)){
					this.exType.storeCorrectGuess(go);
					this.log.found(go.toString());
					if(this.exType.stopOnFirstCorrectGuess()){
						this.notifier.haltAll();
					}
				}
				this.exConsume.reset();
				
				iterationCount = this.counter.incrementAndGet();
				if(iterationCount % this.countMax == 0){
					this.log.info(this.toString() + " Iteration: " + iterationCount + " Current Guess: " + this.exType.provideConsoleUpdate(go));
				}
			}
		} catch (InterruptedException e) {
			if(!this.notifier.shouldHalt(ThreadType.CONSUMERS))
				this.log.debug(this.toString() + " Interrupted.");
		}
	}
	
	@Override
	public String toString(){
		return "ConsumerThread-" + this.threadNum;
	}
}
