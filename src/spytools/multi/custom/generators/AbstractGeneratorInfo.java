package spytools.multi.custom.generators;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import spytools.multi.helpers.Logger;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public abstract class AbstractGeneratorInfo implements Runnable{
	String generatorQueueName;
	BlockingQueue<SingleGuess> queue;
	int threadNum;
	BigInteger bigThreadNum;
	int allocatedThreads;
	BigInteger bigAllocThreadNum;
	String threadName;
	
	protected final Logger log = new Logger();
	protected ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	public void init(int threadNum, int totalThreads, Map<String, BlockingQueue<SingleGuess>> collectionQueue) {
		this.threadNum = threadNum;
		this.bigThreadNum = BigInteger.valueOf(this.threadNum);
		this.allocatedThreads = totalThreads;
		this.bigAllocThreadNum = BigInteger.valueOf(this.allocatedThreads);
		this.threadName = "Thread-" + this.threadNum;
		this.queue = collectionQueue.get(this.generatorQueueName);
		initializeInfo();
	}
	
	@Override
	public void run(){
		while(!this.notifier.shouldHalt(ThreadType.PRODUCERS)){
			try{
				String s = this.generateNextGuess();
				this.queue.put(new SingleGuess(s , this.generatorQueueName));
				this.log.debug(this.toString() + " offered " + s);
			} catch(InterruptedException e){
				if(!this.notifier.shouldHalt(ThreadType.PRODUCERS))
					e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		if(this.notifier.shouldHalt(ThreadType.PRODUCERS))
			this.queue.clear();
	}
	
	public BlockingQueue<SingleGuess> getQueue(){
		return this.queue;
	}
	
	public int getTotalThreadNum(){
		return this.allocatedThreads;
	}
	
	public void setGeneratorName(String name) {
		this.generatorQueueName = name;
	}
	
	protected abstract void initializeInfo();
	public abstract int getNeededThreads();
	public abstract String generateNextGuess();
	@Override
	public abstract String toString();
}
