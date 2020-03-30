package net.ssehub.mutator.mutation.pattern_based;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.mutation.fitness.FitnessComparatorFactory;
import net.ssehub.mutator.mutation.fitness.IFitnessComparator;

public class TopXMutants {

    private IFitnessComparator comparator;

    private List<Mutant> mutants;

    private List<Fitness> fitness;

    private int numMutants;

    public TopXMutants(int numMutants) {
        this.numMutants = numMutants;
        this.mutants = new LinkedList<>();
        this.fitness = new LinkedList<>();
        this.comparator = FitnessComparatorFactory.get();
    }

    public void insertMutant(Mutant mutant, Fitness fitness) {
        boolean inserted = false;
        for (int i = 0; i < this.mutants.size(); i++) {
            if (this.comparator.isLower(this.fitness.get(i), fitness)) {
                this.mutants.add(i, mutant);
                this.fitness.add(i, fitness);
                inserted = true;
                break;
            }
        }

        if (!inserted && this.mutants.size() < this.numMutants) {
            this.mutants.add(mutant);
            this.fitness.add(fitness);
        }

        while (this.mutants.size() > this.numMutants) {
            this.mutants.remove(this.mutants.size() - 1);
            this.fitness.remove(this.fitness.size() - 1);
        }
    }

    public void clear() {
        this.mutants.clear();
        this.fitness.clear();
    }

    public Mutant getTopMutant() {
        return this.mutants.get(0);
    }

    public Fitness getTopFitness() {
        return this.fitness.get(0);
    }

    public List<IMutant> toList() {
        return new ArrayList<>(this.mutants);
    }

}
