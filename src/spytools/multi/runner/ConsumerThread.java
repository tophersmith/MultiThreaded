package spytools.multi.runner;

import java.util.concurrent.BlockingQueue;

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
	
	private int count = 0;
	private final int countMax = 100000;
	private final Logger log = new Logger();
	private ThreadNotifier notifier = ThreadNotifier.getInstance();

	private volatile boolean done = false;
	
	public ConsumerThread(ExecutionType exType, int curThread, int totalThreads){
		this.exType = exType;
		this.exConsume = this.exType.getConsumer();
		this.threadNum = curThread;
		this.totalThreads = totalThreads;
		this.queue = this.exType.getGuessQueue();
	}
	
	@Override
	public void run() {
		try {
			while(!this.notifier.shouldHalt(ThreadType.CONSUMER_THREAD) || (this.queue.size() >=  this.totalThreads)){
				
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
					
				if(++this.count >= this.countMax){
					this.log.info(this.toString() + " Iteration: " + this.count + " Current Guess: " + this.exType.provideConsoleUpdate(go));
					this.count = 0;
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
