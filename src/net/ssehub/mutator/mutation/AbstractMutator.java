package net.ssehub.mutator.mutation;

import java.io.File;
import java.io.IOException;
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
import net.ssehub.mutator.mutation.fitness.FitnessComparatorFactory;
import net.ssehub.mutator.mutation.fitness.IFitnessComparator;
import net.ssehub.mutator.util.AsciiChart;
import net.ssehub.mutator.util.Logger;
import net.ssehub.mutator.visualization.BestFitnessRenderer;
import net.ssehub.mutator.visualization.FitnessRenderer;
import net.ssehub.mutator.visualization.FitnessRenderer3D;

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
            
            for (int objective = 0; objective < statBestInIteration.get(0).numValues(); objective++) {
                LOGGER.println();
                LOGGER.println("Objective " + (objective + 1));
                
                AsciiChart chart = new AsciiChart(20);
                
                for (int iteration = 0; iteration < statBestInIteration.size(); iteration++) {
                    double fitness = statBestInIteration.get(iteration).getValue(objective);
                    chart.addPoint(iteration + 1, fitness);
                }
                
                LOGGER.println(chart.toString());
            }
            
            if (config.getDotExe() != null) {
                int dimension = statBestInIteration.get(0).numValues();
                File bestFitOutput = null;
                BestFitnessRenderer bestFitRenderer = null;
                
                if (dimension == 2) {
                    bestFitOutput = new File(config.getExecDir(), "fitness-evolution.svg");
                    bestFitRenderer = new BestFitnessRenderer(config.getDotExe(), false);
                } else if (dimension == 3) {
                    bestFitOutput = new File(config.getExecDir(), "fitness-evolution.wrl");
                    bestFitRenderer = new BestFitnessRenderer(config.getDotExe(), true);
                }

                if (bestFitRenderer != null) {
                    LOGGER.println("Rendering fitness evolution to " + bestFitOutput.getName());
                    try {
                        bestFitRenderer.render(statBestInIteration, bestFitOutput);
                    } catch (IOException e) {
                        LOGGER.logException(e);
                    }
                }
            }
            
        }
        
        if (config.getDotExe() != null) {
            int dimension = -1;
            if (!fitnessStore.isEmpty()) {
                dimension = fitnessStore.values().iterator().next().numValues();
            }
            
            File allFitOutput = null;
            FitnessRenderer allFitRenderer = null;
            
            if (dimension == 2) {
                allFitOutput = new File(config.getExecDir(), "fitness-all.svg");
                allFitRenderer = new FitnessRenderer(config.getDotExe(), false, false);
            } else if (dimension == 3) {
                allFitOutput = new File(config.getExecDir(), "fitness-all.wrl");
                allFitRenderer = new FitnessRenderer3D(config.getDotExe(), false, false);
            }
            
            if (allFitRenderer != null) {
                LOGGER.println("Rendering all fitness values to " + allFitOutput.getName());
                
                try {
                    if (allFitRenderer.init(fitnessStore.values())) {
                        // initial node first
                        allFitRenderer.addNode(fitnessStore.get(getUnmodifiedId()), getUnmodifiedId(), false);
                        
                        
                        // find best seen fitness
                        IFitnessComparator comparator = FitnessComparatorFactory.get();
                        Fitness best = Collections.max(fitnessStore.values(), comparator);
                        
                        for (Map.Entry<String, Fitness> entry : fitnessStore.entrySet()) {
                            boolean isBest = !comparator.isLower(entry.getValue(), best);
                            
                            allFitRenderer.addNode(entry.getValue(), isBest ? entry.getKey() : "", isBest);
                        }
                        
                        allFitRenderer.render(allFitOutput);
                    }
                } catch (IOException e) {
                    LOGGER.logException(e);
                }
            }
        }
    }

}
