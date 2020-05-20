package net.ssehub.mutator.mutation.fitness;

import java.util.ArrayList;
import java.util.List;

import net.ssehub.mutator.mutation.IFitnessStore;
import net.ssehub.mutator.mutation.IMutant;

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

    @Override
    public <T extends IMutant> List<Integer> sort(List<T> mutants, IFitnessStore fitnessStore) {
        // bubble sort
        boolean changed;
        do {
            changed = false;

            for (int i = 0; i < mutants.size() - 1; i++) {
                T mi = mutants.get(i);
                T mi1 = mutants.get(i + 1);
                if (isLower(fitnessStore.getFitness(mi.getId()), fitnessStore.getFitness(mi1.getId()))) {
                    mutants.set(i, mi1);
                    mutants.set(i + 1, mi);

                    changed = true;
                }
            }
        } while (changed);

        List<Integer> ranks = new ArrayList<>(mutants.size());
        int rank = 1;
        ranks.add(rank);

        for (int i = 1; i < mutants.size(); i++) {
            if (isLower(fitnessStore.getFitness(mutants.get(i).getId()),
                    fitnessStore.getFitness(mutants.get(i - 1).getId()))) {
                rank++;
            }

            ranks.add(rank);
        }

        return ranks;
    }

}
