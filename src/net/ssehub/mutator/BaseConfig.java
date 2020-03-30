package net.ssehub.mutator;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class BaseConfig {

    private File execDir;

    private boolean saveIterations;

    private String fitnessComparison;

    private double[] fitnessWeights;

    private int timeout;

    private String dropin;

    private List<String> compilerArgs;

    private File testSrc;

    private File fitnessSrc;

    private int fitnessMeasures;

    private long sleepBeforeFitness;

    private String dotExe;

    public BaseConfig(Properties props) {
        this.saveIterations = Boolean.parseBoolean(props.getProperty("mutator.saveIterations"));
        this.fitnessComparison = props.getProperty("mutator.fitness.comparison");

        if (props.containsKey("mutator.fitness.weights")) {
            String[] tmp = props.getProperty("mutator.fitness.weights").split(",");
            this.fitnessWeights = new double[tmp.length];
            for (int i = 0; i < tmp.length; i++) {
                this.fitnessWeights[i] = Double.parseDouble(tmp[i]);
            }
        }

        this.timeout = Integer.parseInt(props.getProperty("evaluator.timeout"));
        this.dropin = props.getProperty("evaluator.dropin");

        this.compilerArgs = new LinkedList<>();
        for (String arg : props.getProperty("evaluator.compiler").split(" ")) {
            this.compilerArgs.add(arg);
        }
        this.compilerArgs = Collections.unmodifiableList(this.compilerArgs);

        this.testSrc = new File(props.getProperty("evaluator.test.src"));

        this.fitnessSrc = new File(props.getProperty("evaluator.fitness.src"));
        this.fitnessMeasures = Integer.parseInt(props.getProperty("evaluator.fitness.measures"));
        this.sleepBeforeFitness = Long.parseLong(props.getProperty("evaluator.fitness.sleepBefore"));

        this.dotExe = props.getProperty("visualization.dotExe");
    }

    public File getExecDir() {
        return this.execDir;
    }

    void setExecDir(File execDir) {
        this.execDir = execDir;
    }

    public File getEvalDir() {
        return new File(this.execDir, "evaluation");
    }

    public boolean getSaveIterations() {
        return this.saveIterations;
    }

    public String getFitnessComparison() {
        return this.fitnessComparison;
    }

    public double[] getFitnessWeights() {
        return this.fitnessWeights;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public String getDropin() {
        return this.dropin;
    }

    public List<String> getCompilerArgs() {
        return this.compilerArgs;
    }

    public File getTestSrc() {
        return this.testSrc;
    }

    void setTestSrc(File testSrc) {
        this.testSrc = testSrc;
    }

    public File getFitnessSrc() {
        return this.fitnessSrc;
    }

    void setFitnessSrc(File fitnessSrc) {
        this.fitnessSrc = fitnessSrc;
    }

    public int getFitnessMeasures() {
        return this.fitnessMeasures;
    }

    public long getSleepBeforeFitness() {
        return this.sleepBeforeFitness;
    }

    public String getDotExe() {
        return this.dotExe;
    }

}
