package net.ssehub.mutator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Configuration {
    
    private long seed;

    private boolean saveGenerations;
    
    private int generations;
    
    private int populationSize;
    
    private int elitism;
    
    private int initialMutations;
    
    private List<String> mutations;
    
    private int cleanFrequency;
    
    private double cleanThreshold;
    
    private int timeout;
    
    private File dropin;
    
    private List<String> compilerArgs;
    
    private File testSrc;
    
    private File fitnessSrc;
    
    private int fitnessMeasures;
    
    private long sleepBeforeFitness;
    
    public Configuration(Properties props) {
        this.seed = Long.parseLong(props.getProperty("mutator.seed"));
        this.saveGenerations = Boolean.parseBoolean(props.getProperty("mutator.saveGenerations"));
        this.generations = Integer.parseInt(props.getProperty("mutator.generations"));
        this.populationSize = Integer.parseInt(props.getProperty("mutator.populationSize"));
        this.elitism = Integer.parseInt(props.getProperty("mutator.elitism"));
        this.initialMutations = Integer.parseInt(props.getProperty("mutator.initialMutations"));
        
        String[] tmp = props.getProperty("mutator.mutations").split(",");
        this.mutations = new ArrayList<>(tmp.length);
        for (String mutation : tmp) {
            this.mutations.add(mutation.trim());
        }
        
        this.cleanFrequency = Integer.parseInt(props.getProperty("mutator.clean.frequency"));
        this.cleanThreshold = Double.parseDouble(props.getProperty("mutator.clean.threshold"));
        
        this.timeout = Integer.parseInt(props.getProperty("evaluator.timeout"));
        this.dropin = new File(props.getProperty("evaluator.dropin"));
        
        compilerArgs = new LinkedList<>();
        for (String arg : props.getProperty("evaluator.compiler").split(" ")) {
            compilerArgs.add(arg);
        }
        this.compilerArgs = Collections.unmodifiableList(compilerArgs);
        
        this.testSrc = new File(props.getProperty("evaluator.test.src"));
        
        this.fitnessSrc = new File(props.getProperty("evaluator.fitness.src"));
        this.fitnessMeasures = Integer.parseInt(props.getProperty("evaluator.fitness.measures"));
        this.sleepBeforeFitness = Long.parseLong(props.getProperty("evaluator.fitness.sleepBefore"));
    }
    
    public long getSeed() {
        return seed;
    }
    
    public boolean getSaveGenerations() {
        return saveGenerations;
    }
    
    public int getGenerations() {
        return generations;
    }
    
    public int getPopulationSize() {
        return populationSize;
    }
    
    public int getElitism() {
        return elitism;
    }
    
    public int getInitialMutations() {
        return initialMutations;
    }
    
    public List<String> getMutations() {
        return mutations;
    }
    
    public int getCleanFrequency() {
        return cleanFrequency;
    }
    
    public double getCleanThreshold() {
        return cleanThreshold;
    }
    
    public int getTimeout() {
        return timeout;
    }

    public File getDropin() {
        return dropin;
    }
    
    public List<String> getCompilerArgs() {
        return compilerArgs;
    }
    
    public File getTestSrc() {
        return testSrc;
    }
    
    public File getFitnessSrc() {
        return fitnessSrc;
    }
    
    public int getFitnessMeasures() {
        return fitnessMeasures;
    }

    public long getSleepBeforeFitness() {
        return sleepBeforeFitness;
    }
    
}
