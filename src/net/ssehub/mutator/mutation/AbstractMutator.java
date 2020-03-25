package net.ssehub.mutator.mutation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ssehub.mutator.BaseConfig;
import net.ssehub.mutator.evaluation.Evaluator;
import net.ssehub.mutator.evaluation.EvaluatorFactory;
import net.ssehub.mutator.evaluation.TestResult;
import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.util.Logger;
import net.ssehub.mutator.visualization.BestFitnessRenderer;
import net.ssehub.mutator.visualization.BestFitnessRenderer3D;

public abstract class AbstractMutator implements IMutator {

    private static final Logger LOGGER = Logger.get(AbstractMutator.class.getSimpleName());
    
    private Map<String, Fitness> fitnessStore;
    
    private BaseConfig config;
    
    private Evaluator evaluator;
    
    private int statNumEvaluated;
    private int statNumCompileError;
    private int statNumTimeout;
    private int statNumFailed;
    private int statNumRuntimeError;
    private int statNumError;
    private List<Fitness> statBestInIteration;
    
    public AbstractMutator(BaseConfig config) {
        this.config = config;
        this.evaluator = EvaluatorFactory.create(config);
        this.fitnessStore = new HashMap<>();
        this.statBestInIteration = new ArrayList<>();
    }
    
    @Override
    public Fitness getFitness(String mutantId) {
        return this.fitnessStore.get(mutantId);
    }
    
    protected void setFitness(String mutantId, Fitness fitness) {
        this.fitnessStore.put(mutantId, fitness);
    }
    
    protected boolean hasFitness(String mutantId) {
        return getFitness(mutantId) != null;
    }
    
    protected Fitness evaluate(IMutant mutant, boolean useCache, boolean printAndStats) {
        Fitness fitness = null;
        
        if (useCache && hasFitness(mutant.getId())) {
            fitness = getFitness(mutant.getId());
            if (printAndStats) {
                LOGGER.println(mutant.getId() + ": " + fitness + " (cached)");
            }
            return fitness;
        }

        if (printAndStats) {
            statNumEvaluated++;
        }
        
        TestResult testResult = this.evaluator.test(mutant);
        if (testResult != TestResult.PASS) {
            if (printAndStats) {
                LOGGER.println(mutant.getId() + " " + testResult);
            
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
            }
            
        } else {
            fitness = this.evaluator.measureFitness(mutant);
            if (fitness == Evaluator.RUNTIME_ERROR) {
                if (printAndStats) {
                    LOGGER.println(mutant.getId() + " had a runtime error during fitness evaluation");
                    statNumRuntimeError++;
                }
                fitness = null;
                
            } else {
                if (printAndStats) {
                    LOGGER.println(mutant.getId() + ": " + fitness);
                }
                setFitness(mutant.getId(), fitness);
            }
        }
        
        return fitness;
    }
    
    protected void setBestInIteration(int iteration, IMutant bestMutant) {
        setBestInIteration(iteration, getFitness(bestMutant.getId()));
    }
    
    protected void setBestInIteration(int iteration, Fitness bestFitness) {
        if (iteration - 1 < this.statBestInIteration.size()) {
            this.statBestInIteration.set(iteration - 1, bestFitness);
        } else {
            this.statBestInIteration.add(iteration - 1, bestFitness);
        }
    }
    
    @Override
    public void printStatistics() {
        LOGGER.println("Evaluated: " + statNumEvaluated);
        LOGGER.printf("    failed compilation: %d (%.2f %%)", statNumCompileError,
                (double) statNumCompileError / statNumEvaluated * 100.0);
        LOGGER.println();
        LOGGER.printf("    timed-out: %d (%.2f %%)", statNumTimeout,
                (double) statNumTimeout / statNumEvaluated * 100.0);
        LOGGER.println();
        LOGGER.printf("    failed tests: %d (%.2f %%)", statNumFailed,
                (double) statNumFailed / statNumEvaluated * 100.0);
        LOGGER.println();
        LOGGER.printf("    runtime error: %d (%.2f %%)", statNumRuntimeError,
                (double) statNumRuntimeError / statNumEvaluated * 100.0);
        LOGGER.println();
        LOGGER.printf("    error: %d (%.2f %%)", statNumError,
                (double) statNumError / statNumEvaluated * 100.0);
        LOGGER.println();

        
        if (statBestInIteration.size() >= 2) {
            LOGGER.println();
            LOGGER.println("Best Fitness per Iteration:");
            
            for (int oIter = 0; oIter < statBestInIteration.get(0).numValues(); oIter++) {
                LOGGER.println();
                LOGGER.println("Objective " + (oIter + 1));
                
                final int objective = oIter;
                
                double max = Collections.max(statBestInIteration,
                        (f1, f2) -> Double.compare(f1.getValue(objective), f2.getValue(objective)))
                        .getValue(objective);
                double min = Collections.min(statBestInIteration,
                        (f1, f2) -> Double.compare(f1.getValue(objective), f2.getValue(objective)))
                        .getValue(objective);
                
                final int NUM_LINES = 20;
                double range = (max - min) / NUM_LINES;
                for (int line = 0; line < NUM_LINES; line++) {
                    double upper = max - (line * range);
                    double lower = upper - range;
                    
                    LOGGER.printf("%10.2f |", (upper + lower) / 2);
                    
                    for (int iteration = 0; iteration < statBestInIteration.size(); iteration++) {
                        double fitness = statBestInIteration.get(iteration).getValue(objective);
                        if (fitness <= upper  && fitness >= lower) {
                            LOGGER.print(" *  ");
                        } else {
                            LOGGER.print("    ");
                        }
                    }
                    
                    LOGGER.println();
                }
                LOGGER.println("-----------+" + "----".repeat(statBestInIteration.size()));
                LOGGER.print("           |");
                for (int iteration = 0; iteration < statBestInIteration.size(); iteration++) {
                    LOGGER.printf("%03d ", iteration + 1);
                }
                LOGGER.println();
            }
            
            if (config.getDotExe() != null) {
                if (statBestInIteration.get(0).numValues() == 2) {
                    File output = new File(config.getExecDir(), "fitness.svg");
                    LOGGER.println();
                    LOGGER.println("Rendering fitness evolution to " + output.getName());
                    try {
                        new BestFitnessRenderer(config.getDotExe()).render(statBestInIteration, output);
                    } catch (IOException e) {
                        LOGGER.logException(e);
                    }
                } else if (statBestInIteration.get(0).numValues() == 3) {
                    File output = new File(config.getExecDir(), "fitness.vrml");
                    File output2 = new File(config.getExecDir(), "fitness.wrl");
                    LOGGER.println();
                    LOGGER.println("Rendering fitness evolution to " + output2.getName());
                    try {
                        new BestFitnessRenderer3D(config.getDotExe()).render(statBestInIteration, output);
                        if (output.isFile()) {
                            Files.move(output.toPath(), output2.toPath(), StandardCopyOption.ATOMIC_MOVE);
                        }
                    } catch (IOException e) {
                        LOGGER.logException(e);
                    }
                }
            }
            
        }
    }
    
}
