package spytools.multi.helpers;

public class SingleGuess {
	private String guess;
	public SingleGuess(String guess){
		this.guess = guess;
	}
	@Override
	public String toString(){
		return this.guess;
	}
}
