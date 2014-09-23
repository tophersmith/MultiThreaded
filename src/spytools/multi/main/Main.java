package spytools.multi.main;

import spytools.multi.custom.execplan.AbstractExecutionPlan;
import spytools.multi.custom.execplan.HashCode;
import spytools.multi.custom.generators.AbstractGeneratorInfo;
import spytools.multi.custom.generators.BruteGenerator;
import spytools.multi.helpers.SetupException;
import spytools.multi.runner.MultiThreadExec;
import spytools.multi.types.CharSetType;

public class Main {

	
	public static void main(String[] s) throws SetupException{
		AbstractGeneratorInfo b1 = new BruteGenerator(1, 1, CharSetType.NUMBER);
		AbstractGeneratorInfo b2 = new BruteGenerator(1, 1, CharSetType.NUMBER);
		//GeneratorInfo b1 = new BruteInfo(1, 5, "smitc");
		//GeneratorInfo b2 = new BruteInfo(1, 5, "rules");
		AbstractExecutionPlan t = new HashCode(1316888554);
		MultiThreadExec mte = new MultiThreadExec(t, b1, b2);
		mte.execute();
	}
	//1316888554 smitc/rules
	//144401446  admin@RTD12/rules
}
