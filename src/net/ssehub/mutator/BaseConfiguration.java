package net.ssehub.mutator;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class BaseConfiguration {
    
    private boolean saveIterations;
    
    private int timeout;
    
    private File dropin;
    
    private List<String> compilerArgs;
    
    private File testSrc;
    
    private File fitnessSrc;
    
    private int fitnessMeasures;
    
    private long sleepBeforeFitness;
    
    public BaseConfiguration(Properties props) {
        this.saveIterations = Boolean.parseBoolean(props.getProperty("mutator.saveIterations"));
        
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
    
    public boolean getSaveIterations() {
        return saveIterations;
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
