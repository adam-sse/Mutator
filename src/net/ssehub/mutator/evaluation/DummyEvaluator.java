package net.ssehub.mutator.evaluation;

import java.util.Random;

import net.ssehub.mutator.Configuration;
import net.ssehub.mutator.mutation.Mutant;

public class DummyEvaluator extends Evaluator {
    
    private Random random = new Random(123);

    public DummyEvaluator(Configuration config) {
    }
    
    @Override
    public TestResult test(Mutant mutant) {
        if (mutant.getId().equals("G001_M001")) {
            return TestResult.PASS;
        }
        return random.nextDouble() <= 0.8 ? TestResult.PASS
                : TestResult.values()[random.nextInt(TestResult.values().length)];
    }

    @Override
    public double measureFitness(Mutant mutant) {
        return random.nextDouble() * 10;
    }

}
