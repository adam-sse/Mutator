package net.ssehub.mutator.mutation.pattern_based;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.ssehub.mutator.BaseConfiguration;

public class PatternBasedConfiguration extends BaseConfiguration {

    private List<String> patterns;
    
    private int maxAnnealingIterations;
    
    private double initialTemperature;
    
    public PatternBasedConfiguration(Properties props) {
        super(props);
        
        String[] tmp = props.getProperty("mutator.patterns").split(",");
        this.patterns = new ArrayList<>(tmp.length);
        for (String mutation : tmp) {
            this.patterns.add(mutation.trim());
        }
        
        if (props.containsKey("mutator.annealingIterations")) {
            this.maxAnnealingIterations = Integer.parseInt(props.getProperty("mutator.annealingIterations"));
            this.initialTemperature = Double.parseDouble(props.getProperty("mutator.initialTemperature"));
        }
    }
    
    public List<String> getPatterns() {
        return patterns;
    }
    
    public int getMaxAnnealingIterations() {
        return maxAnnealingIterations;
    }
    
    public double getInitialTemperature() {
        return initialTemperature;
    }
    
}
