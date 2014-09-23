package spytools.multi.custom.execplan;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.execplan.consumer.AbstractExecutionConsumer;
import spytools.multi.custom.execplan.consumer.HashCodeConsumer;
import spytools.multi.custom.generators.AbstractGeneratorInfo;
import spytools.multi.custom.storage.AbstractGuessObject;
import spytools.multi.custom.storage.HashCodeStorage;
import spytools.multi.helpers.SingleGuess;

public class HashCode extends AbstractExecutionPlan{
	private static final boolean stopOnFirst = false;
	private String userQueueName = "USER";
	private String passQueueName = "PASS";
	private BlockingQueue<SingleGuess> userQ;
	private BlockingQueue<SingleGuess> passQ;
	final int target;
	
	public HashCode(int target){
		super(stopOnFirst);
		this.target = target;
	}
	
	@Override
	protected void assignGeneratorNames(AbstractGeneratorInfo[] gens){
		gens[0].setGeneratorName(this.userQueueName);
		gens[1].setGeneratorName(this.passQueueName);
	}
	
	@Override
	protected void generateQueuesByName(Map<String, BlockingQueue<SingleGuess>> generatorQueues, int generators){
		this.userQ = new ArrayBlockingQueue<SingleGuess>(AbstractExecutionPlan.MAX_GENERATOR_QUEUE_SIZE/generators);
		generatorQueues.put(this.userQueueName, this.userQ);
		this.passQ = new ArrayBlockingQueue<SingleGuess>(AbstractExecutionPlan.MAX_GENERATOR_QUEUE_SIZE/generators);
		generatorQueues.put(this.passQueueName, this.passQ);
	}

	@Override
	public AbstractGuessObject makeGuessObject(AbstractGeneratorInfo[] gens, SingleGuess[] guesses) throws InterruptedException {
		if(!(gens.length == 2 && guesses.length == 2)){
			//TODO throw Exception
			System.out.println("FAIL");
			return new HashCodeStorage("","");
		}
		String user = getGuessByGeneratorName(this.userQueueName, guesses).toString();
		String pass = getGuessByGeneratorName(this.passQueueName, guesses).toString();
		System.out.println(user + ":" + pass);
		return new HashCodeStorage(user, pass);
	}
	

	@Override
	public String provideConsoleUpdate(AbstractGuessObject go){
		if(go instanceof HashCodeStorage){
			return ((HashCodeStorage)go).toString();
		}
		return "";
	}
	
	@Override
	public String formatCorrectGuesses(){
		StringBuilder sb = new StringBuilder();
		for(AbstractGuessObject go : this.correctGuesses){
			sb.append(go.toString() + '\n');
		}
		return sb.toString();
	}
	
	@Override
	public AbstractExecutionConsumer getConsumer(){
		return new HashCodeConsumer(this.target);
	}
}
