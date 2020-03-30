package net.ssehub.mutator.mutation.pattern_based;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.mutation.AbstractMutator;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.mutation.fitness.FitnessComparatorFactory;
import net.ssehub.mutator.mutation.fitness.IFitnessComparator;
import net.ssehub.mutator.mutation.pattern_based.patterns.IOpportunity;
import net.ssehub.mutator.util.Logger;

public class PatternBasedMutator extends AbstractMutator {

    private static final Logger LOGGER = Logger.get(PatternBasedMutator.class.getSimpleName());

    private PatternBasedConfig config;

    private String unmodifiedId;

    private List<IOpportunity> opportunities;

    private IFitnessComparator comparator;

    public PatternBasedMutator(PatternBasedConfig config) {
        super(config);
        this.config = config;
        this.comparator = FitnessComparatorFactory.get();

    }

    @Override
    public List<IMutant> run(File originalAst) {
        PatternBasedMutator.LOGGER.println();
        PatternBasedMutator.LOGGER.println("Initialization");
        PatternBasedMutator.LOGGER.println("--------------");

        this.opportunities = new ArrayList<>();
        // the order here matters, as mutations are applied in this order
        for (String pattern : this.config.getPatterns()) {
            try {
                Class<?> patternClass = Class.forName("net.ssehub.mutator.mutation.pattern_based.patterns." + pattern);

                @SuppressWarnings("unchecked")
                Collection<IOpportunity> oppos = (Collection<IOpportunity>) patternClass
                        .getMethod("findOpportunities", File.class).invoke(null, originalAst);
                this.opportunities.addAll(oppos);
            } catch (ReflectiveOperationException e) {
                PatternBasedMutator.LOGGER.logException(e);
            }
        }

        PatternBasedMutator.LOGGER.println("Opportunities:");
        for (IOpportunity oppo : this.opportunities) {
            PatternBasedMutator.LOGGER.println(" * " + oppo);
        }
        PatternBasedMutator.LOGGER.println();

        TopXMutants mutantList = new TopXMutants(5);

        Mutant initial = new Mutant(this.opportunities);

        // get initial starting parameters from config, if set
        if (this.config.getStartParams() != null) {
            if (this.opportunities.size() != this.config.getStartParams().length) {
                PatternBasedMutator.LOGGER.println(
                        "Warning: got " + this.config.getStartParams().length + " start parameters from config, "
                                + "but got " + this.opportunities.size() + " opportunities");
            }

            for (int i = 0; i < Math.min(this.opportunities.size(), this.config.getStartParams().length); i++) {
                IOpportunity oppo = this.opportunities.get(i);
                int param = this.config.getStartParams()[i];
                if (param < oppo.getMinParam()) {
                    PatternBasedMutator.LOGGER
                            .println("startParams[" + i + "] is too small; setting to " + oppo.getMinParam());
                    param = oppo.getMinParam();
                }
                if (param > oppo.getMaxParam()) {
                    PatternBasedMutator.LOGGER
                            .println("startParams[" + i + "] is too large; setting to " + oppo.getMaxParam());
                    param = oppo.getMaxParam();
                }
                initial.setParam(i, param);
            }
        }

        this.unmodifiedId = initial.getId();
        initial.apply(originalAst);

        PatternBasedMutator.LOGGER.println("Original fitness:");
        Fitness initialFitness = evaluate(initial, false, true);
        if (initialFitness == null) {
            PatternBasedMutator.LOGGER.println("ERROR: Initial mutant doesn't pass");
            return new LinkedList<>();
        }

        mutantList.insertMutant(initial, initialFitness);

        nextIteration();

        if (this.config.getMaxAnnealingIterations() > 0) {
            simulatedAnnealing(originalAst, mutantList);
        } else if (this.config.getRandomSearchIterations() > 0) {
            randomSearch(originalAst, mutantList);
        } else {
            hillClimbing(originalAst, mutantList);
        }

        return mutantList.toList();
    }

    private void hillClimbing(File originalAst, TopXMutants mutantList) {
        boolean improved;
        do {
            PatternBasedMutator.LOGGER.println();
            PatternBasedMutator.LOGGER.printf("Iteration %03d\n", getIteration());
            PatternBasedMutator.LOGGER.println("-------------");

            improved = false;
            List<Mutant> neighbors = generateNeighbors(mutantList.getTopMutant());
            PatternBasedMutator.LOGGER.println("Generated " + neighbors.size() + " neighbors");

            for (Mutant neighbor : neighbors) {
                neighbor.apply(originalAst);
                if (this.config.getSaveIterations()) {
                    java.io.File dir = new java.io.File(this.config.getExecDir(),
                            String.format(Locale.ROOT, "iteration_%03d", getIteration()));
                    dir.mkdir();
                    java.io.File out = new java.io.File(dir, "mutant_" + neighbor.getId() + ".c");
                    try {
                        neighbor.write(out);
                    } catch (IOException e) {
                        PatternBasedMutator.LOGGER.logException(e);
                    }
                }

                Fitness fitness = evaluate(neighbor, true, true);
                if (fitness != null) {
                    if (this.comparator.isLower(mutantList.getTopFitness(), fitness)) {
                        PatternBasedMutator.LOGGER.println(
                                " -> " + neighbor.getId() + " is better than " + mutantList.getTopMutant().getId());
                        improved = true;
                    }

                    mutantList.insertMutant(neighbor, fitness);
                }
            }

            setBestInIteration(mutantList.getTopMutant());
            nextIteration();
        } while (improved);
    }

