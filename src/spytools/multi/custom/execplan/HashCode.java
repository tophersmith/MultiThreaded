package spytools.multi.custom.execplan;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.custom.storage.GuessObject;
import spytools.multi.custom.storage.HashCodeStorage;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public class HashCode extends ExecutionType{
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
	protected void assignGeneratorNames(){
		this.generators[0].setGeneratorName(this.userQueueName);
		this.generators[1].setGeneratorName(this.passQueueName);
	}
	
	@Override
	protected void generateQueuesByName(Map<String, BlockingQueue<SingleGuess>> generatorQueues, int generators){
		this.userQ = new ArrayBlockingQueue<SingleGuess>(ExecutionType.MAX_GENERATOR_QUEUE_SIZE/generators);
		generatorQueues.put(this.userQueueName, this.userQ);
		this.passQ = new ArrayBlockingQueue<SingleGuess>(ExecutionType.MAX_GENERATOR_QUEUE_SIZE/generators);
		generatorQueues.put(this.passQueueName, this.passQ);
	}

	@Override
	public GuessObject makeGuessObject(GeneratorInfo[] gens, SingleGuess[] guesses) throws InterruptedException {
		if(!(gens.length == 2 && guesses.length == 2)){
			//TODO throw Exception
			System.out.println("FAIL");
			return new HashCodeStorage("","");
		}
		String user = getGuessByGeneratorName(this.userQueueName, guesses).toString();
		String pass = getGuessByGeneratorName(this.passQueueName, guesses).toString();
			
		return new HashCodeStorage(user, pass);
	}
	

	@Override
	public String provideConsoleUpdate(GuessObject go){
		if(go instanceof HashCodeStorage){
			return ((HashCodeStorage)go).toString();
		}
		return "";
	}
	
	@Override
	public String formatCorrectGuesses(){
		StringBuilder sb = new StringBuilder();
		for(GuessObject go : this.correctGuesses){
			sb.append(go.toString() + '\n');
		}
		return sb.toString();
	}
	
	@Override
	public ExecutionConsumer getConsumer(){
		return new HashCodeConsumer();
	}
	
	
	
	
	class HashCodeConsumer extends ExecutionConsumer{
		int target;
		
		HashCodeConsumer(){
			this.target = HashCode.this.target;
		}
		
		@Override
		public boolean isCorrect(Object guess){
			if(guess instanceof HashCodeStorage){
				boolean isGood = false;
				String user = ((HashCodeStorage) guess).getUser();
				String pass = ((HashCodeStorage) guess).getPass();
				
				int test = 0;
				test += getHash("UserIdentifier", user);
				test += getHash("Password", pass);
				isGood = test == this.target;
				
				test += getHash("Old password", "");
				isGood = (isGood) || (test == this.target);

				return isGood;
				
			}
			return false;
		}
		
		private int getHash(String key, String value){
			 return (key==null   ? 0 : key.hashCode()) ^
	                (value==null ? 0 : value.hashCode());
		}
	
		@Override
		public void reset() {
			//unnecessary. I clean up internally
		}
	}
}
