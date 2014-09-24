package spytools.multi.helpers;


/**
 * allows passing notifications of thread status.
 * 
 * @author Chris
 */
public class ThreadNotifier {
	private static ThreadNotifier inst = null;

	protected ThreadNotifier(){}
	public static ThreadNotifier getInstance() {
		if(inst == null){
			inst = new ThreadNotifier();
		}
		return inst;
	}

	public enum ThreadType{MAIN, CONSUMERS, PRODUCERS}

	private static enum ThreadInfo{
		MAIN(ThreadType.MAIN),
		CONS(ThreadType.CONSUMERS),
		PROS(ThreadType.PRODUCERS);
		private ThreadType type;
		private volatile boolean halt = false;
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
}
