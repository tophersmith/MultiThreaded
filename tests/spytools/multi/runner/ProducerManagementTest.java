package spytools.multi.runner;


import static org.junit.Assert.assertTrue;

import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import spytools.multi.custom.generators.AbstractGeneratorInfo;
import spytools.multi.custom.setup.Setup;
import spytools.multi.execplan.AbstractExecutionPlan;
import spytools.multi.helpers.SetupException;
import spytools.multi.storage.AbstractGuessObject;
import spytools.multi.types.CharSetType;

public class ProducerManagementTest {
	
	@Test
	public void comboTest() throws SetupException, InterruptedException{
		AbstractExecutionPlan t = Setup.setupExecType();
		AbstractGeneratorInfo gi = Setup.setupBrute(0, 3, CharSetType.NUMBER);
		t.addGenerators(gi);
		AbstractManagementThread mt = new ProducerManagement(t, 1, gi);
		mt.run();
		BlockingQueue<AbstractGuessObject> q = t.getGuessQueue();
		
		
		AbstractGeneratorInfo gi2 = Setup.setupBrute(0, 3, CharSetType.NUMBER);
		String s, s2 = null;
		while(!q.isEmpty()){
			s = q.take().toString();
			s2 = gi2.generateNextGuess();
			assertTrue(s.equals(s2));
		}
	}
}
