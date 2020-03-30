package net.ssehub.mutator.mutation.fitness;

class ParetoFitnessComparator implements IFitnessComparator {

    @Override
    public boolean isLower(Fitness f1, Fitness f2) {
        if (f1.numValues() != f2.numValues())
            throw new IllegalArgumentException("Fitness values have different number of objectives");

        boolean oneGreater = false;

        for (int i = 0; i < f1.numValues(); i++) {
            if (f1.getValue(i) > f2.getValue(i))
                return false;

            if (f2.getValue(i) > f1.getValue(i)) {
                oneGreater = true;
            }
        }

        return oneGreater;
    }

    @Override
    public double toSingleValue(Fitness fitness) {
        // TODO: simply add the values?

        double sum = 0.0;
        for (int i = 0; i < fitness.numValues(); i++) {
            sum += fitness.getValue(i);
        }
        return sum;
    }

}
