package net.ssehub.mutator.mutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.ssehub.mutator.evaluation.Evaluator;
import net.ssehub.mutator.evaluation.TestResult;

public abstract class AbstractMutator implements IMutator {

    private Map<String, Double> fitnessStore;
    
    private int statNumEvaluated;
    private int statNumCompileError;
    private int statNumTimeout;
    private int statNumFailed;
    private int statNumRuntimeError;
    private int statNumError;
    private List<Double> statBestInIteration;
    
    public AbstractMutator() {
        this.fitnessStore = new HashMap<>();
        this.statBestInIteration = new ArrayList<>();
    }
    
    @Override
    public Double getFitness(String mutantId) {
        return this.fitnessStore.get(mutantId);
    }
    
    protected void setFitness(String mutantId, double fitness) {
        this.fitnessStore.put(mutantId, fitness);
    }
    
    protected boolean hasFitness(String mutantId) {
        return getFitness(mutantId) != null;
    }
    
    protected Double evaluate(IMutant mutant, Evaluator evaluator, boolean useCache) {
        Double fitness = null;
        
        if (useCache && hasFitness(mutant.getId())) {
            fitness = getFitness(mutant.getId());
            System.out.println(mutant.getId() + ": " + fitness + " (cached)");
            return fitness;
        }
        
        statNumEvaluated++;
        
        TestResult testResult = evaluator.test(mutant);
        if (testResult != TestResult.PASS) {
            System.out.println(mutant.getId() + " " + testResult);
            
            switch (testResult) {
            case COMPILATION_FAILED:
                statNumCompileError++;
                break;
            case ERROR:
                statNumError++;
                break;
            case TEST_FAILED:
                statNumFailed++;
                break;
            case TIMEOUT:
                statNumTimeout++;
                break;
            default:
            }
            
        } else {
            fitness = evaluator.measureFitness(mutant);
            if (fitness == Evaluator.RUNTIME_ERROR) {
                System.out.println(mutant.getId() + " had a runtime error during fitness evaluation");
                statNumRuntimeError++;
                fitness = null;
                
            } else {
                System.out.println(mutant.getId() + ": " + fitness);
                setFitness(mutant.getId(), fitness);
            }
        }
        
        return fitness;
    }
    
    protected void setBestInIteration(int iteration, IMutant bestMutant) {
        setBestInIteration(iteration, getFitness(bestMutant.getId()));
    }
    
    protected void setBestInIteration(int iteration, double bestFitness) {
        if (iteration - 1 < this.statBestInIteration.size()) {
            this.statBestInIteration.set(iteration - 1, bestFitness);
        } else {
            this.statBestInIteration.add(iteration - 1, bestFitness);
        }
    }
    
    @Override
    public void printStatistics() {
        System.out.println("Evaluated: " + statNumEvaluated);
        System.out.printf(Locale.ROOT, "    failed compilation: %d (%.2f %%)", statNumCompileError,
                (double) statNumCompileError / statNumEvaluated * 100.0);
        System.out.println();
        System.out.printf(Locale.ROOT, "    timed-out: %d (%.2f %%)", statNumTimeout,
                (double) statNumTimeout / statNumEvaluated * 100.0);
        System.out.println();
        System.out.printf(Locale.ROOT, "    failed tests: %d (%.2f %%)", statNumFailed,
                (double) statNumFailed / statNumEvaluated * 100.0);
        System.out.println();
        System.out.printf(Locale.ROOT, "    runtime error: %d (%.2f %%)", statNumRuntimeError,
                (double) statNumRuntimeError / statNumEvaluated * 100.0);
        System.out.println();
        System.out.printf(Locale.ROOT, "    error: %d (%.2f %%)", statNumError,
                (double) statNumError / statNumEvaluated * 100.0);
        System.out.println();

        
        if (statBestInIteration.size() >= 2) {
            System.out.println();
            System.out.println("Best Fitness per Iteration:");
            
            double max = Collections.max(statBestInIteration);
            double min = Collections.min(statBestInIteration);
            
            final int NUM_LINES = 20;
            double range = (max - min) / NUM_LINES;
            for (int line = 0; line < NUM_LINES; line++) {
                double upper = max - (line * range);
                double lower = upper - range;
                
                System.out.printf(Locale.ROOT, "%10.2f |", (upper + lower) / 2);
                
                for (int iteration = 0; iteration < statBestInIteration.size(); iteration++) {
                    double fitness = statBestInIteration.get(iteration);
                    if (fitness <= upper  && fitness >= lower) {
                        System.out.print(" *  ");
                    } else {
                        System.out.print("    ");
                    }
                }
                
                System.out.println();
            }
            System.out.println("-----------+" + "----".repeat(statBestInIteration.size()));
            System.out.print("           |");
            for (int iteration = 0; iteration < statBestInIteration.size(); iteration++) {
                System.out.printf(Locale.ROOT, "%03d ", iteration + 1);
            }
            System.out.println();
        }
    }
    
}
