package spytools.multi.custom.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class DictionaryInfo extends GeneratorInfo{
	private File dictionaryFile;
	private BufferedReader reader;
	private String currentGuess;
	
	public DictionaryInfo(File dict){
		this.dictionaryFile = dict;
		try {
			this.reader = new BufferedReader(new FileReader(this.dictionaryFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.currentGuess = null;
	}
	
	@Override
	public DictionaryInfo newInstance(){
		return new DictionaryInfo(this.dictionaryFile);
	}
	
	@Override 
	public int getNeededThreads(){
		return 1;
	}
	
	@Override
	public int getMaxThreads(int available){
		return 1;
	}
	
	@Override
	public String generateNextGuess() {
		try {
			this.currentGuess = this.reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//End of file reached, reset the reader but still return null to mark that the reader finished
		if(this.currentGuess == null){ 
			resetReader();
		}
		return this.currentGuess;
	}
	
	private void resetReader(){
		try {
			this.reader = new BufferedReader(new FileReader(this.dictionaryFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString(){
		return "DictionaryProducerThread-" + this.threadNum;
	}
}
