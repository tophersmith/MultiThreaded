package spytools.multi.custom.execplan;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.execplan.ExecutionType.ExecutionConsumer;
import spytools.multi.custom.storage.GuessObject;
import spytools.multi.custom.storage.HashCodeStorage;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public class HashCode extends ExecutionType{
	private static final boolean stopOnFirst = false;
	private String userQueueName = "USER";
	private String passQueueName = "PASS";
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
		generatorQueues.put(this.userQueueName, new ArrayBlockingQueue<SingleGuess>(ExecutionType.MAX_GENERATOR_QUEUE_SIZE/generators));
		generatorQueues.put(this.passQueueName, new ArrayBlockingQueue<SingleGuess>(ExecutionType.MAX_GENERATOR_QUEUE_SIZE/generators));
	}

	@Override
	public void collectGuesses(Map<String, BlockingQueue<SingleGuess>> collectionQueue) {
		String u = "";
		String p = "";
		try {
			//Thread.sleep(5000);
			BlockingQueue<SingleGuess> user = collectionQueue.get(this.userQueueName);
			BlockingQueue<SingleGuess> pass = collectionQueue.get(this.passQueueName);
			
			while((u = user.take().toString()) != null && !this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD)){
				while((p = pass.take().toString())!= null && !this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD)){
					super.addGuessObject(new HashCodeStorage(u, p));
					this.log.debug(this.toString() + " put " + u + ":" + p);
				}
			}
		} catch (InterruptedException e) {
			if(!this.notifier.shouldHalt(ThreadType.CONSUMER_THREAD)){
				e.printStackTrace();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
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
				if (isGood)
					System.out.println();
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
