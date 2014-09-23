package spytools.multi.execplan.custom;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.generators.AbstractGeneratorInfo;
import spytools.multi.execplan.AbstractExecutionPlan;
import spytools.multi.execplan.consumer.AbstractExecutionConsumer;
import spytools.multi.execplan.consumer.custom.HashCodeConsumer;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.storage.AbstractGuessObject;
import spytools.multi.storage.custom.UserPassStorage;

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
			return new UserPassStorage("","");
		}
		String user = getGuessByGeneratorName(this.userQueueName, guesses).toString();
		String pass = getGuessByGeneratorName(this.passQueueName, guesses).toString();
		return new UserPassStorage(user, pass);
	}
	

	@Override
	public String provideConsoleUpdate(AbstractGuessObject go){
		if(go instanceof UserPassStorage){
			return ((UserPassStorage)go).toString();
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
