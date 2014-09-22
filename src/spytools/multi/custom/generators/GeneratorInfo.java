package spytools.multi.custom.generators;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import spytools.multi.helpers.Logger;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public abstract class GeneratorInfo implements Runnable{
	String generatorQueueName;
	BlockingQueue<SingleGuess> queue;
	int threadNum;
	BigInteger bigThreadNum;
	int totalThreadNum;
	BigInteger bigTotalThreadNum;
	String threadName;
	
	protected final Logger log = new Logger();
	protected ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	public void init(int threadNum, int totalThreads, Map<String, BlockingQueue<SingleGuess>> collectionQueue) {
		this.init(threadNum, totalThreads, collectionQueue.get(this.generatorQueueName));
	}
	private void init (int threadNum, int totalThreads,BlockingQueue<SingleGuess> guessQueue){
		this.threadNum = threadNum;
		this.bigThreadNum = BigInteger.valueOf(this.threadNum);
		this.totalThreadNum = totalThreads;
		this.bigTotalThreadNum = BigInteger.valueOf(this.totalThreadNum);
		this.threadName = "Thread-" + this.threadNum;
		this.queue = guessQueue;
		initializeInfo();
	}
	
	@Override
	public void run(){
		while(!this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD)){
			try{
				String s = this.generateNextGuess();
				this.queue.put(new SingleGuess(s , this.generatorQueueName));
				this.log.debug(this.toString() + " offered " + s);
			} catch(InterruptedException e){
				if(!this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD))
					e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		if(this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD))
			this.queue.clear();
	}
	
	public BlockingQueue<SingleGuess> getQueue(){
		return this.queue;
	}
	public int getTotalThreadNum(){
		return this.totalThreadNum;
	}
	
	public void setGeneratorName(String name) {
		this.generatorQueueName = name;
	}
	
	public GeneratorInfo createNewInstance(){
		GeneratorInfo gen = this.newInstance();
		gen.generatorQueueName = this.generatorQueueName;
		gen.init(this.threadNum, this.totalThreadNum, this.queue);
		return gen;
	}
	
	protected abstract void initializeInfo();
	public abstract int getNeededThreads();
	public abstract int getMaxThreads(int available);
	public abstract String generateNextGuess();
	@Override
	public abstract String toString();
	public abstract GeneratorInfo newInstance();
}
