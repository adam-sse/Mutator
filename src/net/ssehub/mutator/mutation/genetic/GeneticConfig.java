package net.ssehub.mutator.mutation.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.ssehub.mutator.BaseConfig;

public class GeneticConfig extends BaseConfig {

    private long seed;
    
    private int generations;
    
    private int populationSize;
    
    private int elitism;
    
    private int initialMutations;
    
    private List<String> mutations;
    
    private int cleanFrequency;
    
    private double cleanThreshold;
    
    public GeneticConfig(Properties props) {
        super(props);
        
        this.seed = Long.parseLong(props.getProperty("mutator.seed"));
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
    }
    
    public long getSeed() {
        return seed;
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
    
}
