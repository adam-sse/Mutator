package mutation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import evaluation.Evaluator;
import evaluation.EvaluatorFactory;
import evaluation.TestResult;
import main.Configuration;
import mutation.mutations.CopyInsertStatement;
import mutation.mutations.CopyOverrideExpression;
import mutation.mutations.CopyOverrideStatement;
import mutation.mutations.DeleteStatement;
import mutation.mutations.Mutation;
import mutation.mutations.OverrideWithLiteral;
import parsing.ast.File;

public class Mutator implements IFitnessStore {

    private Configuration config;
    
    private Map<String, Double> fitnessStore;
    
    private Random random;
    
    private MutantList population;
    
    private int generation;
    
    private int nextMutantId;
    
    private int stat_numEvaluated;
    private int stat_numCompileError;
    private int stat_numTimeout;
    private int stat_numFailed;
    private int stat_numError;
    
    public Mutator(Configuration config) {
        this.config = config;
        random = new Random(config.getSeed());
    }
    
    public MutantList run(File originalAst) {
        Evaluator evaluator = EvaluatorFactory.create(config);
        this.fitnessStore = new HashMap<>(config.getGenerations() * config.getPopulationSize());
        
        this.population = new MutantList();
        this.generation = 0;
        nextGeneration();

        System.out.println();
        System.out.println("Initial Creation");
        System.out.println("----------------");
        // add the default non-mutant
        Mutant nonMutant = new Mutant(generateMutantId(), originalAst);
        this.population.addMutant(nonMutant);
        System.out.println(nonMutant.getId() + " is unmodified original");
        while (population.getSize() < config.getPopulationSize()) {
            Mutant mutant = generateInitialMutant(originalAst);
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
                
                stat_numEvaluated++;
            
                TestResult testResult = evaluator.test(mutant);
                if (testResult != TestResult.PASS) {
                    population.removeMutant(i);
                    i--;
                    System.out.println(mutant.getId() + " " + testResult);
                    
                    switch (testResult) {
                    case COMPILATION_FAILED:
                        stat_numCompileError++;
                        break;
                    case ERROR:
                        stat_numError++;
                        break;
                    case TEST_FAILED:
                        stat_numFailed++;
                        break;
                    case TIMEOUT:
                        stat_numTimeout++;
                        break;
                    default:
                    }
                    
                } else {
                    double fitness = evaluator.measureFitness(mutant);
                    System.out.println(mutant.getId() + ": " + fitness);
                    fitnessStore.put(mutant.getId(), fitness);
                }
                
            }
            
            population.sort(this);
            
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
                        Mutant mutant = generateInitialMutant(originalAst);
                        if (nextPopulation.addMutant(mutant)) {
                            System.out.println(mutant.getId() + " is new mutant");
                        }
                    }
                }
                
                this.population = nextPopulation;
            }
        }
        
        return population;
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

    private Mutant generateInitialMutant(File originalAst) {
        Mutant mutant = new Mutant(generateMutantId(), originalAst);
        
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
        boolean added;
        do {
            Mutation mutation = createMutation(mutant.getAst());
            added = mutant.addMutation(mutation);
        } while (!added);
    }
    
    private Mutation createMutation(File ast) {
        Mutation mutation = null;
        
        do {
            
            switch (random.nextInt(5)) {
            case 0:
                mutation = CopyOverrideStatement.find(ast, random);
                break;
            case 1:
                mutation = CopyOverrideExpression.find(ast, random);
                break;
            case 2:
                mutation = DeleteStatement.find(ast, random);
                break;
            case 3:
                mutation = CopyInsertStatement.find(ast, random);
                break;
            case 4:
                mutation = OverrideWithLiteral.find(ast, random);
                break;
            }
            
        } while (mutation == null);
        
        return mutation;
    }
    
    private String generateMutantId() {
        return String.format(Locale.ROOT, "G%03d_M%03d", generation, nextMutantId++);
    }

    @Override
    public Double getFitness(String mutantId) {
        return fitnessStore.get(mutantId);
    }
    
    public void printStatistics() {
        System.out.println("Evaluated: " + stat_numEvaluated);
        System.out.println("    failed compilation: " + stat_numCompileError);
        System.out.println("    timed-out: " + stat_numTimeout);
        System.out.println("    failed tests: " + stat_numFailed);
        System.out.println("    error: " + stat_numError);
    }
    
}
