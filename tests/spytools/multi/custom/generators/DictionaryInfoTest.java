package spytools.multi.custom.generators;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import spytools.multi.custom.setup.Setup;
import spytools.multi.generators.AbstractGeneratorInfo;

public class DictionaryInfoTest {

	@Test
	public void testGeneration() {
		File f = Setup.makeFile();
		StringBuilder sb = new StringBuilder();
		AbstractGeneratorInfo gi = Setup.setupDefaultBrute();
		for(int i = 0; i < 100; i++){
			sb.append(gi.generateNextGuess());
			sb.append("\r\n");
		}
		Setup.writeFile(f, sb.toString());
		
		//reset gi
		gi = Setup.setupDefaultBrute();
		AbstractGeneratorInfo gi2 = Setup.setupDefaultDictionary();
		
		String a, b = "";
		for(int i = 0; i < 100; i++){
			a = gi.generateNextGuess();
			b = gi2.generateNextGuess();
			assertTrue(a.equals(b));
		}
		Setup.removeFile(f);
	}

}
