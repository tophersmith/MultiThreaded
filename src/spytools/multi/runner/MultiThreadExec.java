package spytools.multi.runner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import spytools.multi.execplan.AbstractExecutionPlan;
import spytools.multi.generators.AbstractGeneratorInfo;
import spytools.multi.helpers.SetupException;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

/**
 * Main entrypoint for execution
 * 
 * handles creation/dispatching to producer/consumer management threads
 * 
 * @author smitc
 */
public class MultiThreadExec {
	private final AbstractExecutionPlan exType;
	private final ProducerManagement pThread;
	private final ConsumerManagement cThread;
	private int threadsAvail;
	private final ExecutorService exec;
	
	private ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	
	private MultiThreadExec(AbstractExecutionPlan exType, int suggestProducers, int threadsToUse, long maxGuesses, AbstractGeneratorInfo... gens) throws SetupException{
		this.exType = exType;
		this.exType.addGenerators(gens);
		
		this.threadsAvail = threadsToUse;
		boolean threadOverrideDisabled = true;
		if(this.threadsAvail < 1){
			this.threadsAvail = Runtime.getRuntime().availableProcessors();
			threadOverrideDisabled = false;
		} 
		
		int minProducers = ProducerManagement.determineNeededThreads(exType.getGenerators());
		int producers = suggestProducers == 0 ? this.threadsAvail/2 : suggestProducers;
		
		//if there aren't enough processors for one thread per processor and one consumer, 
		//double threads to make room. This will cause a slowdown, but may not be too bad.
		if(minProducers > producers){
			if(threadOverrideDisabled){
				throw new SetupException("Not enough threads were reserved for the Execution Plan given");
			}
			if(this.threadsAvail*2 > minProducers+1){
				this.threadsAvail = minProducers * 2;
				producers = this.threadsAvail/2;
			}
		} 
		
		this.pThread = new ProducerManagement(exType, producers, maxGuesses, exType.getGenerators());
		this.threadsAvail = this.threadsAvail - producers + this.pThread.getUnusedThreads();
		this.cThread = new ConsumerManagement(exType, this.threadsAvail);
		this.exec = Executors.newFixedThreadPool(2);
	}
	public MultiThreadExec(AbstractExecutionPlan exType, int threadsToUse, AbstractGeneratorInfo... gens) throws SetupException{
		this(exType, 0, threadsToUse, Long.MAX_VALUE, gens);
	}
	public MultiThreadExec(AbstractExecutionPlan exType, AbstractGeneratorInfo... gens) throws SetupException{
		this(exType, 0, 0, Long.MAX_VALUE, gens);
	}
	
	public void execute(){
		long startTime = System.nanoTime();
		this.exec.execute(this.pThread);
		this.exec.execute(this.cThread);
		while(!this.notifier.shouldHalt(ThreadType.MAIN)){//while this thread shouldn't stop
			if(this.notifier.shouldHalt(ThreadType.PRODUCERS) && this.notifier.shouldHalt(ThreadType.CONSUMERS)){//but if these threads should stop
				this.notifier.haltThread(ThreadType.MAIN);
			}
		}
		shutdownAll();
		System.out.println(this.getResults());
		System.out.println("Guesses: " + this.cThread.getFinalCount());
		System.out.println("Time: " + ((System.nanoTime()-startTime)/1000000000.0));
	}
	
	private static void shutdownManagement(AbstractManagementThread mgtThread) {
		mgtThread.shutdown();
	}
	
	private void shutdownAll(){
		this.notifier.haltAll();
		
		if(!this.notifier.shouldHalt(ThreadType.PRODUCERS))
			shutdownManagement(this.pThread);
		if(!this.notifier.shouldHalt(ThreadType.CONSUMERS))
			shutdownManagement(this.cThread);
		
		try{
			this.exec.shutdown();
			this.exec.awaitTermination(10, TimeUnit.SECONDS);
		} catch(InterruptedException e){
			e.printStackTrace();
		} finally{
			this.exec.shutdownNow();
		}
	}
	
	public String getResults(){
		return this.exType.getCorrectGuesses();
	}
}
