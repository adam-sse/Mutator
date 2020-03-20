package net.ssehub.mutator.mutation.pattern_based;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.ssehub.mutator.BaseConfiguration;

public class PatternBasedConfiguration extends BaseConfiguration {

    private List<String> patterns;
    
    public PatternBasedConfiguration(Properties props) {
        super(props);
        
        String[] tmp = props.getProperty("mutator.patterns").split(",");
        this.patterns = new ArrayList<>(tmp.length);
        for (String mutation : tmp) {
            this.patterns.add(mutation.trim());
        }
    }
    
    public List<String> getPatterns() {
        return patterns;
    }

}
