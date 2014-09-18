package spytools.multi.custom.execplan;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.custom.storage.GuessObject;
import spytools.multi.custom.storage.HashCodeStorage;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public class HashCode extends ExecutionType{
	private static final boolean stopOnFirst = true;
	private String userQueueName = "USER";
	private String passQueueName = "PASS";
	private int target = 0;
	private Map<String, String> actionMap;
	
	public HashCode(int target, GeneratorInfo... gen){
		super(stopOnFirst);
		this.target = target;
		this.actionMap = new HashMap<String, String>();
	}
	
	
	@Override
	protected void assignGeneratorNames(){
		this.generators[0].setGeneratorName(this.userQueueName);
		this.generators[1].setGeneratorName(this.passQueueName);
		
	}
	@Override
	protected void generateQueuesByName(int generators) {
		super.generatorQueues.put(this.userQueueName, new ArrayBlockingQueue<String>(ExecutionType.MAX_GENERATOR_QUEUE_SIZE/generators));
		super.generatorQueues.put(this.passQueueName, new ArrayBlockingQueue<String>(ExecutionType.MAX_GENERATOR_QUEUE_SIZE/generators));
	}

	@Override
	public void collectGuesses(Map<String, BlockingQueue<String>> queue) {
		try {
			String u, p;
			while((u = queue.get(this.userQueueName).poll()) != null && !this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD)){
				while((p = queue.get(this.passQueueName).poll())!= null && !this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD)){
					this.guessQueue.put(new HashCodeStorage(u, p));
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isCorrect(Object guess){
		if(guess instanceof HashCodeStorage){
			String user = ((HashCodeStorage) guess).getUser();
			String pass = ((HashCodeStorage) guess).getPass();
			this.actionMap.put("UserIdentifier", user);
			this.actionMap.put("Password", pass);
			int test1 = this.actionMap.hashCode();
			this.actionMap.put("Old password", "");
			int test2 = this.actionMap.hashCode();
			
			return (test1 == this.target) || (test2 == this.target);
			
		}
		System.err.println("Incorrect guess type");
		return false;
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
	public void reset(){
		this.actionMap.clear();
	}
}
