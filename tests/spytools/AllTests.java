package spytools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import spytools.multi.custom.generators.GeneratorsTestSuite;
import spytools.multi.helpers.HelpersSuite;
import spytools.multi.runner.RunnerSuite;

@RunWith(Suite.class)
@SuiteClasses({GeneratorsTestSuite.class, HelpersSuite.class, RunnerSuite.class})
public class AllTests {
//runs all testsuites
}
