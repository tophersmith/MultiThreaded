package spytools.multi.runner;


import static org.junit.Assert.assertTrue;

import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.custom.setup.Setup;
import spytools.multi.custom.storage.GuessObject;
import spytools.multi.helpers.SetupException;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.types.CharSetType;

public class ProducerManagementTest {
	private ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	@Test
	public void comboTest() throws SetupException, InterruptedException{
		ExecutionType t = Setup.setupExecType();
		GeneratorInfo gi = Setup.setupGenerator(0, 3, CharSetType.NUMBER);
		t.addGenerators(gi);
		ManagementThread mt = new ProducerManagement(t, 1, gi);
		mt.run();
		BlockingQueue<GuessObject> q = t.getGuessQueue();
		
		
		GeneratorInfo gi2 = Setup.setupGenerator(0, 3, CharSetType.NUMBER);
		String s, s2 = null;
		while(!q.isEmpty()){
			s = q.take().toString();
			s2 = gi2.generateNextGuess();
			assertTrue(s.equals(s2));
		}
	}
}
