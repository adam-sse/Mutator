package net.ssehub.mutator.evaluation;

import java.util.Random;

import net.ssehub.mutator.BaseConfig;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.fitness.Fitness;

public class DummyEvaluator extends Evaluator {
    
    private Random random = new Random(123);

    public DummyEvaluator(BaseConfig config) {
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
    public Fitness measureFitness(IMutant mutant) {
        return new Fitness(random.nextDouble() * 10, random.nextDouble() * 10);
    }

}
