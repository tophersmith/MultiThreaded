package spytools.multi.custom.generators;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import spytools.multi.helpers.Helpers;
import spytools.multi.helpers.SingleGuess;

public class BruteInfo extends GeneratorInfo {
	private final int minLength;
	private final BigInteger min;
	private final int maxLength;
	private final BigInteger max;
	private char[] charSet; // Character Set
	
	private BigInteger currentGuessNum = null;
	private String currentGuess;
	
	public BruteInfo(int min, int max, Object charSet){
		this.charSet = Helpers.generateCharSet(charSet);
		this.minLength = min;
		this.min = Helpers.findGuessValueFromLength(this.minLength, this.charSet);
		this.maxLength = max;
		this.max = Helpers.findGuessValueFromLength(this.maxLength+1, this.charSet);
	}
	
	@Override
	protected void initializeInfo() {
		resetCounter();
	}
	
	@Override 
	public int getNeededThreads(){
		return 1;
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
		this.currentGuessNum = this.min.add(this.bigThreadNum);
	}

	@Override
	public String toString(){
		return "BruteProducerThread-" + this.threadNum + "-Queue-" + this.generatorQueueName;
	}
}
