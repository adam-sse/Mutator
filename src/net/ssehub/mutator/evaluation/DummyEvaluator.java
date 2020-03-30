package net.ssehub.mutator.evaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.ssehub.mutator.BaseConfig;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.fitness.Fitness;

public class DummyEvaluator extends Evaluator {

    private Random random = new Random(123);

    private Map<String, TestResult> testResults;

    private Map<String, Fitness> fitnessResults;

    private int numObjectives;

    private double bias;

    public DummyEvaluator(BaseConfig config) {
        this.testResults = new HashMap<>();
        this.fitnessResults = new HashMap<>();

        if (config.getFitnessWeights() != null) {
            this.numObjectives = config.getFitnessWeights().length;
        } else {
            this.numObjectives = 2;
        }

        this.bias = 0.0;
    }

    @Override
    public TestResult test(IMutant mutant) {
        TestResult result = this.testResults.get(mutant.getId());

        if (result == null) {
            double rand = this.random.nextDouble();

            if (rand < 0.01) {
                result = TestResult.ERROR;
            } else if (rand < 0.05) {
                result = TestResult.TIMEOUT;
            } else if (rand < 0.10) {
                result = TestResult.COMPILATION_FAILED;
            } else if (rand < 0.2) {
                result = TestResult.TEST_FAILED;
            } else {
                result = TestResult.PASS;
            }

            this.testResults.put(mutant.getId(), result);
        }

        return result;
    }

    @Override
    public Fitness measureFitness(IMutant mutant) {
        Fitness result = this.fitnessResults.get(mutant.getId());

        if (result == null) {
            double[] values = new double[this.numObjectives];
            for (int i = 0; i < values.length; i++) {
                values[i] = this.random.nextDouble() * (1.0 + this.bias);
            }
            this.bias += 0.05;

            result = new Fitness(values);
        }

        return result;
    }

}
