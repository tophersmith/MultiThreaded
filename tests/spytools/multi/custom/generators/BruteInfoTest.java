package spytools.multi.custom.generators;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import spytools.multi.custom.setup.Setup;
import spytools.multi.generators.AbstractGeneratorInfo;

public class BruteInfoTest {

	@Test
	public void testSimpleBrute() {
		AbstractGeneratorInfo gi = Setup.setupDefaultBrute();
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
