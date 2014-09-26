package spytools.multi.main;

import java.io.File;

import spytools.multi.execplan.AbstractExecutionPlan;
import spytools.multi.execplan.custom.HashCodeExecutionPlan;
import spytools.multi.generators.AbstractGeneratorInfo;
import spytools.multi.generators.BruteGenerator;
import spytools.multi.generators.DictionaryGenerator;
import spytools.multi.helpers.SetupException;
import spytools.multi.runner.MultiThreadExec;
import spytools.multi.types.CharSetType;

public class Main {

	
	public static void main(String[] s) throws SetupException{
		AbstractGeneratorInfo b1 = new DictionaryGenerator(new File("C:/Users/smitc/Desktop/names.txt"));
		AbstractGeneratorInfo b2 = new BruteGenerator(1, 5, CharSetType.LOWER);
		AbstractExecutionPlan t = new HashCodeExecutionPlan(1316888554);
		MultiThreadExec mte = new MultiThreadExec(t, b1, b2);
		mte.execute();
	}
}