    private void simulatedAnnealing(File originalAst, TopXMutants mutantList) {
        double initTemp = this.config.getInitialTemperature();
        int maxIter = this.config.getMaxAnnealingIterations();

        Mutant currentMutant = mutantList.getTopMutant();
        Fitness currentFitness = mutantList.getTopFitness();

        mutantList.clear();

        while (getIteration() <= this.config.getMaxAnnealingIterations()) {
            PatternBasedMutator.LOGGER.println();
            PatternBasedMutator.LOGGER.printf("Iteration %03d\n", getIteration());
            PatternBasedMutator.LOGGER.println("-------------");

            double temperature;
            if (this.config.getCoolingFactor() == null) {
                temperature = initTemp * (maxIter - getIteration() + 1) / maxIter;
            } else {
                temperature = initTemp * Math.pow(this.config.getCoolingFactor(), getIteration() - 1);
            }
            PatternBasedMutator.LOGGER.println("Temperature: " + temperature);

            List<Mutant> neighbors = generateNeighbors(currentMutant);
            PatternBasedMutator.LOGGER.println("Generated " + neighbors.size() + " neighbors");

            Mutant neighbor;
            Fitness nFitness;
            do {
                neighbor = neighbors.get((int) (Math.random() * neighbors.size()));
                neighbor.apply(originalAst);

                if (this.config.getSaveIterations()) {
                    java.io.File dir = new java.io.File(this.config.getExecDir(),
                            String.format(Locale.ROOT, "iteration_%03d", getIteration()));
                    dir.mkdir();
                    java.io.File out = new java.io.File(dir, "mutant_" + neighbor.getId() + ".c");
                    try {
                        neighbor.write(out);
                    } catch (IOException e) {
                        PatternBasedMutator.LOGGER.logException(e);
                    }
                }

                nFitness = evaluate(neighbor, true, true);
            } while (nFitness == null);

            if (this.comparator.isLower(currentFitness, nFitness)) {
                PatternBasedMutator.LOGGER
                        .println(" -> " + neighbor.getId() + " is better than " + currentMutant.getId());
                currentMutant = neighbor;
                currentFitness = nFitness;
            } else {
                double delta = this.comparator.toSingleValue(currentFitness) - this.comparator.toSingleValue(nFitness);
                // TODO: normalise delta somehow?
                // TODO: use max delta for multi-objective?

                if (Math.random() < Math.pow(Math.E, -delta / temperature)) {
                    PatternBasedMutator.LOGGER.println(" -> " + neighbor.getId() + " selected because of temperature");
                    currentMutant = neighbor;
                    currentFitness = nFitness;
                } else {
                    PatternBasedMutator.LOGGER.println(" -> " + neighbor.getId() + " not selected");
                }
            }

            setBestInIteration(currentFitness);
            nextIteration();
        }

        PatternBasedMutator.LOGGER.println();
        PatternBasedMutator.LOGGER.println("Temperature exceeded, falling back to hill climbing");
        PatternBasedMutator.LOGGER.println("Current mutant:");
        PatternBasedMutator.LOGGER.println(currentMutant.getId() + ": " + currentFitness);

        mutantList.insertMutant(currentMutant, currentFitness);

        hillClimbing(originalAst, mutantList);
    }

    private void randomSearch(File originalAst, TopXMutants mutantList) {
        PatternBasedMutator.LOGGER.println();
        PatternBasedMutator.LOGGER.println("Random Search");
        PatternBasedMutator.LOGGER.println("-------------");
        PatternBasedMutator.LOGGER.println("Number to generate: " + this.config.getRandomSearchIterations());

        while (getIteration() <= this.config.getRandomSearchIterations()) {
            Mutant random = generateRandom();
            random.apply(originalAst);
            Fitness fitness = evaluate(random, true, true);
            if (fitness != null) {
                mutantList.insertMutant(random, fitness);
            }

            nextIteration();
        }
    }

    private List<Mutant> generateNeighbors(Mutant base) {
        List<Mutant> result = new LinkedList<>();

        for (int i = 0; i < this.opportunities.size(); i++) {
            IOpportunity oppo = this.opportunities.get(i);
            int paramBase = base.getParams(i);

            if (paramBase - 1 >= oppo.getMinParam()) {
                Mutant newNeighbor = new Mutant(base);
                newNeighbor.setParam(i, paramBase - 1);
                result.add(newNeighbor);
            }

            if (paramBase + 1 <= oppo.getMaxParam()) {
                Mutant newNeighbor = new Mutant(base);
                newNeighbor.setParam(i, paramBase + 1);
                result.add(newNeighbor);
            }
        }

        return result;
    }

    private Mutant generateRandom() {
        Random random = new Random();
        Mutant mutant = new Mutant(this.opportunities);

        for (int i = 0; i < this.opportunities.size(); i++) {
            IOpportunity oppo = this.opportunities.get(i);

            mutant.setParam(i, random.nextInt(oppo.getMaxParam() - oppo.getMinParam() + 1) + oppo.getMinParam());
        }

        return mutant;
    }

    @Override
    public String getUnmodifiedId() {
        return this.unmodifiedId;
    }

}
