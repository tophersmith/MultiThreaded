package spytools.multi.runner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import spytools.multi.execplan.AbstractExecutionPlan;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public class ConsumerManagement extends AbstractManagementThread{
	private AbstractExecutionPlan exType;
	private int consumerThreads;
	private ExecutorService exec; //manages sub threads
	
	private final Logger log = new Logger();
	private ThreadNotifier notifier = ThreadNotifier.getInstance();
	private final AtomicLong counter;
	
	public ConsumerManagement(AbstractExecutionPlan exType, int consumerThreads){
		this.exType = exType;
		this.consumerThreads = consumerThreads;
		this.exec = Executors.newFixedThreadPool(consumerThreads);
		this.counter = new AtomicLong(0);
	}
	
	@Override
	public void run() {
		//List<ConsumerThread> threads = new ArrayList<ConsumerThread>();
		for(int i = 0; i < this.consumerThreads; i++){
			final int curThread = i;
			ConsumerThread c = new ConsumerThread(this.exType, curThread, this.counter);
			//threads.add(c);
			this.exec.execute(c);
		}
		
		try {
			while(!this.notifier.shouldHalt(ThreadType.CONSUMERS)){
				if(this.exType.getGuessQueue().isEmpty() && this.notifier.shouldHalt(ThreadType.PRODUCERS))
					break;
			}
		} finally {
			shutdown();
		}
	}
	
	@Override
	public void shutdown(){
		try{
			this.log.info("ConsumerThreads shutdown");
			this.notifier.haltThread(ThreadType.CONSUMERS);
			this.exec.shutdown();
			this.exec.awaitTermination(1, TimeUnit.SECONDS);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			this.exec.shutdownNow();	
			this.log.info("ConsumerThreads complete");
		}
	}
	
	public long getFinalCount(){
		return this.counter.get();
	}
}
