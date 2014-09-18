package spytools.multi.types;

public enum CharSetType{
	LOWER("abcdefghijklmnopqrstuvwxyz"), 
	UPPER("ABCDEFGHIJKLMNOPQRSTUVWXYZ"), 
	NUMBER("1234567890"), 
	SYMBOL("`~!@#$%^&*()_+-={}[]|\\:;'\",./<>?");
	private final String set;
	CharSetType(String s){
		this.set = s;
	}
	public String getSet(){
		return this.set;
	}
}
