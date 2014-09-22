package spytools.multi.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.SetupException;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public class ProducerManagement extends ManagementThread implements Runnable{
	private ExecutionType exType;
	private List<GeneratorInfo> gens;
	private Map<String, BlockingQueue <SingleGuess>> collectionQueue;
	
	private int numProducerThreads;
	private ExecutorService exec; //manages sub threads
	List<ThreadDist> threadDist;
	
	private int unusedThreads;
	
	private final Logger log = new Logger();
	private ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	public ProducerManagement(ExecutionType exType, int producerThreads, GeneratorInfo... gen) throws SetupException{
		this.exType = exType;
		this.gens = new ArrayList<GeneratorInfo>();
		for(GeneratorInfo g : gen){
			this.gens.add(g);
		}
		
		int neededThreads = determineNeededThreads(gen);
		if(neededThreads > producerThreads)
			throw new SetupException("Not enough threads allocated for execution");
		
		this.collectionQueue = exType.generateQueues(gen.length);
		this.numProducerThreads = producerThreads;
		this.exec = Executors.newFixedThreadPool(this.numProducerThreads);
		this.threadDist = distributeThreads();
	}
	
	public static int determineNeededThreads(GeneratorInfo... gen){
		int neededThreads = 0;
		for(GeneratorInfo g : gen){
			neededThreads += g.getNeededThreads();
		}
		return neededThreads;
	}
	
	public int getUnusedThreads(){
		return this.unusedThreads;
	}
	
	@Override
	public void run() {
		for(ThreadDist td : this.threadDist){
			final int max = td.currentAlloc;
			for(int i = 0; i < max; i++){
				final int curThread = i;
				td.gen.init(curThread, max, this.collectionQueue);
				GeneratorInfo gen = td.gen.createNewInstance();
				this.exec.execute(gen);
			}
		}
		//this is long running, it stops when there are no more guesses to generate or the thread has been halted
		try {
			generateGuesses();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		shutdown();
		notifyDone();
	}
	
	private void generateGuesses() throws InterruptedException{
		GeneratorInfo[] gens = this.exType.getGenerators();
		SingleGuess[] guesses = new SingleGuess[gens.length];
		generateGuess(gens, 0, gens.length - 1,  guesses);
	}
	
	private void generateGuess(GeneratorInfo[] gens, int index, int maxIndex, SingleGuess[] guesses) throws InterruptedException{
		GeneratorInfo g = gens[index];
		BlockingQueue<SingleGuess> q = g.getQueue();
		int numNulls = g.getTotalThreadNum();
		while(!this.notifier.shouldHalt(ThreadType.PRODUCER_MANAGEMENT) || !this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD)){
			SingleGuess guess = q.take();
			if(guess.toString() == null){
				if(--numNulls == 0){
					break;
				}
				continue;
			}
			guesses[index] = guess;
			if(index < maxIndex)
				generateGuess(gens, index + 1, maxIndex, guesses);
			else
				this.exType.addGuessObject(this.exType.makeGuessObject(gens));
		}
	}

	@Override
	public void notifyDone(){
		this.notifier.setDone(ThreadType.PRODUCER_THREAD);
		this.notifier.setDone(ThreadType.PRODUCER_MANAGEMENT);
		for(String s: this.collectionQueue.keySet()){
			this.collectionQueue.get(s).clear();
		}
	}
	
	@Override
	public void shutdown(){
		try{
			this.notifier.haltThread(ThreadType.PRODUCER_THREAD);
			this.exec.shutdown();
			this.exType.clearCollector(this.collectionQueue);
			this.exec.awaitTermination(10, TimeUnit.SECONDS);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			this.exec.shutdownNow();
			for(String s: this.collectionQueue.keySet()){
				this.collectionQueue.get(s).clear();
			}
			this.notifier.haltThread(ThreadType.PRODUCER_MANAGEMENT);
		}
	}
	
	
	/**
	 * helper class to determine thread distribution
	 * 
	 * @author smitc
	 */
	private class ThreadDist{
		public GeneratorInfo gen;
		public int currentAlloc = 0;
		public int maxAlloc;
		public int min;
		public ThreadDist(GeneratorInfo g, int availableThreads){
			this.gen = g;
			this.min = this.gen.getNeededThreads();
			this.maxAlloc = this.gen.getMaxThreads(availableThreads);
		}
		public int reserveMinThreads(){
			return reserveThread(this.min);
		}
		public int reserveThread(int numThreads){
			this.currentAlloc += numThreads;
			return numThreads;
		}
	}
	
	private List<ThreadDist> distributeThreads(){
		List<ThreadDist> setupDist = new ArrayList<ThreadDist>();
		int remainingThreads = this.numProducerThreads;
		
		for(GeneratorInfo g : this.gens){
			ThreadDist td = new ThreadDist(g, this.numProducerThreads);
			remainingThreads -= td.reserveMinThreads();
			setupDist.add(td);
		}
		
		if(remainingThreads > 0){
			remainingThreads = distributeRemaining(setupDist, remainingThreads);
		}
		
		this.unusedThreads = remainingThreads;
		
		if(remainingThreads > 0)
			this.log.debug("You have " + remainingThreads + " unused producer threads.");
		
		
		return setupDist;
	}
	
	private static int distributeRemaining(List<ThreadDist> setupDist, int remainingThreads){
		List<ThreadDist> dist = new ArrayList<ThreadDist>();
		int count = 0;
		ThreadDist td;
		
		while(remainingThreads > 0 && !setupDist.isEmpty()){
			td = setupDist.get(count);
			if(td.currentAlloc >= td.maxAlloc){
				dist.add(setupDist.remove(count));
			} else{
				remainingThreads -= td.reserveThread(1);
			}
			count++;
			if(count >= setupDist.size()){
				count = 0;
			}
		}
		setupDist.addAll(dist); 
		return remainingThreads;
	}
	
}
