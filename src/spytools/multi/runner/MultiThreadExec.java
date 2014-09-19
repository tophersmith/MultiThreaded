package spytools.multi.runner;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.helpers.SetupException;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;


public class MultiThreadExec {
	private final ExecutionType exType;
	private final ProducerManagement pThread;
	private final ConsumerManagement cThread;
	private int threadsAvail;
	private final ExecutorService exec;
	
	private ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	public MultiThreadExec(ExecutionType exType, int suggestProducers) throws SetupException{
		this.exType = exType;
		this.threadsAvail = Runtime.getRuntime().availableProcessors();
		int minProducers = ProducerManagement.determineNeededThreads(exType.getGenerators());
		int producers = suggestProducers == 0 ? this.threadsAvail/2 : suggestProducers;
		
		//if there aren't enough processors for one thread per processor and one consumer, 
		//double threads to make room. This will cause a slowdown, but may not be too bad.
		if(minProducers > producers){
			if(this.threadsAvail*2 > minProducers+1){
				this.threadsAvail = minProducers * 2;
				producers = this.threadsAvail/2;
			}
		} 
		
		this.pThread = new ProducerManagement(exType, producers, exType.getGenerators());
		this.threadsAvail = this.threadsAvail - producers + this.pThread.getUnusedThreads();
		this.cThread = new ConsumerManagement(exType, this.threadsAvail);
		this.exec = Executors.newFixedThreadPool(2);
	}
	public MultiThreadExec(ExecutionType exType) throws SetupException{
		this(exType, 0);
	}
	
	public void execute(){
		long startTime = System.nanoTime();
		this.exec.execute(this.pThread);
		this.exec.execute(this.cThread);
		while(!this.notifier.shouldHalt(ThreadType.MAIN)){
			if(this.notifier.isDone(ThreadType.PRODUCER_MANAGEMENT) && this.notifier.isDone(ThreadType.CONSUMER_MANAGEMENT)){
				this.notifier.haltThread(ThreadType.MAIN);
			}
		}
		shutdownAll();
		System.out.println(this.getResults());
		System.out.println("Time: " + ((System.nanoTime()-startTime)/1000000000.0));
	}
	
	private static void shutdownManagement(ManagementThread mgtThread) {
		mgtThread.shutdown();
	}
	
	private void shutdownAll(){
		this.notifier.haltAll();
		
		if(!this.notifier.isDone(ThreadType.PRODUCER_MANAGEMENT) || !this.notifier.isDone(ThreadType.PRODUCER_THREAD))
			shutdownManagement(this.pThread);
		if(!this.notifier.isDone(ThreadType.CONSUMER_MANAGEMENT) || !this.notifier.isDone(ThreadType.CONSUMER_THREAD))
			shutdownManagement(this.cThread);
		
		try{
			this.exec.shutdown();
			this.exec.awaitTermination(10, TimeUnit.SECONDS);
		} catch(InterruptedException e){
			e.printStackTrace();
		} finally{
			this.exec.shutdownNow();
			this.notifier.setDone(ThreadType.MAIN);
		}
	}
	
	public String getResults(){
		return this.exType.getCorrectGuesses();
	}
}
