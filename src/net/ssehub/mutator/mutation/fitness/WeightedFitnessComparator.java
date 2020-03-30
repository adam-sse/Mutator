package net.ssehub.mutator.mutation.fitness;

class WeightedFitnessComparator implements IFitnessComparator {

    private double[] weights;

    public WeightedFitnessComparator(double... weights) {
        this.weights = weights;
    }

    @Override
    public double toSingleValue(Fitness fitness) {
        if (fitness.numValues() != this.weights.length)
            throw new IllegalArgumentException("Fitness objectives don't match weights");

        double sum = 0.0;
        for (int i = 0; i < this.weights.length; i++) {
            sum += this.weights[i] * fitness.getValue(i);
        }
        return sum;
    }

    @Override
    public boolean isLower(Fitness f1, Fitness f2) {
        return toSingleValue(f1) < toSingleValue(f2);
    }

}
