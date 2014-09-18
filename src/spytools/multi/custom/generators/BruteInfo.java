package spytools.multi.custom.generators;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import spytools.multi.helpers.Helpers;
import spytools.multi.helpers.SingleGuess;

public class BruteInfo extends GeneratorInfo {
	private BigInteger min;
	private BigInteger max;
	private char[] charSet; // Character Set
	
	private BigInteger currentGuessNum = null;
	private String currentGuess;
	
	public BruteInfo(int min, int max, Object charSet){
		this.charSet = Helpers.generateCharSet(charSet);
		this.min = Helpers.findGuessValueFromLength(min, this.charSet);
		this.max = Helpers.findGuessValueFromLength(max+1, this.charSet);
		resetCounter();
	}
	@Override
	public void init(int threadNum, int totalThreads, Map<String, BlockingQueue<SingleGuess>> collectionQueue) {
		super.init(threadNum, totalThreads, collectionQueue);
		this.min = this.min.add(this.bigThreadNum);
	}
	
	@Override 
	public int getNeededThreads(){
		return 1;
	}
	

	@Override
	public int getMaxThreads(int available){
		return available;
	}
	
	@Override
	public String generateNextGuess(){
		//reset if over, but return null to signify
		if (this.currentGuessNum.compareTo(this.max) >= 0){ //max is first guess of next highest length, so stop early
			resetCounter();
			return null; 
		}
		this.currentGuess = Helpers.stringifyCharArray(Helpers.createCharArray(this.currentGuessNum, this.charSet));
		this.currentGuessNum = this.currentGuessNum.add(this.bigTotalThreadNum);
		
		return this.currentGuess;
	}
	
	private void resetCounter(){
		this.currentGuessNum = this.min;
	}

	@Override
	public String toString(){
		return "BruteProducerThread-" + this.threadNum + "-Queue-" + this.generatorQueueName;
	}
}
