package net.ssehub.mutator.mutation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MutantList implements Iterable<Mutant> {

    private List<Mutant> mutants;
    
    public MutantList() {
        this.mutants = new LinkedList<>();
    }

    public Mutant getMutant(int index) {
        return mutants.get(index);
    }
    
    public boolean addMutant(Mutant mutant) {
        if (!mutants.contains(mutant)) {
            mutants.add(mutant);
            return true;
        }
        return false;
    }
    
    public void removeMutant(int index) {
        mutants.remove(index);
    }
    
    public int getSize() {
        return mutants.size();
    }
    
    public void sort(IFitnessStore fitness) {
        // bubble sort
        boolean changed;
        do {
            changed = false;
            
            for (int i = 0; i < mutants.size() - 1; i++) {
                Mutant mi = mutants.get(i);
                Mutant mi1 = mutants.get(i + 1);
                if (fitness.getFitness(mi.getId()) < fitness.getFitness(mi1.getId())) {
                    mutants.set(i, mi1);
                    mutants.set(i + 1, mi);
                    
                    changed = true;
                }
            }
            
        } while (changed);
    }
    
    @Override
    public Iterator<Mutant> iterator() {
        return mutants.iterator();
    }
    
}
