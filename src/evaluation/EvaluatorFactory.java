package evaluation;

import main.Configuration;

public class EvaluatorFactory {

    public static Evaluator create(Configuration config) {
        String os = System.getProperty("os.name", "generic").toLowerCase();
        if (os.startsWith("win")) {
            System.out.println("WARNING: using DummyEvaluator on Windows");
            return new DummyEvaluator(config);
        } else {
            return new LinuxEvaluator(config);
        }
    }
    
}
