package spytools.multi.custom.execplan.consumer;

import spytools.multi.custom.storage.HashCodeStorage;

public class HashCodeConsumer extends AbstractExecutionConsumer{
	private int target;
	
	public HashCodeConsumer(int target){
		this.target = target;
	}
	
	@Override
	public boolean isCorrect(Object guess){
		if(guess instanceof HashCodeStorage){
			boolean isGood = false;
			String user = ((HashCodeStorage) guess).getUser();
			String pass = ((HashCodeStorage) guess).getPass();
			
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