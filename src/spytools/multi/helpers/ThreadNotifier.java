package spytools.multi.helpers;

public class ThreadNotifier {
	private static ThreadNotifier inst = null;

	protected ThreadNotifier(){}
	public static ThreadNotifier getInstance() {
		if(inst == null){
			inst = new ThreadNotifier();
		}
		return inst;
	}

	public enum ThreadType{MAIN, CONSUMER_MANAGEMENT, PRODUCER_MANAGEMENT, CONSUMER_THREAD, PRODUCER_THREAD}

	private static enum ThreadInfo{
		MAIN(ThreadType.MAIN),
		CON_MGT(ThreadType.CONSUMER_MANAGEMENT),
		PRO_MGT(ThreadType.PRODUCER_MANAGEMENT),
		CON_THD(ThreadType.CONSUMER_THREAD),
		PRO_THD(ThreadType.PRODUCER_THREAD);
		private ThreadType type;
		private volatile boolean halt = false;
		private volatile boolean done = false;
		ThreadInfo(ThreadType t){
			this.type = t;
		}
		public ThreadType getType(){
			return this.type;
		}
		public boolean shouldHalt() {
			return this.halt;
		}
		public void halt(){
			this.halt = true;
		}
		public boolean isDone(){
			return this.done;
		}
		public void done(){
			this.done = true;
		}
	}

	private static ThreadInfo getInfo(ThreadType t){
		for(ThreadInfo i : ThreadInfo.values()){
			if(t.equals(i.getType()))
				return i;
		}
		return null; //TODO THROW
	}

	public boolean shouldHalt(ThreadType t) {
		return getInfo(t).shouldHalt();
	}

	public void haltAll(){
		for(ThreadType i : ThreadType.values()){
			haltThread(i);
		}
	}

	public void haltThread(ThreadType t){
		getInfo(t).halt();
	}
	
	public boolean isDone(ThreadType t){
		return getInfo(t).isDone();
	}
	
	public void setDone(ThreadType t){
		getInfo(t).done();
	}
}
