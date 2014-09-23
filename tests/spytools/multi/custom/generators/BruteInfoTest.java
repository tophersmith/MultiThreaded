package spytools.multi.custom.generators;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import spytools.multi.custom.setup.Setup;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.types.CharSetType;

public class BruteInfoTest {

	@Test
	public void testSimpleBrute() {
		GeneratorInfo gi = Setup.setupDefaultGenerator();
		StringBuilder sb = new StringBuilder();
		String s = null;
		for(int i = 0; i < 100; i++){
			s = gi.generateNextGuess();
			sb.append(s);
		}
		assertTrue("89".equals(s));
		assertTrue(sb.length() == 190);
	}

}
