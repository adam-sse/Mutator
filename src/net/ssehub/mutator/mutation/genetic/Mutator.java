package net.ssehub.mutator.mutation.genetic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.ssehub.mutator.Configuration;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.evaluation.Evaluator;
import net.ssehub.mutator.evaluation.EvaluatorFactory;
import net.ssehub.mutator.evaluation.TestResult;
import net.ssehub.mutator.mutation.AbstractMutator;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.genetic.mutations.Mutation;
import net.ssehub.mutator.mutation.genetic.mutations.MutationFactory;

public class Mutator extends AbstractMutator {

    private Configuration config;
    
    private File originalAst;
    
    private Evaluator evaluator;
    
    private Random random;
    
    private MutantList population;
    
    private int generation;
    
    private int nextMutantId;
    
    public Mutator(Configuration config) {
        this.config = config;
        random = new Random(config.getSeed());
    }
    
    @Override
    public List<IMutant> run(File originalAst) {
        this.evaluator = EvaluatorFactory.create(config);
        this.originalAst = originalAst;
        
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
                
                Double fitness = evaluate(mutant, evaluator, false);
                
                if (fitness == null) {
                    population.removeMutant(i);
                    i--;
                }
            }
            
            population.sort(this);
            
            if (this.generation % config.getCleanFrequency() == 0) {
                cleanPopulation();
            }
            
            if (population.getSize() > 0) {
                setBestInIteration(generation, population.getMutant(0));
            } else {
                setBestInIteration(generation, 0.0);
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
        double originalFitness = getFitness(original.getId());
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
                        
                        setFitness(cleanedId, tempFitness);
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
    public String getUnmodifiedId() {
        return "G001_M001";
    }
    
}
