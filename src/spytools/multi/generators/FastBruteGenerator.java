package spytools.multi.generators;

import spytools.multi.helpers.Helpers;

public class FastBruteGenerator extends AbstractGeneratorInfo {
	private final String startingGuess;
	private char[] currentGuess;
	private int maxGuessLength;
	private char[] charSet;

	public FastBruteGenerator(String startingGuess, int maxGuessLength, Object charSet){
		this.charSet = Helpers.generateCharSet(charSet);
		this.maxGuessLength = maxGuessLength;
		this.startingGuess = startingGuess;
		resetGuess();
	}
	
	@Override
	protected void initializeInfo() {
		// TODO Auto-generated method stub

	}

	private void resetGuess(){
		this.currentGuess = this.startingGuess.toCharArray();
	}
	
	@Override
	public int getNeededThreads() {
		return 1;
	}
	
	
	@Override
	public String generateNextGuess() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "FastBruteProducerThread-" + this.threadNum + "-Queue-" + this.generatorQueueName;
	}

}
