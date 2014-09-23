package spytools.multi.runner;

public abstract class AbstractManagementThread implements Runnable{

	abstract void notifyDone();
	abstract void shutdown();
}
