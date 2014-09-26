package spytools.multi.generators;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import spytools.multi.helpers.Logger;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

/**
 * AbstractGenerator handles universal methods shared across Generator instances
 * 
 * @author smitc
 */
public abstract class AbstractGeneratorInfo implements Runnable{
	String generatorQueueName;
	BlockingQueue<SingleGuess> queue;
	int threadNum;
	
	int allocatedThreads;
	
	String threadName;
	
	protected final Logger log = new Logger();
	protected ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	/**
	 * initialize the Generator by adding thread and queue information
	 * 
	 * @param threadNum current threadIdentifier
	 * @param totalThreads total allocated generator threads
	 * @param collectionQueue Map of generator queue name to queue containing that generator's guesses
	 */
	public void init(int threadNum, int totalThreads, Map<String, BlockingQueue<SingleGuess>> collectionQueue) {
		this.threadNum = threadNum;
		this.allocatedThreads = totalThreads;
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
	
	public String getGeneratorName(){
		return this.generatorQueueName;
	}
	
	/**
	 * Initialize any necessary information for a given Generator.
	 * Executes just before starting thread
	 */
	protected abstract void initializeInfo();
	
	/**
	 * @return the minimum number of threads needed to generate a guess
	 */
	public abstract int getNeededThreads();
	
	/**
	 * @return the next guess accoring to the Generator's inputs
	 */
	public abstract String generateNextGuess();
	
	@Override
	public abstract String toString();
}
