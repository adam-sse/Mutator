package net.ssehub.mutator;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class BaseConfig {
    
    private File execDir;
    
    private boolean saveIterations;
    
    private int timeout;
    
    private String dropin;
    
    private List<String> compilerArgs;
    
    private File testSrc;
    
    private File fitnessSrc;
    
    private int fitnessMeasures;
    
    private long sleepBeforeFitness;
    
    public BaseConfig(Properties props) {
        this.saveIterations = Boolean.parseBoolean(props.getProperty("mutator.saveIterations"));
        
        this.timeout = Integer.parseInt(props.getProperty("evaluator.timeout"));
        this.dropin = props.getProperty("evaluator.dropin");
        
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
    
    public File getExecDir() {
        return execDir;
    }
    
    void setExecDir(File execDir) {
        this.execDir = execDir;
    }
    
    public File getEvalDir() {
        return new File(this.execDir, "evaluation");
    }
    
    public boolean getSaveIterations() {
        return saveIterations;
    }
    
    public int getTimeout() {
        return timeout;
    }

    public String getDropin() {
        return dropin;
    }
    
    public List<String> getCompilerArgs() {
        return compilerArgs;
    }
    
    public File getTestSrc() {
        return testSrc;
    }
    
    void setTestSrc(File testSrc) {
        this.testSrc = testSrc;
    }
    
    public File getFitnessSrc() {
        return fitnessSrc;
    }
    
    void setFitnessSrc(File fitnessSrc) {
        this.fitnessSrc = fitnessSrc;
    }
    
    public int getFitnessMeasures() {
        return fitnessMeasures;
    }

    public long getSleepBeforeFitness() {
        return sleepBeforeFitness;
    }
    
}
