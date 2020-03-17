package net.ssehub.mutator.mutation.genetic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import net.ssehub.mutator.Configuration;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.evaluation.Evaluator;
import net.ssehub.mutator.evaluation.EvaluatorFactory;
import net.ssehub.mutator.evaluation.TestResult;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.IMutator;
import net.ssehub.mutator.mutation.genetic.mutations.Mutation;
import net.ssehub.mutator.mutation.genetic.mutations.MutationFactory;

public class Mutator implements IMutator {

    private Configuration config;
    
    private File originalAst;
    
    private Map<String, Double> fitnessStore;
    
    private Evaluator evaluator;
    
    private Random random;
    
    private MutantList population;
    
    private int generation;
    
    private int nextMutantId;
    
    private int statNumEvaluated;
    private int statNumCompileError;
    private int statNumTimeout;
    private int statNumFailed;
    private int statNumRuntimeError;
    private int statNumError;
    private List<Double> statBestGenFitness;
    
    public Mutator(Configuration config) {
        this.config = config;
        random = new Random(config.getSeed());
    }
    
    @Override
    public List<IMutant> run(File originalAst) {
        this.evaluator = EvaluatorFactory.create(config);
        this.fitnessStore = new HashMap<>(config.getGenerations() * config.getPopulationSize());
        this.originalAst = originalAst;
        
        this.statBestGenFitness = new ArrayList<>(config.getGenerations());
        
        this.population = new MutantList();
        this.generation = 0;
        nextGeneration();

        System.out.println();
        System.out.println("Initial Creation");
        System.out.println("----------------");
        // add the default non-mutant
        Mutant nonMutant = new Mutant(generateMutantId(), this.originalAst);
        this.population.addMutant(nonMutant);
        System.out.println(nonMutant.getId() + " is unmodified original");
        while (population.getSize() < config.getPopulationSize()) {
            Mutant mutant = generateInitialMutant();
            if (population.addMutant(mutant)) {
                System.out.println(mutant.getId() + " is new mutant");
            }
        }
        
        while (generation <= config.getGenerations()) {
            if (config.getSaveGenerations()) {
                saveGeneration();
            }
            
            // evaluate fitness
            System.out.println();
            System.out.println("Evaluation");
            System.out.println("----------");
            for (int i = 0; i < population.getSize(); i++) {
                Mutant mutant = population.getMutant(i);
                
                statNumEvaluated++;
            
                TestResult testResult = this.evaluator.test(mutant);
                if (testResult != TestResult.PASS) {
                    population.removeMutant(i);
                    i--;
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
                    double fitness = this.evaluator.measureFitness(mutant);
                    if (fitness == Evaluator.RUNTIME_ERROR) {
                        System.out.println(mutant.getId() + " had a runtime error during fitness evaluation");
                        population.removeMutant(i);
                        i--;
                        statNumRuntimeError++;
                        
                    } else {
                        System.out.println(mutant.getId() + ": " + fitness);
                        fitnessStore.put(mutant.getId(), fitness);
                    }
                }
                
            }
            
            population.sort(this);
            
            if (this.generation % config.getCleanFrequency() == 0) {
                cleanPopulation();
            }
            
            if (population.getSize() > 0) {
                this.statBestGenFitness.add(fitnessStore.get(population.getMutant(0).getId()));
            } else {
                this.statBestGenFitness.add(0.0);
            }
            
            nextGeneration();
            if (generation <= config.getGenerations()) {
                
                System.out.println();
                System.out.println("Creation");
                System.out.println("--------");
                
                // create next generation
                MutantList nextPopulation = new MutantList();
                
                // elitism: keep best X unmodified mutants from previous generation
                for (int i = 0; i < config.getElitism() && i < population.getSize(); i++) {
                    if (nextPopulation.addMutant(population.getMutant(i))) {
                        System.out.println(population.getMutant(i).getId() + " survived because of elitism");
                    }
                }
                
                if (population.getSize() >= 2) {
                    
                    // create crossover mutants by recombination of survivors
                    while (nextPopulation.getSize() < config.getPopulationSize()) {
                        Mutant m1;
                        Mutant m2;
                        do {
                            m1 = randomlySelect(population);
                            m2 = randomlySelect(population);
                        } while (m1.equals(m2));
                        
                        Mutant cross = createCrossover(m1, m2);
                        if (cross != null) {
                            
                            boolean mutate = random.nextInt(2) == 0;
                            if (mutate) {
                                addNewMutation(cross);
                            }
                            if (nextPopulation.addMutant(cross)) {
                                System.out.println(cross.getId() + " is " + (mutate ? "mutated " : "")
                                        + "crossover of " + m1.getId() + " & " + m2.getId());
                            }
                            
                        } else {
                            Mutant descendant = createDescendant(m1);
                            if (nextPopulation.addMutant(descendant)) {
                                System.out.println(descendant.getId() + " is direct descendant of "
                                        + m1.getId() + " (crossover with " + m2.getId() + " failed)");
                            }
                        }
                    }
                    
                } else {
                    System.out.println("Not enough mutants survived, creating new initial mutants");
                    while (nextPopulation.getSize() < config.getPopulationSize()) {
                        Mutant mutant = generateInitialMutant();
                        if (nextPopulation.addMutant(mutant)) {
                            System.out.println(mutant.getId() + " is new mutant");
                        }
                    }
                }
                
                this.population = nextPopulation;
            }
        }

        cleanPopulation();
        
        return this.population.convertToList();
    }
    
