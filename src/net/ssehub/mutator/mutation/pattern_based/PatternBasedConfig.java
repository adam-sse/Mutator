package net.ssehub.mutator.mutation.pattern_based;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.ssehub.mutator.BaseConfig;

public class PatternBasedConfig extends BaseConfig {

    private List<String> patterns;

    private int[] startParams;

    private double initialTemperature;

    private int maxAnnealingIterations;

    private Double coolingFactor;

    private int randomSearchIterations;

    public PatternBasedConfig(Properties props) {
        super(props);

        String[] tmp = props.getProperty("mutator.patterns").split(",");
        this.patterns = new ArrayList<>(tmp.length);
        for (String mutation : tmp) {
            this.patterns.add(mutation.trim());
        }

        if (props.containsKey("mutator.startParams")) {
            tmp = props.getProperty("mutator.startParams").split("\\.");
            this.startParams = new int[tmp.length];
            int i = 0;
            for (String param : tmp) {
                this.startParams[i++] = Integer.parseInt(param);
            }
        }

        if (props.containsKey("mutator.initialTemperature")) {
            this.initialTemperature = Double.parseDouble(props.getProperty("mutator.initialTemperature"));
            this.maxAnnealingIterations = Integer.parseInt(props.getProperty("mutator.annealingIterations"));

            if (props.containsKey("mutator.coolingFactor")) {
                this.coolingFactor = Double.parseDouble(props.getProperty("mutator.coolingFactor"));
            }
        }

        if (props.containsKey("mutator.randomSearchIterations")) {
            this.randomSearchIterations = Integer.parseInt(props.getProperty("mutator.randomSearchIterations"));
        }
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public int[] getStartParams() {
        return startParams;
    }

    public double getInitialTemperature() {
        return initialTemperature;
    }

    public int getMaxAnnealingIterations() {
        return maxAnnealingIterations;
    }

    public Double getCoolingFactor() {
        return coolingFactor;
    }

    public int getRandomSearchIterations() {
        return randomSearchIterations;
    }

}
