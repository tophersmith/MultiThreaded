package spytools.multi.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import spytools.multi.custom.generators.AbstractGeneratorInfo;
import spytools.multi.execplan.AbstractExecutionPlan;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.SetupException;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

/**
 * ProducerManagement handles all producer threads
 * It contains all applicable logic for delegating an individual guess and
 * also dispatches to the applicable ExecutionPlan to manage collecting guesses
 * in the applicable GuessObject 
 * 
 * e.g. ProducerManagement manages n producers generating n separate lists of 
 * GuessObjects which is takes from and collects via the ExecutionPlan into one single 
 * guessQueue and provides that for ConsumerThreads.
 * 
 * 
 * @author smitc
 */
public class ProducerManagement extends AbstractManagementThread{
	private AbstractExecutionPlan exType;
	private List<AbstractGeneratorInfo> gens;
	
	//keys are generator names (defined in ExecutionPlan) values are the queues populated by the generator
	private Map<String, BlockingQueue <SingleGuess>> collectionQueue; 
	
	//will be equivalent to the number of needed threads per generator
	private int numProducerThreads; 
	
	//manages generator threads
	private ExecutorService exec; 
	
	//used if client tries to give more threads to producers than needed
	private int unusedThreads;
	
	private final Logger log = new Logger();
	private ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	public ProducerManagement(AbstractExecutionPlan exType, int producerThreads, AbstractGeneratorInfo... gen) throws SetupException{
		this.exType = exType;
		this.gens = new ArrayList<AbstractGeneratorInfo>();
		for(AbstractGeneratorInfo g : gen){
			this.gens.add(g);
		}
		
		int neededThreads = determineNeededThreads(gen);
		if(neededThreads > producerThreads)
			throw new SetupException("Not enough threads allocated for execution");
		
		this.collectionQueue = exType.generateQueues(gen.length);
		this.numProducerThreads = producerThreads;
		this.exec = Executors.newFixedThreadPool(this.numProducerThreads);
	}
	
	/**
	 * 
	 * @param gen 0 or more AbstractGeneratorInfo instances to be used
	 * @return number of threads needed to execute each Generator
	 */
	public static int determineNeededThreads(AbstractGeneratorInfo... gen){
		int neededThreads = 0;
		for(AbstractGeneratorInfo g : gen){
			neededThreads += g.getNeededThreads();
		}
		return neededThreads;
	}
	
	public int getUnusedThreads(){
		return this.unusedThreads;
	}
	
	@Override
	public void run() {
		/* 
		 * for each generator, generate the threadnumber to be used 
		 * as an offset if there are ever any producers that need multiple threads
		 */
		for(AbstractGeneratorInfo gen : this.gens){
			final int allocThreads = gen.getNeededThreads();
			for(int i = 0; i < allocThreads; i++){
				final int curThread = i;
				gen.init(curThread, gen.getNeededThreads(), this.collectionQueue);
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
	}
	
	private void generateGuesses() throws InterruptedException{
		AbstractGeneratorInfo[] gens = this.exType.getGenerators();
		SingleGuess[] guesses = new SingleGuess[gens.length];
		generateGuess(gens, 0, guesses);
	}
	
	/**
	 * Given the generators used, recursively calls itself to populate a SingleGuess array 
	 * which is provided to the ExecutionPlan to turn into a GuessObject
	 * 
	 * This method is designed to create every possible combination of Generator outputs possible
	 * 
	 * recursive depth is bounded to the number of Generators, which should never get too deep
	 * 
	 * @param gens array of Generator objects to be queried for guesses
	 * @param index current array index of gens and guesses
	 * @param guesses array of guesses so far, one guess per Generator
	 * 
	 * @throws InterruptedException is the Thread has been told to terminate
	 */
	private void generateGuess(final AbstractGeneratorInfo[] gens, int index,  final SingleGuess[] guesses) throws InterruptedException{
		AbstractGeneratorInfo g = gens[index];
		BlockingQueue<SingleGuess> q = g.getQueue();
		int numNulls = g.getTotalThreadNum();
		while(!this.notifier.shouldHalt(ThreadType.PRODUCERS)){
			SingleGuess guess = q.take();
			if(guess.toString() == null){
				if(--numNulls == 0){
					break;
				}
				continue;
			}
			guesses[index] = guess;
			if(index < (gens.length - 1))
				generateGuess(gens, index + 1,  guesses);
			else
				this.exType.addGuessObject(this.exType.makeGuessObject(gens, guesses));
		}
	}

	@Override
	public void shutdown(){
		try{
			this.log.info("ProducerThreads shutdown");
			this.notifier.haltThread(ThreadType.PRODUCERS);
			this.exec.shutdown();
			this.exType.clearCollector(this.collectionQueue);
			this.exec.awaitTermination(1, TimeUnit.SECONDS);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			this.exec.shutdownNow();
			this.exType.clearCollector(this.collectionQueue);
			this.log.info("ProducerThreads complete");
		}
	}
}
