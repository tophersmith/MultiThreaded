package spytools.multi.runner;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.custom.execplan.ExecutionType.ExecutionConsumer;
import spytools.multi.custom.storage.GuessObject;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public class ConsumerThread implements Runnable{
	private ExecutionType exType;
	private ExecutionConsumer exConsume;
	
	private int threadNum;
	private int totalThreads;
	
	private BlockingQueue<GuessObject> queue;
	
	private final AtomicLong counter;
	private final long countMax = 100000;
	private final Logger log = new Logger();
	private ThreadNotifier notifier = ThreadNotifier.getInstance();

	private volatile boolean done = false;
	
	public ConsumerThread(ExecutionType exType, int curThread, int totalThreads, AtomicLong counter){
		this.exType = exType;
		this.exConsume = this.exType.getConsumer();
		this.threadNum = curThread;
		this.totalThreads = totalThreads;
		this.queue = this.exType.getGuessQueue();
		this.counter = counter;
	}
	
	@Override
	public void run() {
		try {
			long iterationCount = 0;
			while(!this.notifier.shouldHalt(ThreadType.CONSUMER_THREAD)){
				
				if(this.queue.isEmpty()){
					this.log.debug(this.toString() + " reports empty guess queue");
				}
				
				GuessObject go = this.queue.take();
				
				this.log.debug(this.toString() + " taken " + go.toString());
				if(this.exConsume.isCorrect(go)){
					this.exType.storeCorrectGuess(go);
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
			this.log.debug(this.toString() + " Interrupted.");
		} finally{
			consumerDone();
		}
	}
	
	private void consumerDone(){
		this.done = true;
	}
	
	public boolean isDone(){
		return this.done;
	}
	
	@Override
	public String toString(){
		return "ConsumerThread-" + this.threadNum;
	}

}
