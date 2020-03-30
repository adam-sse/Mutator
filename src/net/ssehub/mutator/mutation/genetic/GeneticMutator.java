package net.ssehub.mutator.mutation.genetic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.mutation.AbstractMutator;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.mutation.fitness.FitnessComparatorFactory;
import net.ssehub.mutator.mutation.fitness.IFitnessComparator;
import net.ssehub.mutator.mutation.genetic.mutations.Mutation;
import net.ssehub.mutator.mutation.genetic.mutations.MutationFactory;
import net.ssehub.mutator.util.Logger;

public class GeneticMutator extends AbstractMutator {

    private static final Logger LOGGER = Logger.get(GeneticMutator.class.getSimpleName());

    private GeneticConfig config;

    private File originalAst;

    private Random random;

    private MutantList population;

    private int nextMutantId;

    public GeneticMutator(GeneticConfig config) {
        super(config);
        this.config = config;
        this.random = new Random(config.getSeed());
    }

    @Override
    public List<IMutant> run(File originalAst) {
        this.originalAst = originalAst;

        this.population = new MutantList();
        nextIteration();

        LOGGER.println();
        LOGGER.println("Initial Creation");
        LOGGER.println("----------------");
        // add the default non-mutant
        Mutant nonMutant = new Mutant(generateMutantId(), this.originalAst);
        this.population.addMutant(nonMutant);
        LOGGER.println(nonMutant.getId() + " is unmodified original");
        while (this.population.getSize() < this.config.getPopulationSize()) {
            Mutant mutant = generateInitialMutant();
            if (this.population.addMutant(mutant)) {
                LOGGER.println(mutant.getId() + " is new mutant");
            }
        }

        while (getIteration() <= this.config.getGenerations()) {
            if (this.config.getSaveIterations()) {
                saveGeneration();
            }

            // evaluate fitness
            LOGGER.println();
            LOGGER.println("Evaluation");
            LOGGER.println("----------");
            for (int i = 0; i < this.population.getSize(); i++) {
                Mutant mutant = this.population.getMutant(i);

                Fitness fitness = evaluate(mutant, true, true);

                if (fitness == null) {
                    this.population.removeMutant(i);
                    i--;
                }
            }

            this.population.sort(this);

            if (getIteration() % this.config.getCleanFrequency() == 0) {
                cleanPopulation();
            }

            if (this.population.getSize() > 0) {
                setBestInIteration(this.population.getMutant(0));
            } else {
                setBestInIteration(new Fitness(0.0));
            }

            nextIteration();
            if (getIteration() <= this.config.getGenerations()) {

                LOGGER.println();
                LOGGER.println("Creation");
                LOGGER.println("--------");

                // create next generation
                MutantList nextPopulation = new MutantList();

                // elitism: keep best X unmodified mutants from previous generation
                for (int i = 0; i < this.config.getElitism() && i < this.population.getSize(); i++) {
                    if (nextPopulation.addMutant(this.population.getMutant(i))) {
                        LOGGER.println(this.population.getMutant(i).getId() + " survived because of elitism");
                    }
                }

                if (this.population.getSize() >= 2) {

                    // create crossover mutants by recombination of survivors
                    while (nextPopulation.getSize() < this.config.getPopulationSize()) {
                        Mutant m1;
                        Mutant m2;
                        do {
                            m1 = randomlySelect(this.population);
                            m2 = randomlySelect(this.population);
                        } while (m1.equals(m2));

                        Mutant cross = createCrossover(m1, m2);
                        if (cross != null) {

                            boolean mutate = this.random.nextInt(2) == 0;
                            if (mutate) {
                                addNewMutation(cross);
                            }
                            if (nextPopulation.addMutant(cross)) {
                                LOGGER.println(cross.getId() + " is " + (mutate ? "mutated " : "") + "crossover of "
                                        + m1.getId() + " & " + m2.getId());
                            }
                        } else {
                            Mutant descendant = createDescendant(m1);
                            if (nextPopulation.addMutant(descendant)) {
                                LOGGER.println(descendant.getId() + " is direct descendant of " + m1.getId()
                                        + " (crossover with " + m2.getId() + " failed)");
                            }
                        }
                    }
                } else {
                    LOGGER.println("Not enough mutants survived, creating new initial mutants");
                    while (nextPopulation.getSize() < this.config.getPopulationSize()) {
                        Mutant mutant = generateInitialMutant();
                        if (nextPopulation.addMutant(mutant)) {
                            LOGGER.println(mutant.getId() + " is new mutant");
                        }
                    }
                }

                this.population = nextPopulation;
            }
        }

        if ((getIteration() - 1) % this.config.getCleanFrequency() != 0) {
            cleanPopulation();
        }

        return this.population.convertToList();
    }

    private void cleanPopulation() {
        if (this.config.getCleanThreshold() > 0) {
            LOGGER.println();
            LOGGER.println("Cleaning");
            LOGGER.println("--------");
            MutantList cleaned = new MutantList();
            for (Mutant mutant : this.population) {
                if (!cleaned.addMutant(cleanMutations(mutant))) {
                    LOGGER.println(mutant.getId() + " is redundant after cleaning");
                }
            }

            cleaned.sort(this);

            this.population = cleaned;
        }
    }

