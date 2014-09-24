package spytools.multi.helpers;


/**
 * Contains a single object created by a Generator
 *  
 * @author Chris
 */
public class SingleGuess {
	private Object guess;
	private String generatedBy;
	public SingleGuess(Object guess, String generatedBy){
		this.guess = guess;
		this.generatedBy = generatedBy;
	}
	public String getGeneratorName(){
		return this.generatedBy;
	}
	@Override
	public String toString(){
		return this.guess == null ? null : this.guess.toString();
	}
}