    private void cleanPopulation() {
        if (config.getCleanThreshold() > 0) {
            System.out.println();
            System.out.println("Cleaning");
            System.out.println("--------");
            MutantList cleaned = new MutantList();
            for (Mutant mutant : population) {
                if (!cleaned.addMutant(cleanMutations(mutant))) {
                    System.out.println(mutant.getId() + " is redundant after cleaning");
                }
            }
            
            cleaned.sort(this);
            
            this.population = cleaned;
        }
    }
    
    private Mutant randomlySelect(MutantList population) {
        /*
         * select a mutant, with the first having higher priority
         * 
         * e.g.:
         * population.getSize() = 5
         * random is in [0, 15)  (5*(5+1))/2=15
         * 
         * "buckets" for mutants (if random is in bucket, select that individual): 
         * [0]:  0 -  4 => P([0]) = 5/15 = 33.33 %
         * [1]:  5 -  8 => P([1]) = 4/15 = 26.66 %
         * [2]:  9 - 11 => P([2]) = 3/15 = 20.00 %
         * [3]: 12 - 13 => P([3]) = 2/15 = 13.33 %
         * [4]: 14 - 14 => P([4]) = 1/15 =  6.66 %
         * 
         */
        int limit = (population.getSize() * (population.getSize() + 1)) / 2;
        int random = this.random.nextInt(limit);
        
        int range = population.getSize() - 1;
        int start = 0;
        for (Mutant mutant : population) {
            if (random <= start + range) {
                return mutant;
            }
            
            start += range + 1;
            range--;
        }
        
        // can never reach here
        throw new AssertionError();
    }
    
    private void nextGeneration() {
        this.generation++;
        this.nextMutantId = 1;
        
        if (generation <= config.getGenerations()) {
            System.out.println();
            System.out.printf(Locale.ROOT, "Generation %03d\n", generation);
            System.out.println("==============");
        }
    }
    
    private void saveGeneration() {
        java.io.File folder = new java.io.File(String.format(Locale.ROOT, "generation_%03d", generation));
        folder.mkdir();
        
        for (Mutant mutant : population) {
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
        
        for (int i = 0; i < config.getInitialMutations(); i++) {
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
        MutationFactory factory = new MutationFactory(config.getMutations());
        
        boolean added;
        do {
            Mutation mutation = factory.createRandomMutation(mutant.getAst(), random);
            added = mutant.addMutation(mutation);
        } while (!added);
    }
    
    private Mutant cleanMutations(Mutant original) {
        if (original.getId().endsWith("c")) {
            return original;
        }
        
        String cleanedId = original.getId() + "c";
        double originalFitness = fitnessStore.get(original.getId());
        List<Mutation> mutations = new ArrayList<>(original.getMutations());
        
        System.out.println("Cleaning " + original.getId());
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
                TestResult testResult = this.evaluator.test(temp);
                if (testResult == TestResult.PASS) {
                    double tempFitness = evaluator.measureFitness(temp);
                    if ((originalFitness - tempFitness) / originalFitness < config.getCleanThreshold()) {
                        System.out.println(" * Mutation " + mutations.get(i) + " is not required");
                        System.out.println("   (original fitness: " + originalFitness
                                + "; w/o this mutation: " + tempFitness + ")");
                        
                        this.fitnessStore.put(cleanedId, tempFitness);
                        if (tempFitness > originalFitness) {
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
            System.out.println(" * All mutations required");
            return original;
        }
    }
    
    private String generateMutantId() {
        return String.format(Locale.ROOT, "G%03d_M%03d", generation, nextMutantId++);
    }

    @Override
    public Double getFitness(String mutantId) {
        return fitnessStore.get(mutantId);
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
        
        System.out.println();
        System.out.println("Best Fitness per Generation:");
        
        double max = Collections.max(statBestGenFitness);
        double min = Collections.min(statBestGenFitness);
        
        final int NUM_LINES = 20;
        double range = (max - min) / NUM_LINES;
        for (int line = 0; line < NUM_LINES; line++) {
            double upper = max - (line * range);
            double lower = upper - range;
            
            System.out.printf(Locale.ROOT, "%10.2f |", (upper + lower) / 2);
            
            for (int gen = 0; gen < statBestGenFitness.size(); gen++) {
                double fitness = statBestGenFitness.get(gen);
                if (fitness <= upper  && fitness >= lower) {
                    System.out.print(" *  ");
                } else {
                    System.out.print("    ");
                }
            }
            
            System.out.println();
        }
        System.out.println("-----------+" + "----".repeat(statBestGenFitness.size()));
        System.out.print("           |");
        for (int gen = 0; gen < statBestGenFitness.size(); gen++) {
            System.out.printf(Locale.ROOT, "%03d ", gen + 1);
        }
    }
    
}
