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

    private static class FitnessStoreEntry extends Fitness {

        private int iteration;

        public FitnessStoreEntry(Fitness fitness, int iteration) {
            super(fitness.getValues());
            this.iteration = iteration;
        }

    }

    private Map<String, Fitness> fitnessStore;

    private BaseConfig config;

    private Evaluator evaluator;

    private int iteration;

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

    protected int getIteration() {
        return this.iteration;
    }

    protected void nextIteration() {
        this.iteration++;
    }

    @Override
    public Fitness getFitness(String mutantId) {
        return this.fitnessStore.get(mutantId);
    }

    protected void setFitness(String mutantId, Fitness fitness) {
        this.fitnessStore.put(mutantId, new FitnessStoreEntry(fitness, this.iteration));
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
            this.statNumEvaluated++;
        }

        TestResult testResult = this.evaluator.test(mutant);
        if (testResult != TestResult.PASS) {
            if (printAndStats) {
                LOGGER.println(mutant.getId() + " " + testResult);

                switch (testResult) {
                case COMPILATION_FAILED:
                    this.statNumCompileError++;
                    break;

                case ERROR:
                    this.statNumError++;
                    break;

                case TEST_FAILED:
                    this.statNumFailed++;
                    break;

                case TIMEOUT:
                    this.statNumTimeout++;
                    break;

                default:
                }
            }
        } else {
            fitness = this.evaluator.measureFitness(mutant);
            if (fitness == Evaluator.RUNTIME_ERROR) {
                if (printAndStats) {
                    LOGGER.println(mutant.getId() + " had a runtime error during fitness evaluation");
                    this.statNumRuntimeError++;
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

    protected void setBestInIteration(IMutant bestMutant) {
        setBestInIteration(getFitness(bestMutant.getId()));
    }

    protected void setBestInIteration(Fitness bestFitness) {
        if (this.iteration - 1 < this.statBestInIteration.size()) {
            this.statBestInIteration.set(this.iteration - 1, bestFitness);
        } else {
            this.statBestInIteration.add(this.iteration - 1, bestFitness);
        }
    }

    @Override
    public void printStatistics() {
        LOGGER.println("Evaluated: " + this.statNumEvaluated);
        LOGGER.printf("    failed compilation: %d (%.2f %%)", this.statNumCompileError,
                (double) this.statNumCompileError / this.statNumEvaluated * 100.0);
        LOGGER.println();
        LOGGER.printf("    timed-out: %d (%.2f %%)", this.statNumTimeout,
                (double) this.statNumTimeout / this.statNumEvaluated * 100.0);
        LOGGER.println();
        LOGGER.printf("    failed tests: %d (%.2f %%)", this.statNumFailed,
                (double) this.statNumFailed / this.statNumEvaluated * 100.0);
        LOGGER.println();
        LOGGER.printf("    runtime error: %d (%.2f %%)", this.statNumRuntimeError,
                (double) this.statNumRuntimeError / this.statNumEvaluated * 100.0);
        LOGGER.println();
        LOGGER.printf("    error: %d (%.2f %%)", this.statNumError,
                (double) this.statNumError / this.statNumEvaluated * 100.0);
        LOGGER.println();

        if (this.statBestInIteration.size() >= 2) {
            LOGGER.println();
            LOGGER.println("Best Fitness per Iteration:");

            for (int objective = 0; objective < this.statBestInIteration.get(0).numValues(); objective++) {
                LOGGER.println();
                LOGGER.println("Objective " + (objective + 1));

                AsciiChart chart = new AsciiChart(20);

                for (int iteration = 0; iteration < this.statBestInIteration.size(); iteration++) {
                    double fitness = this.statBestInIteration.get(iteration).getValue(objective);
                    chart.addPoint(iteration + 1, fitness);
                }

                LOGGER.println(chart.toString());
            }

            if (this.config.getDotExe() != null) {
                int dimension = this.statBestInIteration.get(0).numValues();
                File bestFitOutput = null;
                BestFitnessRenderer bestFitRenderer = null;

                if (dimension == 2) {
                    bestFitOutput = new File(this.config.getExecDir(), "fitness-evolution.svg");
                    bestFitRenderer = new BestFitnessRenderer(this.config.getDotExe(), false);
                } else if (dimension == 3) {
                    bestFitOutput = new File(this.config.getExecDir(), "fitness-evolution.wrl");
                    bestFitRenderer = new BestFitnessRenderer(this.config.getDotExe(), true);
                }

                if (bestFitRenderer != null) {
                    LOGGER.println("Rendering fitness evolution to " + bestFitOutput.getName());
                    try {
                        bestFitRenderer.render(this.statBestInIteration, bestFitOutput);
                    } catch (IOException e) {
                        LOGGER.logException(e);
                    }
                }
            }
        }

        if (this.config.getDotExe() != null) {
            int dimension = -1;
            if (!this.fitnessStore.isEmpty()) {
                dimension = this.fitnessStore.values().iterator().next().numValues();
            }

            File allFitOutput = null;
            FitnessRenderer allFitRenderer = null;

            if (dimension == 2) {
                allFitOutput = new File(this.config.getExecDir(), "fitness-all.svg");
                allFitRenderer = new FitnessRenderer(this.config.getDotExe(), false, false);
            } else if (dimension == 3) {
                allFitOutput = new File(this.config.getExecDir(), "fitness-all.wrl");
                allFitRenderer = new FitnessRenderer3D(this.config.getDotExe(), false, false);
            }

            if (allFitRenderer != null) {
                LOGGER.println("Rendering all fitness values to " + allFitOutput.getName());

                try {
                    if (allFitRenderer.init(this.fitnessStore.values())) {
                        // find best seen fitness
                        IFitnessComparator comparator = FitnessComparatorFactory.get();
                        Fitness best = Collections.max(this.fitnessStore.values(), comparator);

                        for (Map.Entry<String, Fitness> entry : this.fitnessStore.entrySet()) {
                            FitnessStoreEntry fitness = (FitnessStoreEntry) entry.getValue();

                            boolean isInit = entry.getKey().equals(getUnmodifiedId());
                            boolean isBest = !comparator.isLower(entry.getValue(), best);

                            allFitRenderer.addNode(entry.getValue(), (isBest || isInit) ? entry.getKey() : "", isInit,
                                    isBest, (double) fitness.iteration / (this.iteration - 1));
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
