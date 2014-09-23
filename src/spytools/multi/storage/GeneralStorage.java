package spytools.multi.storage;

/**
 * GeneralStorage is an entirely generalized Storage Object 
 * This is a good model of what a custom Storage Object should look like
 * 
 * @author smitc
 */
public class GeneralStorage extends AbstractGuessObject{
	
	private final Object[] objects;
	private final int size;

	public GeneralStorage(Object... obj){
		this.objects = obj;
		this.size = obj.length;
	}
	
	public Object getObject(int index) {
		if(index < this.size)
			return this.objects[index];
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while(i < this.size){
			sb.append("Object " + i + ": " + this.objects[i].toString() + "   ");
			i++;
		}
		return sb.toString();
	}
}
