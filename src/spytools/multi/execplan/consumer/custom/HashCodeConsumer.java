package spytools.multi.execplan.consumer.custom;

import spytools.multi.execplan.consumer.AbstractExecutionConsumer;
import spytools.multi.storage.custom.UserPassStorage;


public class HashCodeConsumer extends AbstractExecutionConsumer{
	private int target;
	
	public HashCodeConsumer(int target){
		this.target = target;
	}
	
	@Override
	public boolean isCorrect(Object guess){
		if(guess instanceof UserPassStorage){
			boolean isGood = false;
			String user = ((UserPassStorage) guess).getUser();
			String pass = ((UserPassStorage) guess).getPass();
			
			int test = 0;
			test += getHash("UserIdentifier", user);
			test += getHash("Password", pass);
			isGood = test == this.target;
			
			test += getHash("Old password", "");
			isGood = (isGood) || (test == this.target);

			return isGood;
			
		}
		return false;
	}
	
	private int getHash(String key, String value){
		 return (key==null   ? 0 : key.hashCode()) ^
                (value==null ? 0 : value.hashCode());
	}

	@Override
	public void reset() {
		//unnecessary. I clean up internally
	}
}