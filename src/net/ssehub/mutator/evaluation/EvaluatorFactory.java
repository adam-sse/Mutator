package net.ssehub.mutator.evaluation;

import net.ssehub.mutator.BaseConfiguration;

public class EvaluatorFactory {

    public static Evaluator create(BaseConfiguration config) {
        String os = System.getProperty("os.name", "generic").toLowerCase();
        if (os.startsWith("win")) {
            System.out.println("WARNING: using DummyEvaluator on Windows");
            return new DummyEvaluator(config);
        } else {
            return new LinuxEvaluator(config);
        }
    }
    
}
