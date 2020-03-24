package net.ssehub.mutator.mutation.fitness;

public class WeightedFitnessComparator implements IFitnessComparator {

    private double[] weights;

    public WeightedFitnessComparator(double... weights) {
        this.weights = weights;
    }

    @Override
    public double toSingleValue(Fitness fitness) {
        if (fitness.numValues() != weights.length) {
            throw new IllegalArgumentException("Fitness objectives don't match weights");
        }
        
        double sum = 0.0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * fitness.getValue(i);
        }
        return sum;
    }
    
    @Override
    public boolean isLower(Fitness f1, Fitness f2) {
        return toSingleValue(f1) < toSingleValue(f2);
    }
    
}
