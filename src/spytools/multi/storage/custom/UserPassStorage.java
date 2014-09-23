package spytools.multi.storage.custom;

import spytools.multi.storage.AbstractGuessObject;

/**
 * UserPassStorage holds a general two string storage object used
 * for usernames and passwords
 * 
 * @author smitc
 */
public class UserPassStorage extends AbstractGuessObject{
	private String user;
	private String pass;
	
	public UserPassStorage(String u, String p){
		this.user = u;
		this.pass = p;
	}
	public String getUser() {
		return this.user;
	}
	public String getPass() {
		return this.pass;
	}
	
	@Override
	public String toString() {
		return "User: " + this.user + " Pass: " + this.pass;
	}
}
