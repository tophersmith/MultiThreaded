package spytools.multi.execplan.consumer;

import spytools.multi.storage.AbstractGuessObject;

/**
 * lightweight Consumer object used to judge guess correctness
 * 
 * @author smitc
 */
public abstract class AbstractExecutionConsumer{
	
	/**
	 * create a new exact copy of this consumer for use in other threads
	 * 
	 * @return new duplicate instance of this object 
	 */
	public abstract AbstractExecutionConsumer duplicate();
	
	/**
	 * Judges whether a guess is correct or not
	 * 
	 * @param guess single GuessObject 
	 * @return true if correct, false otherwise (including any errors found)
	 */
	public abstract boolean isCorrect(AbstractGuessObject guess);
	
	/**
	 * reset this object back to a state where a new guess won't interact with an old guess
	 */
	public abstract void reset();
}