    private Mutant randomlySelect(MutantList population) {
        /*
         * select a mutant, with the first having higher priority e.g.:
         * population.getSize() = 5 random is in [0, 15) (5*(5+1))/2=15 "buckets" for
         * mutants (if random is in bucket, select that individual): [0]: 0 - 4 =>
         * P([0]) = 5/15 = 33.33 % [1]: 5 - 8 => P([1]) = 4/15 = 26.66 % [2]: 9 - 11 =>
         * P([2]) = 3/15 = 20.00 % [3]: 12 - 13 => P([3]) = 2/15 = 13.33 % [4]: 14 - 14
         * => P([4]) = 1/15 = 6.66 %
         */
        int limit = (population.getSize() * (population.getSize() + 1)) / 2;
        int random = this.random.nextInt(limit);

        int range = population.getSize() - 1;
        int start = 0;
        for (Mutant mutant : population) {
            if (random <= start + range)
                return mutant;

            start += range + 1;
            range--;
        }

        // can never reach here
        throw new AssertionError();
    }

    @Override
    protected void nextIteration() {
        super.nextIteration();
        this.nextMutantId = 1;

        if (getIteration() <= this.config.getGenerations()) {
            LOGGER.println();
            LOGGER.printf("Generation %03d\n", getIteration());
            LOGGER.println("==============");
        }
    }

    private void saveGeneration() {
        java.io.File folder = new java.io.File(this.config.getExecDir(),
                String.format(Locale.ROOT, "generation_%03d", getIteration()));
        folder.mkdir();

        for (Mutant mutant : this.population) {
            java.io.File output = new java.io.File(folder, "mutant_" + mutant.getId() + ".c");
            try {
                mutant.write(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Mutant generateInitialMutant() {
        Mutant mutant = new Mutant(generateMutantId(), this.originalAst);

        for (int i = 0; i < this.config.getInitialMutations(); i++) {
            addNewMutation(mutant);
        }

        return mutant;
    }

    private Mutant createDescendant(Mutant base) {
        Mutant mutant = base.clone(generateMutantId());

        addNewMutation(mutant);

        return mutant;
    }

    private Mutant createCrossover(Mutant m1, Mutant m2) {
        Mutant crossover = m1.clone(generateMutantId());
        boolean oneSuccess = false;

        for (Mutation mutation : m2.getMutations()) {
            oneSuccess |= crossover.addMutation(mutation);
        }

        return oneSuccess ? crossover : null;
    }

    private void addNewMutation(Mutant mutant) {
        MutationFactory factory = new MutationFactory(this.config.getMutations());

        boolean added;
        do {
            Mutation mutation = factory.createRandomMutation(mutant.getAst(), this.random);
            added = mutant.addMutation(mutation);
        } while (!added);
    }

    private boolean isWithinThreshold(Fitness originalFitness, Fitness newFitness, double threshold) {
        if (originalFitness.numValues() != newFitness.numValues())
            throw new IllegalArgumentException("Fitness values have different number of objectives");

        for (int i = 0; i < originalFitness.numValues(); i++) {
            double o = originalFitness.getValue(i);
            double n = newFitness.getValue(i);
            if ((o - n) / o > threshold)
                return false;
        }
        return true;
    }

    private Mutant cleanMutations(Mutant original) {
        if (original.getId().endsWith("c"))
            return original;

        String cleanedId = original.getId() + "c";
        Fitness originalFitness = getFitness(original.getId());
        List<Mutation> mutations = new ArrayList<>(original.getMutations());

        IFitnessComparator comparator = FitnessComparatorFactory.get();

        LOGGER.println("Cleaning " + original.getId());
        boolean changed;
        do {
            changed = false;

            for (int i = 0; i < mutations.size(); i++) {
                // create a temporary mutant with all mutations but the i-th.
                Mutant temp = new Mutant("temp", this.originalAst);
                for (int j = 0; j < mutations.size(); j++) {
                    if (i != j) {
                        temp.addMutation(mutations.get(j));
                    }
                }

                // evaluate
                Fitness tempFitness = evaluate(temp, false, false);
                if (tempFitness != null) {
                    if (isWithinThreshold(originalFitness, tempFitness, this.config.getCleanThreshold())) {
                        LOGGER.println(" * Mutation " + mutations.get(i) + " is not required");
                        LOGGER.println("   (original fitness: " + originalFitness + "; w/o this mutation: "
                                + tempFitness + ")");

                        setFitness(cleanedId, tempFitness);
                        if (comparator.isLower(originalFitness, tempFitness)) {
                            originalFitness = tempFitness;
                        }

                        changed = true;
                        mutations.remove(i);
                        i--;
                    }
                }
            }
        } while (changed);

        if (!mutations.equals(original.getMutations())) {
            Mutant replacement = new Mutant(cleanedId, this.originalAst);
            for (Mutation mutation : mutations) {
                replacement.addMutation(mutation);
            }

            return replacement;
        } else {
            LOGGER.println(" * All mutations required");
            return original;
        }
    }

    private String generateMutantId() {
        return String.format(Locale.ROOT, "G%03d_M%03d", getIteration(), this.nextMutantId++);
    }

    @Override
    public String getUnmodifiedId() {
        return "G001_M001";
    }

}
