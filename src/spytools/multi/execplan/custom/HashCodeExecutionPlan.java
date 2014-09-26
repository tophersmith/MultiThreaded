package spytools.multi.execplan.custom;

import spytools.multi.execplan.AbstractExecutionPlan;
import spytools.multi.execplan.consumer.custom.HashCodeConsumer;
import spytools.multi.generators.AbstractGeneratorInfo;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.storage.AbstractGuessObject;
import spytools.multi.storage.custom.UserPassStorage;

/**
 * Simple contrived example to find an equivalent java hashcode as some input
 * 
 * @author smitc
 */
public class HashCodeExecutionPlan extends AbstractExecutionPlan{
	private static final boolean stopOnFirst = false;
	private String userQueueName = "USER";
	private String passQueueName = "PASS";
	
	public HashCodeExecutionPlan(int target){
		super(stopOnFirst, new HashCodeConsumer(target));
	}
	
	@Override
	protected void assignGeneratorNames(AbstractGeneratorInfo[] gens){
		gens[0].setGeneratorName(this.userQueueName);
		gens[1].setGeneratorName(this.passQueueName);
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
	public String formatCorrectGuesses(){
		StringBuilder sb = new StringBuilder();
		for(AbstractGuessObject go : this.correctGuesses){
			sb.append(go.toString() + '\n');
		}
		return sb.toString();
	}
}
