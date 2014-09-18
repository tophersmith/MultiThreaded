package spytools.multi.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public class ConsumerManagement implements Runnable{
	private ExecutionType exType;
	private int consumerThreads;
	private ExecutorService exec; //manages sub threads
	
	private final Logger log = new Logger();
	private ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	public ConsumerManagement(ExecutionType exType, int consumerThreads){
		this.exType = exType;
		this.consumerThreads = consumerThreads;
		this.exec = Executors.newFixedThreadPool(consumerThreads);
	}
	
	@Override
	public void run() {
		List<ConsumerThread> threads = new ArrayList<ConsumerThread>();
		for(int i = 0; i < this.consumerThreads; i++){
			final int curThread = i;
			ConsumerThread c = new ConsumerThread(this.exType, curThread);
			threads.add(c);
			this.exec.execute(c);
		}
		
		try {
			while(!this.notifier.shouldHalt(ThreadType.CONSUMER_MANAGEMENT) && !threads.isEmpty()){
				if(this.exType.getGuessQueue().isEmpty() && this.notifier.isDone(ThreadType.PRODUCER_THREAD))
					break;
				for(ConsumerThread c : threads){
					if(c.isDone())
						threads.remove(c);
				}
				Thread.sleep(1000);
			}
			notifyDone();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			shutdown();
		}
		
	}
	
	public void notifyDone(){
		this.notifier.setDone(ThreadType.CONSUMER_THREAD);
		this.notifier.setDone(ThreadType.CONSUMER_MANAGEMENT);
	}
	
	public void shutdown(){
		try{
			this.notifier.haltThread(ThreadType.CONSUMER_MANAGEMENT);
			this.notifier.haltThread(ThreadType.CONSUMER_THREAD);
			this.shutdown();
			this.exec.awaitTermination(10, TimeUnit.SECONDS);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			this.exec.shutdownNow();		
		}
	}
}
