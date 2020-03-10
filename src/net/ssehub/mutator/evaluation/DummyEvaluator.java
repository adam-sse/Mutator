package net.ssehub.mutator.evaluation;

import net.ssehub.mutator.Configuration;
import net.ssehub.mutator.mutation.Mutant;

public class DummyEvaluator extends Evaluator {

    public DummyEvaluator(Configuration config) {
    }
    
    @Override
    public TestResult test(Mutant mutant) {
        if (mutant.getId().equals("G001_M001")) {
            return TestResult.PASS;
        }
        return Math.random() <= 0.8 ? TestResult.PASS
                : TestResult.values()[(int) ((Math.random() * (TestResult.values().length - 1)) + 1)];
    }

    @Override
    public double measureFitness(Mutant mutant) {
        return Math.random() * 10;
    }

}
