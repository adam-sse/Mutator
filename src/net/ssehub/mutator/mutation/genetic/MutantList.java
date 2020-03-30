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

    public MutantList() {
        this.mutants = new LinkedList<>();
    }

    public Mutant getMutant(int index) {
        return this.mutants.get(index);
    }

    public boolean addMutant(Mutant mutant) {
        if (!this.mutants.contains(mutant)) {
            this.mutants.add(mutant);
            return true;
        }
        return false;
    }

    public void removeMutant(int index) {
        this.mutants.remove(index);
    }

    public int getSize() {
        return this.mutants.size();
    }

    public void sort(IFitnessStore fitness) {
        IFitnessComparator comparator = FitnessComparatorFactory.get();

        // bubble sort
        boolean changed;
        do {
            changed = false;

            for (int i = 0; i < this.mutants.size() - 1; i++) {
                Mutant mi = this.mutants.get(i);
                Mutant mi1 = this.mutants.get(i + 1);
                if (comparator.isLower(fitness.getFitness(mi.getId()), fitness.getFitness(mi1.getId()))) {
                    this.mutants.set(i, mi1);
                    this.mutants.set(i + 1, mi);

                    changed = true;
                }
            }
        } while (changed);
    }

    public List<IMutant> convertToList() {
        return new ArrayList<>(this.mutants);
    }

    @Override
    public Iterator<Mutant> iterator() {
        return this.mutants.iterator();
    }

}
