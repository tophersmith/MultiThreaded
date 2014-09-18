package spytools.multi.main;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.custom.execplan.HashCode;
import spytools.multi.custom.generators.BruteInfo;
import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.helpers.SetupException;
import spytools.multi.runner.MultiThreadExec;
import spytools.multi.types.CharSetType;

public class Main {

	
	public static void main(String[] s) throws SetupException{
		GeneratorInfo b1 = new BruteInfo(3, 5, "123");
		GeneratorInfo b2 = new BruteInfo(3, 5, "123");
		ExecutionType t = new HashCode(1316888554);
		t.addGenerators(b1, b2);
		MultiThreadExec mte = new MultiThreadExec(t, 6);
		mte.execute();
	}
	//1316888554 smitc/rules
	//144401446  admin@RTD12/rules
}
