package spytools.multi.generators;

import java.util.Arrays;

import spytools.multi.helpers.Helpers;
import spytools.multi.helpers.SetupException;

public class FastBruteGenerator extends AbstractGeneratorInfo {
	private final String startingGuess;
	private char[] currentGuess;
	private int maxGuessLength;
	private char[] charSet;
	
	public FastBruteGenerator(String startingGuess, int maxGuessLength, Object charSet) throws SetupException{
		this.charSet = Helpers.generateCharSet(charSet);
		this.maxGuessLength = maxGuessLength;
		this.startingGuess = startingGuess;
		if(this.maxGuessLength> this.startingGuess.length()){
			throw new SetupException("Max Guess Length Exceeded by starting guess length");
		}
	}
	
	public FastBruteGenerator(int min, int max, Object charSet){
		this.charSet = Helpers.generateCharSet(charSet);
		this.maxGuessLength = max;
		char[] guess = new char[min];
		Arrays.fill(guess, this.charSet[0]);
		this.startingGuess = new String(guess);
	}
	
	
	@Override
	protected void initializeInfo() {
		resetGuess();
	}

	private void resetGuess(){
		this.currentGuess = this.startingGuess.toCharArray();
		this.currentGuess = increaseGuessBy(this.currentGuess, this.threadNum);
	}
	
	@Override
	public int getNeededThreads() {
		return 1;
	}
	
	
	@Override
	public String generateNextGuess() {
		this.currentGuess = increaseGuessBy(this.currentGuess, this.allocatedThreads);
		if(this.currentGuess == null){
			resetGuess();
			return null;
		}
		return new String(this.currentGuess);
	}
	
	private char[] increaseGuessBy(char[] guess, int incCurrent){
		int carry = incCurrent; // add this to the next char to the left (the carry column)
		int charSetNum = 0; //location of a character in the charSet array
		int newCharValue = 0;
		
		int location = guess.length - 1; //iterate right to left lsB to msB
		while(location >= 0){
			charSetNum = findCharLocation(guess[location]);
			newCharValue = charSetNum + carry;

			carry = newCharValue / this.charSet.length;
			if(newCharValue < this.charSet.length){ //all done. no need to affect other values 
				guess[location] = this.charSet[newCharValue];
				break;
			}
			guess[location] = this.charSet[0];
			location--;
		}
		if(carry > 0){
			guess = increaseCharArray(guess, carry);
			if(guess.length > this.maxGuessLength)
				return null;
		}
		return guess;
	}
	
	private char[] increaseCharArray(char[] guess, int carry){
		int storedCarry = carry;
		int max = this.charSet.length;
		int numSpotsToIncrease = 0; //how many new array locations should be created
		while(carry >= max){//leave carry as a value between 0 and max-1 inclusive
			carry -= max;
			numSpotsToIncrease++;
		}
		numSpotsToIncrease++;
		char[] newGuessArray = new char[guess.length + numSpotsToIncrease];
		
		//copy old array values into new array values
		int oldCounter = guess.length - 1;
		int newCounter = newGuessArray.length - 1;
		while(oldCounter >= 0){
			newGuessArray[newCounter] = guess[oldCounter];
			newCounter--;
			oldCounter--;
		}
		
		//add new values
		while(numSpotsToIncrease > 1 && newCounter > 0){ //newCounter is being defensive
			newGuessArray[newCounter] = this.charSet[this.charSet.length];
			newCounter--;
		}
		if(newCounter == 0){
			newGuessArray[newCounter] = this.charSet[0];
		}else{
			this.log.error("newCounter went very wrong. It reports it has: " + newCounter + " remaining." +
					"\n increaseCharArray was called with guess="+ new String(guess) + " and a carry=" + storedCarry);
		}
		return newGuessArray;
	}
	
	private int findCharLocation(char c){
		for(int i = 0; i < this.charSet.length; i++){
			if(c == this.charSet[i])
				return i;
		}
		return -1;
	}

	@Override
	public String toString() {
		return "FastBruteProducerThread-" + this.threadNum + "-Queue-" + this.generatorQueueName;
	}
}
