package net.ssehub.mutator.mutation.fitness;

import net.ssehub.mutator.BaseConfig;

public class FitnessComparatorFactory {

    private static IFitnessComparator instance;

    private FitnessComparatorFactory() {
    }

    public static void init(BaseConfig config) {
        switch (config.getFitnessComparison()) {
        case "weighted":
            instance = new WeightedFitnessComparator(config.getFitnessWeights());
            break;

        case "pareto":
            instance = new ParetoFitnessComparator();
            break;

        default:
            throw new IllegalArgumentException("Invalid fitness comparison: " + config.getFitnessComparison());
        }
    }

    public static IFitnessComparator get() {
        if (instance == null) {
            throw new IllegalStateException("Not yet initialized");
        }
        return instance;
    }

}
