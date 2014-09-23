package spytools.multi.custom.execplan.consumer;

public abstract class AbstractExecutionConsumer{
	public abstract boolean isCorrect(Object guess);
	public abstract void reset();
}
