package spytools.multi.custom.execplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.custom.storage.GuessObject;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier;

public abstract class ExecutionType {
	private final boolean stopOnFirstCorrect;
	private Map<String, BlockingQueue<SingleGuess>> generatorQueues;
	private BlockingQueue<GuessObject> guessQueue;
	protected List<GuessObject> correctGuesses;
	protected GeneratorInfo[] generators;
	protected static final int MAX_GENERATOR_QUEUE_SIZE = 10000;
	protected static final int MAX_GUESS_QUEUE_SIZE = 10000;
	protected final Logger log = new Logger();
	protected ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	protected ExecutionType(boolean stopOnFirst){
		this.guessQueue = new ArrayBlockingQueue<GuessObject>(MAX_GUESS_QUEUE_SIZE);
		this.stopOnFirstCorrect = stopOnFirst;
		this.correctGuesses = new ArrayList<GuessObject>();
	}

	public void addGenerators(GeneratorInfo... gen) {
		this.generators = gen;
		assignGeneratorNames();
	}

	public Map<String, BlockingQueue<SingleGuess>> generateQueues(int generators) {
		if(this.generatorQueues == null){
			this.generatorQueues = new HashMap<String, BlockingQueue<SingleGuess>>();
			this.generateQueuesByName(this.generatorQueues, generators);
		}
		return this.generatorQueues;
	}
	
	public BlockingQueue<GuessObject> getGuessQueue() {
		return this.guessQueue;
	}
	
	public void storeCorrectGuess(GuessObject go){
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
	
	public GeneratorInfo[] getGenerators() {
		return this.generators;
	}

	public void clearCollector(Map<String, BlockingQueue<SingleGuess>> collectionQueue){
		for(String s : collectionQueue.keySet())
			collectionQueue.get(s).clear();
	}

	public void addGuessObject(GuessObject guess) throws InterruptedException {
		this.guessQueue.put(guess);
	}
	protected abstract void assignGeneratorNames();
	public abstract GuessObject makeGuessObject(GeneratorInfo... gens) throws InterruptedException;
	protected abstract void generateQueuesByName(Map<String, BlockingQueue<SingleGuess>> generatorQueues, int generators);
	protected abstract String formatCorrectGuesses(); 
	public abstract String provideConsoleUpdate(GuessObject go);
	public abstract ExecutionConsumer getConsumer();
	
	public abstract class ExecutionConsumer{
		public abstract boolean isCorrect(Object guess);
		public abstract void reset();
	}

	@Override
	public String toString(){
		return "Generator";
	}

}
