package spytools.multi.types;

/**
 * Manages standard english character sets for generating brute force character sets
 * 
 * @author smitc
 */
public enum CharSetType{
	LOWER("abcdefghijklmnopqrstuvwxyz"), 
	UPPER("ABCDEFGHIJKLMNOPQRSTUVWXYZ"), 
	NUMBER("1234567890"), 
	SYMBOL("`~!@#$%^&*()_+-={}[]|\\:;'\",./<>?");
	private final String set;
	private CharSetType(String s){
		this.set = s;
	}
	public String getSet(){
		return this.set;
	}
}
