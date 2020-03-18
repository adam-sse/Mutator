package net.ssehub.mutator.evaluation;

import java.util.Random;

import net.ssehub.mutator.BaseConfiguration;
import net.ssehub.mutator.mutation.IMutant;

public class DummyEvaluator extends Evaluator {
    
    private Random random = new Random(123);

    public DummyEvaluator(BaseConfiguration config) {
    }
    
    @Override
    public TestResult test(IMutant mutant) {
        if (mutant.getId().equals("G001_M001")) {
            return TestResult.PASS;
        }
        return random.nextDouble() <= 0.8 ? TestResult.PASS
                : TestResult.values()[random.nextInt(TestResult.values().length)];
    }

    @Override
    public double measureFitness(IMutant mutant) {
        return random.nextDouble() * 10;
    }

}
