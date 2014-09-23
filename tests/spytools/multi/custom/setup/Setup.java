package spytools.multi.custom.setup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.execplan.AbstractExecutionPlan;
import spytools.multi.custom.execplan.consumer.AbstractExecutionConsumer;
import spytools.multi.custom.generators.AbstractGeneratorInfo;
import spytools.multi.custom.generators.BruteGenerator;
import spytools.multi.custom.generators.DictionaryGenerator;
import spytools.multi.custom.storage.AbstractGuessObject;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.types.CharSetType;

public class Setup {

	
	public static File makeFile(){
		File f = null;
		try {
		f = new File("..\\testfile.txt").getCanonicalFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
	public static void writeFile(File f, String append){
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(f));
			bw.write(append);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(bw != null)
					bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void removeFile(File f){
		if(f.exists())
			f.delete();
	}
	
	public static AbstractGeneratorInfo setupDefaultDictionary(){
		return setupDict(makeFile());
	}
	public static AbstractGeneratorInfo setupDict(File f){
		AbstractGeneratorInfo gi = new DictionaryGenerator(f);
		gi.setGeneratorName("TestGenerator");
		AbstractExecutionPlan t = setupExecType();
		gi.init(0, 1, t.generateQueues(1));		
		return gi;
	}
	
	public static AbstractGeneratorInfo setupDefaultBrute(){
		return setupBrute(0, 2, CharSetType.NUMBER);
	}
	public static AbstractGeneratorInfo setupBrute(int min, int max, Object charSet){
		AbstractGeneratorInfo gi = new BruteGenerator(min, max, charSet);
		gi.setGeneratorName("TestGenerator");
		AbstractExecutionPlan t = setupExecType();
		gi.init(0, 1, t.generateQueues(1));
		return gi;
	}
	
	public static AbstractExecutionPlan setupExecType(){
		return new TestType(true);
	}
	
	public static class TestType extends AbstractExecutionPlan{

		protected TestType(boolean stopOnFirst) {
			super(stopOnFirst);
		}

		@Override
		protected void assignGeneratorNames(AbstractGeneratorInfo[] gens) {
			gens[0].setGeneratorName("TestGenerator");
		}

		@Override
		public AbstractGuessObject makeGuessObject(AbstractGeneratorInfo[] gens, SingleGuess[] guesses) throws InterruptedException {
			return new TestObject(guesses[0].toString());
		}

		@Override
		protected void generateQueuesByName(Map<String, BlockingQueue<SingleGuess>> generatorQueues, int generators) {
			generatorQueues.put("TestGenerator", new ArrayBlockingQueue<SingleGuess>(500));
		}

		@Override
		protected String formatCorrectGuesses() {
			return null;
		}

		@Override
		public String provideConsoleUpdate(AbstractGuessObject go) {
			return null;
		}

		@Override
		public AbstractExecutionConsumer getConsumer() {
			return null;
		}
		
	}
	
	
	public static class TestObject extends AbstractGuessObject{
		String s;
		public TestObject(String s){
			this.s = s;
		}
		@Override
		public String toString() {
			return this.s;
		}
		
	}
}
