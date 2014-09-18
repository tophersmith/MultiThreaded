package spytools.multi.helpers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import spytools.multi.types.CharSetType;

public class Helpers {
	
	/**
	 * creates string representation of List char array
	 * @param array array of characters to become a string
	 * @return string
	 */
	public static String stringifyCharArray(List<Character> array) {
		StringBuilder sb = new StringBuilder();

		for (Character s : array) 
			sb.append(s);

		return sb.toString();
	}
	
	/**
	 * creates a character array from a char set and value such that
	 * 
	 * charset = "abc" value 0 = a, value 1 = a, value 2 = b, value 4 = aa etc
	 * 
	 * @param value the number representation of the array to create
	 * @param charSet the available characters to create the array from
	 * @return character list array of the complete character array
	 */
	public static List<Character> createCharArray(BigInteger value, char[] charSet) {
		List<Character> charArray = new ArrayList<Character>();
		StringBuilder sb = new StringBuilder();
		
		BigInteger csSize = BigInteger.valueOf(charSet.length);

		if (value.compareTo(BigInteger.ZERO) == 0){
			charArray.add(0, charSet[0]);
			sb.append(charSet[0]);
		}else {
			BigInteger[] divmod = value.divideAndRemainder(csSize);
			BigInteger modded = divmod[1];
			BigInteger digit = divmod[0];
			
			
			while (modded.compareTo(BigInteger.ZERO) != 0 || digit.compareTo(BigInteger.ZERO) != 0) {
				if (modded.compareTo(BigInteger.ZERO) == 0) {
					charArray.add(0, charSet[csSize.subtract(BigInteger.ONE).intValue()]);
					sb.append(charSet[csSize.subtract(BigInteger.ONE).intValue()]);
					value = value.subtract(BigInteger.ONE);
				} else{
					charArray.add(0, charSet[modded.subtract(BigInteger.ONE).intValue()]);
					sb.append(charSet[modded.subtract(BigInteger.ONE).intValue()]);
				}
				value = value.divide(csSize);
				divmod = value.divideAndRemainder(csSize);
				modded = divmod[1];
				digit = divmod[0];
			}
		}
		sb = sb.reverse();
		return charArray;
	}
	

	public static BigInteger findGuessValueFromLength(int length, char[] charSet){
		if(length <= 1)
			return BigInteger.ONE;

		int size = charSet.length;
		BigInteger guessValue = BigInteger.ONE;
		BigInteger v = BigInteger.valueOf(size);
		for(int i = 1; i < length; i++){
			guessValue = guessValue.add(v.pow(i));
		}
		return guessValue;
	}
	
	public static char[] generateCharSet(Object charSet){
		if(charSet instanceof String){
			return generateCharSets((String) charSet);
		}
		if(charSet instanceof CharSetType){
			return generateCharSets((CharSetType)charSet);
		}
		return generateCharSets((CharSetType[])charSet);
	}
	
	public static char[] generateCharSets(CharSetType... ts){
		List<Character> cl = new ArrayList<Character>();
		for(CharSetType t : ts){
			for(char c : t.getSet().toCharArray())
				cl.add(c);
		}
		char[] retc = new char[cl.size()];
		for (int i = 0; i < cl.size(); i++){
			retc[i] = cl.get(i);
		}
		return retc;
	}
	
	public static char[] generateCharSets(String s){
		List<Character> cl = new ArrayList<Character>();
		for(char c : s.toCharArray())
			cl.add(c);
		char[] retc = new char[cl.size()];
		for (int i = 0; i < cl.size(); i++){
			retc[i] = cl.get(i);
		}
		return retc;
	}
}
