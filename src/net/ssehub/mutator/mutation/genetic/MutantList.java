package net.ssehub.mutator.mutation.genetic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.mutation.IFitnessStore;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.fitness.FitnessComparatorFactory;
import net.ssehub.mutator.mutation.fitness.IFitnessComparator;

public class MutantList implements Iterable<Mutant> {

    private List<Mutant> mutants;

    private List<Integer> ranks;

    public MutantList() {
        this.mutants = new LinkedList<>();
    }

    public Mutant getMutant(int index) {
        return this.mutants.get(index);
    }

    public int getRank(int index) {
        if (hasRanks())
            return this.ranks.get(index);
        else
            return -1;
    }

    public boolean hasRanks() {
        return this.ranks != null;
    }

    public boolean addMutant(Mutant mutant) {
        if (!this.mutants.contains(mutant)) {
            this.ranks = null;
            this.mutants.add(mutant);
            return true;
        }
        return false;
    }

    public void removeMutant(int index) {
        this.mutants.remove(index);

        if (hasRanks()) {
            this.ranks.remove(index);
        }
    }

    public int getSize() {
        return this.mutants.size();
    }

    public void sort(IFitnessStore fitness) {
        IFitnessComparator comparator = FitnessComparatorFactory.get();

        this.ranks = comparator.sort(this.mutants, fitness);
    }

    public List<IMutant> convertToList() {
        return new ArrayList<>(this.mutants);
    }

    @Override
    public Iterator<Mutant> iterator() {
        return this.mutants.iterator();
    }

}
