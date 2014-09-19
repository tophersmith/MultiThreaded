package spytools.multi.runner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.custom.storage.GuessObject;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public class ConsumerManagement extends ManagementThread implements Runnable{
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
			ConsumerThread c = new ConsumerThread(this.exType, curThread, this.consumerThreads);
			threads.add(c);
			this.exec.execute(c);
		}
		
		try {
			while(!this.notifier.shouldHalt(ThreadType.CONSUMER_MANAGEMENT) && !threads.isEmpty()){
				if(this.exType.getGuessQueue().isEmpty() && this.notifier.isDone(ThreadType.PRODUCER_THREAD))
					break;
				Iterator<ConsumerThread> iter = threads.iterator();
				while(iter.hasNext()){
					ConsumerThread c = iter.next();
					if(c.isDone())
						iter.remove();
				}
				Thread.sleep(1000);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			shutdown();
			notifyDone();
		}
		
	}
	
	@Override
	public void notifyDone(){
		this.notifier.setDone(ThreadType.CONSUMER_THREAD);
		this.notifier.setDone(ThreadType.CONSUMER_MANAGEMENT);
	}
	
	@Override
	public void shutdown(){
		try{
			this.notifier.haltThread(ThreadType.CONSUMER_THREAD);
			this.exec.shutdown();
			this.exec.awaitTermination(10, TimeUnit.SECONDS);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			this.exec.shutdownNow();	
			this.notifier.haltThread(ThreadType.CONSUMER_MANAGEMENT);
		}
	}
}
