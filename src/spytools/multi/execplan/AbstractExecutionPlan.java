package spytools.multi.execplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.generators.AbstractGeneratorInfo;
import spytools.multi.execplan.consumer.AbstractExecutionConsumer;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.storage.AbstractGuessObject;

public abstract class AbstractExecutionPlan {
	private final boolean stopOnFirstCorrect;
	private Map<String, BlockingQueue<SingleGuess>> generatorQueues;
	private BlockingQueue<AbstractGuessObject> guessQueue;
	protected List<AbstractGuessObject> correctGuesses;
	protected AbstractGeneratorInfo[] generators;
	protected static final int MAX_GENERATOR_QUEUE_SIZE = 10000;
	protected static final int MAX_GUESS_QUEUE_SIZE = 10000;
	protected final Logger log = new Logger();
	protected ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	protected AbstractExecutionPlan(boolean stopOnFirst){
		this.guessQueue = new ArrayBlockingQueue<AbstractGuessObject>(MAX_GUESS_QUEUE_SIZE);
		this.stopOnFirstCorrect = stopOnFirst;
		this.correctGuesses = new ArrayList<AbstractGuessObject>();
	}

	public void addGenerators(AbstractGeneratorInfo... gen) {
		this.generators = gen;
		assignGeneratorNames(this.generators);
	}

	public Map<String, BlockingQueue<SingleGuess>> generateQueues(int generators) {
		if(this.generatorQueues == null){
			this.generatorQueues = new HashMap<String, BlockingQueue<SingleGuess>>();
			this.generateQueuesByName(this.generatorQueues, generators);
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

	public void clearCollector(Map<String, BlockingQueue<SingleGuess>> collectionQueue){
		for(String s : collectionQueue.keySet())
			collectionQueue.get(s).clear();
	}

	public void addGuessObject(AbstractGuessObject guess) throws InterruptedException {
		this.guessQueue.put(guess);
	}
	
	protected SingleGuess getGuessByGeneratorName(String generatorName, SingleGuess[] guesses){
		for(SingleGuess g : guesses){
			if(g.getGeneratorName().equals(generatorName)){
				return g;
			}
		}
		return null;
	}
	
	protected abstract void assignGeneratorNames(AbstractGeneratorInfo[] generators2);
	public abstract AbstractGuessObject makeGuessObject(AbstractGeneratorInfo[] gens, SingleGuess[] guesses) throws InterruptedException;
	protected abstract void generateQueuesByName(Map<String, BlockingQueue<SingleGuess>> generatorQueues, int generators);
	protected abstract String formatCorrectGuesses(); 
	public abstract String provideConsoleUpdate(AbstractGuessObject go);
	public abstract AbstractExecutionConsumer getConsumer();
	
	@Override
	public String toString(){
		return "Generator";
	}

}
