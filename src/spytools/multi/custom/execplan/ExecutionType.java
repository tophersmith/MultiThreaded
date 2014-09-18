package spytools.multi.custom.execplan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.custom.storage.GuessObject;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.ThreadNotifier;

public abstract class ExecutionType {
	private final boolean stopOnFirstCorrect;
	protected Map<String, BlockingQueue<String>> generatorQueues;
	protected BlockingQueue<GuessObject> guessQueue;
	protected List<GuessObject> correctGuesses;
	protected GeneratorInfo[] generators;
	protected static final int MAX_GENERATOR_QUEUE_SIZE = 10000000;
	protected static final int MAX_GUESS_QUEUE_SIZE = 10000000;
	
	protected final Logger log = new Logger();
	protected ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	protected ExecutionType(boolean stopOnFirst){
		this.guessQueue = new ArrayBlockingQueue<GuessObject>(MAX_GUESS_QUEUE_SIZE);
		this.stopOnFirstCorrect = stopOnFirst;
	}

	public void addGenerators(GeneratorInfo... gen) {
		this.generators = gen;
		
		assignGeneratorNames();
	}

	public Map<String, BlockingQueue<String>> generateQueues(int generators) {
		if(this.generatorQueues == null){
			this.generatorQueues = new HashMap<String, BlockingQueue<String>>();
			this.generateQueuesByName(generators);
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
	protected abstract void assignGeneratorNames();
	public abstract void collectGuesses(Map<String, BlockingQueue<String>> queue);
	protected abstract void generateQueuesByName(int generators);
	public abstract boolean isCorrect(Object guess);
	protected abstract String formatCorrectGuesses(); 
	public abstract void reset();
	public abstract String provideConsoleUpdate(GuessObject go);

}
