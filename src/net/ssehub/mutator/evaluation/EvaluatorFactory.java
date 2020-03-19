package net.ssehub.mutator.evaluation;

import net.ssehub.mutator.BaseConfiguration;
import net.ssehub.mutator.util.Logger;

public class EvaluatorFactory {

    private static final Logger LOGGER = Logger.get(EvaluatorFactory.class.getSimpleName());
    
    public static Evaluator create(BaseConfiguration config) {
        String os = System.getProperty("os.name", "generic").toLowerCase();
        if (os.startsWith("win")) {
            LOGGER.println("WARNING: using DummyEvaluator on Windows");
            return new DummyEvaluator(config);
        } else {
            return new LinuxEvaluator(config);
        }
    }
    
}
