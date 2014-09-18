package spytools.multi.custom.generators;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import spytools.multi.helpers.Logger;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public abstract class GeneratorInfo implements Runnable{
	String generatorQueueName;
	BlockingQueue<String> queue;
	int threadNum;
	int totalThreadNum;
	BigInteger bigTotalThreadNum;
	String threadName;
	
	protected final Logger log = new Logger();
	protected ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	public void init(int threadNum, int totalThreads, Map<String, BlockingQueue<String>> queue) {
		this.threadNum = threadNum;
		this.totalThreadNum = totalThreads;
		this.bigTotalThreadNum = BigInteger.valueOf(this.totalThreadNum);
		this.threadName = "Thread-" + this.threadNum;
		this.queue = queue.get(this.generatorQueueName);
	}
	
	@Override
	public void run(){
		while(!this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD)){
			this.queue.add(this.generateNextGuess());
		}
		if(this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD))
			this.queue.clear();
	}
	
	public void setGeneratorName(String name) {
		this.generatorQueueName = name;
	}
	
	public abstract int getNeededThreads();
	public abstract int getMaxThreads(int available);
	public abstract String generateNextGuess();
	@Override
	public abstract String toString();
}
