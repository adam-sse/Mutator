package evaluation;

import main.Configuration;
import mutation.Mutant;

public class DummyEvaluator extends Evaluator {

    public DummyEvaluator(Configuration config) {
    }
    
    @Override
    public TestResult test(Mutant m) {
        if (m.getId().equals("G001_M001")) {
            return TestResult.PASS;
        }
        return Math.random() <= 0.8 ? TestResult.PASS
                : TestResult.values()[(int) ((Math.random() * (TestResult.values().length - 1)) + 1)];
    }

    @Override
    public double measureFitness(Mutant m) {
        return Math.random() * 10;
    }

}
