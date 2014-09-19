package spytools.multi.runner;

public abstract class ManagementThread implements Runnable{

	abstract void notifyDone();
	abstract void shutdown();
}
