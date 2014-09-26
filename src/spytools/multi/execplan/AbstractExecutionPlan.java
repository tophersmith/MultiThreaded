package spytools.multi.execplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.execplan.consumer.AbstractExecutionConsumer;
import spytools.multi.generators.AbstractGeneratorInfo;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.storage.AbstractGuessObject;

/**
 * AbstractExecutionPlan contains all information needed to provide information
 * to Producers and Consumers to make and check guesses
 * 
 * @author smitc
 */
public abstract class AbstractExecutionPlan {
	//if true, end execution once a correct answer found
	private final boolean stopOnFirstCorrect;
	
	//map of generator name to guess queue per named generator
	private Map<String, BlockingQueue<SingleGuess>> generatorQueues;
	
	//collected guesses from generatorQueue(s) become GuessObjects in this queue
	private BlockingQueue<AbstractGuessObject> guessQueue;
	
	private AbstractExecutionConsumer consumerInstance;

	protected List<AbstractGuessObject> correctGuesses;
	protected AbstractGeneratorInfo[] generators;
	protected static final int MAX_GENERATOR_QUEUE_SIZE = 1000000;
	protected static final int MAX_GUESS_QUEUE_SIZE = 1000000;
	
	protected final Logger log = new Logger();
	protected ThreadNotifier notifier = ThreadNotifier.getInstance();

	
	/**
	 * @param stopOnFirst if after finding the first correct guess, stop all execution and return that guess
	 */
	protected AbstractExecutionPlan(boolean stopOnFirst, AbstractExecutionConsumer consumer){
		this.guessQueue = new ArrayBlockingQueue<AbstractGuessObject>(MAX_GUESS_QUEUE_SIZE);
		this.stopOnFirstCorrect = stopOnFirst;
		this.correctGuesses = new ArrayList<AbstractGuessObject>();
		this.consumerInstance = consumer;
	}
	
	
	/**
	 * @return a <u>new</u> ExecutionConsumer for use in Consumer Threads
	 */
	public AbstractExecutionConsumer getConsumer(){
		return this.consumerInstance.duplicate();
	}
	
	/**
	 * adds n number of Generators and calls assignGeneratorNames
	 * @param gen 0 or more Generators
	 */
	public void addGenerators(AbstractGeneratorInfo... gen) {
		this.generators = gen;
		assignGeneratorNames(this.generators);
	}

	/**
	 * dispatch to generateQueuesByName to generator a number of generatorQueues
	 * 
	 * @param generators number of generators used to make a generatorQueue per Generator
	 * @return map of generated queue names to generator queues
	 */
	public Map<String, BlockingQueue<SingleGuess>> generateQueues(int generators) {
		if(this.generatorQueues == null){
			this.generatorQueues = new HashMap<String, BlockingQueue<SingleGuess>>();
			for(AbstractGeneratorInfo g : this.generators){
				this.generatorQueues.put(g.getGeneratorName(), new ArrayBlockingQueue<SingleGuess>(AbstractExecutionPlan.MAX_GENERATOR_QUEUE_SIZE/generators));
			}
		}
		return this.generatorQueues;
	}
	
	public BlockingQueue<AbstractGuessObject> getGuessQueue() {
		return this.guessQueue;
	}
	
	public void storeCorrectGuess(AbstractGuessObject go){
		this.correctGuesses.add(go);
	}
	
	public String getCorrectGuesses(){
		return 	"\n\n\nResults:\n"+
				"-----------------------------------------------------\n" +
				formatCorrectGuesses();
	}

	public boolean stopOnFirstCorrectGuess() {
		return this.stopOnFirstCorrect;
	}
	
	public AbstractGeneratorInfo[] getGenerators() {
		return this.generators;
	}

	/**
	 * removes all values from all generator queues
	 * 
	 * @param collectionQueue to clear
	 */
	public void clearCollectorQueue(Map<String, BlockingQueue<SingleGuess>> collectionQueue){
		for(String s : collectionQueue.keySet())
			collectionQueue.get(s).clear();
	}

	/**
	 * adds a GuessObject to the guessQueue
	 * 
	 * @param guess
	 * @throws InterruptedException if Interrupted while waiting to add to the queue
	 */
	public void addGuessObject(AbstractGuessObject guess) throws InterruptedException {
		this.guessQueue.put(guess);
	}
	
	/**
	 * Given a generator name, return the generator's GuessObject
	 */
	protected SingleGuess getGuessByGeneratorName(String generatorName, SingleGuess[] guesses){
		for(SingleGuess g : guesses){
			if(g.getGeneratorName().equals(generatorName)){
				return g;
			}
		}
		return null;
	}
	
	/**
	 * return the GuessObject's string representation
	 */
	public String provideConsoleUpdate(AbstractGuessObject go){
		return go.toString();
	}
	
	/**
	 * for the array of Generators, assign a name to each
	 */
	protected abstract void assignGeneratorNames(AbstractGeneratorInfo[] generators);
	
	/**
	 * given the list of generators and guesses (in the same ordering) return a GuessObject
	 * representing the information in those SingleGuess objects
	 * 
	 * @throws InterruptedException
	 */
	public abstract AbstractGuessObject makeGuessObject(AbstractGeneratorInfo[] gens, SingleGuess[] guesses) throws InterruptedException;
	
	/**
	 * @return a string representing correct guesses for output
	 */
	protected abstract String formatCorrectGuesses(); 
	

}
