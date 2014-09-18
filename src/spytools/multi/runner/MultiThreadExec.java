package spytools.multi.runner;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.helpers.SetupException;


public class MultiThreadExec {
	private final ExecutionType exType;
	private final ProducerManagement pThread;
	private final ConsumerManagement cThread;
	private int threadsAvail;
	private final ExecutorService exec;
	
	public MultiThreadExec(ExecutionType exType) throws SetupException{
		this.exType = exType;
		this.threadsAvail = Runtime.getRuntime().availableProcessors();
		int producers = ProducerManagement.determineNeededThreads(exType.getGenerators());
		
		//if there aren't enough processors for one thread per processor and one consumer, 
		//double threads to make room. This will cause a slowdown, but may not be too bad.
		if(producers > (this.threadsAvail - 1)){
			this.threadsAvail = Math.max(this.threadsAvail*2, producers+1);  
		}
		
		this.pThread = new ProducerManagement(exType, producers, exType.getGenerators());
		this.threadsAvail += this.pThread.getUnusedThreads();
		this.cThread = new ConsumerManagement(exType, this.threadsAvail);
		this.exec = Executors.newFixedThreadPool(2);
	}
	
	public void execute(){
		this.exec.execute(this.pThread);
		this.exec.execute(this.cThread);
	}
	
	public String getResults(){
		return this.exType.getCorrectGuesses();
	}
}
