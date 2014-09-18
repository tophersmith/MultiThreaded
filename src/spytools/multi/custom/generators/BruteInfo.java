package spytools.multi.custom.generators;

import java.math.BigInteger;

import spytools.multi.helpers.Helpers;

public class BruteInfo extends GeneratorInfo {
	private final BigInteger min;
	private final BigInteger max;
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
