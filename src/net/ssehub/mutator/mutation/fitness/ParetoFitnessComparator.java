package net.ssehub.mutator.mutation.fitness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ssehub.mutator.mutation.IFitnessStore;
import net.ssehub.mutator.mutation.IMutant;

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

    @Override
    public <T extends IMutant> List<Integer> sort(List<T> mutants, IFitnessStore fitnessStore) {
        List<Fitness> fitness = new ArrayList<>(mutants.size());
        for (T mutant : mutants) {
            fitness.add(fitnessStore.getFitness(mutant.getId()));
        }

        // 1) fast, non-dominated sorting from NSGA-II
        // this creates a list of fronts (= sets of "equal" (i.e. don't dominate each
        // other ) solutions)
        // the first front (index 0) is the pareto-optimal front of 'mutants'

        LinkedList<List<Integer>> fronts = new LinkedList<>(); // contains indices in 'mutants'
        fronts.add(new LinkedList<>()); // first front

        List<Integer> numDominatedBy = new ArrayList<>(mutants.size()); // number of mutants that dominate [i]
        List<Set<Integer>> dominates = new ArrayList<>(mutants.size()); // mutants that are dominated by [i]

        for (int p = 0; p < mutants.size(); p++) {
            dominates.add(new HashSet<>()); // S[p] = empty set
            numDominatedBy.add(0); // n[p] = 0

            for (int q = 0; q < mutants.size(); q++) {
                if (isLower(fitness.get(q), fitness.get(p))) {
                    // p dominates q
                    dominates.get(p).add(q);
                } else if (isLower(fitness.get(p), fitness.get(q))) {
                    // q dominates p
                    numDominatedBy.set(p, numDominatedBy.get(p) + 1);
                }
            }

            if (numDominatedBy.get(p) == 0) {
                // p belongs to the first front
                fronts.get(0).add(p);
            }
        }

        while (!fronts.getLast().isEmpty()) {
            List<Integer> nextFront = new LinkedList<>();

            for (int p : fronts.getLast()) {
                for (int q : dominates.get(p)) {
                    numDominatedBy.set(q, numDominatedBy.get(q) - 1);
                    if (numDominatedBy.get(q) == 0) {
                        // q belongs to the next front
                        nextFront.add(q);
                    }
                }
            }

            fronts.add(nextFront);
        }

        // 2) diversity preservation (sorting inside the fronts) from NSGA-II
        int dimensions = fitness.get(0).numValues();
        List<Double> distance = new ArrayList<>(mutants.size()); // store the crowding distance for each mutant
        for (int i = 0; i < mutants.size(); i++) {
            distance.add(0.0);
        }

        for (List<Integer> front : fronts) {
            if (front.isEmpty()) {
                continue;
            }

            for (int i = 0; i < dimensions; i++) {
                int dim = i;
                front.sort((i1, i2) -> Double.compare(fitness.get(i1).getValue(dim), fitness.get(i2).getValue(dim)));

                int minI = front.get(0);
                int maxI = front.get(front.size() - 1);

                distance.set(minI, Double.POSITIVE_INFINITY);
                distance.set(maxI, Double.POSITIVE_INFINITY);

                double fMin = fitness.get(minI).getValue(dim);
                double fMax = fitness.get(maxI).getValue(dim);

                for (int j = 1; j < front.size() - 1; j++) {
                    double prev = fitness.get(front.get(j - 1)).getValue(dim);
                    double next = fitness.get(front.get(j + 1)).getValue(dim);

                    double add = (next - prev) / (fMax - fMin);
                    distance.set(front.get(j), distance.get(front.get(j)) + add);
                }
            }

            front.sort((i1, i2) -> Double.compare(distance.get(i2), distance.get(i1)));
        }

        // 3) now fill 'mutants' up according to the order given by a) the fronts and b)
        // the diversity preservation
        List<Integer> ranks = new ArrayList<>(mutants.size());

        List<T> clone = new ArrayList<>(mutants);
        mutants.clear();
        int rank = 1;
        for (List<Integer> front : fronts) {
            for (int index : front) {
                mutants.add(clone.get(index));
                ranks.add(rank);
            }
            rank++;
        }

        return ranks;
    }

}
