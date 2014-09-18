package spytools.multi.custom.storage;

public class HashCodeStorage extends GuessObject{
	private String user;
	private String pass;
	
	public HashCodeStorage(String u, String p){
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
