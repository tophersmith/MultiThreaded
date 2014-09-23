package spytools.multi.custom.setup;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.custom.generators.BruteInfo;
import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.custom.storage.GuessObject;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.types.CharSetType;

public class Setup {

	public static GeneratorInfo setupDefaultGenerator(){
		return setupGenerator(0, 2, CharSetType.NUMBER);
	}
	public static GeneratorInfo setupGenerator(int min, int max, Object charSet){
		GeneratorInfo gi = new BruteInfo(min, max, charSet);
		gi.setGeneratorName("TestGenerator");
		ExecutionType t = setupExecType();
		gi.init(0, 1, t.generateQueues(1));
		return gi;
	}
	
	public static ExecutionType setupExecType(){
		return new TestType(true);
	}
	
	public static class TestType extends ExecutionType{

		protected TestType(boolean stopOnFirst) {
			super(stopOnFirst);
		}

		@Override
		protected void assignGeneratorNames(GeneratorInfo[] gens) {
			gens[0].setGeneratorName("TestGenerator");
		}

		@Override
		public GuessObject makeGuessObject(GeneratorInfo[] gens, SingleGuess[] guesses) throws InterruptedException {
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
		public String provideConsoleUpdate(GuessObject go) {
			return null;
		}

		@Override
		public ExecutionConsumer getConsumer() {
			return null;
		}
		
	}
	
	
	public static class TestObject extends GuessObject{
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
