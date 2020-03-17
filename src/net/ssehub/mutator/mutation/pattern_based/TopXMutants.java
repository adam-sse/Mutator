package net.ssehub.mutator.mutation.pattern_based;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.mutation.IMutant;

public class TopXMutants {

    private List<Mutant> mutants;
    
    private List<Double> fitness;
    
    private int numMutants;
    
    public TopXMutants(int numMutants) {
        this.numMutants = numMutants;
        this.mutants = new LinkedList<>();
        this.fitness = new LinkedList<>();
    }
    
    public void insertMutant(Mutant mutant, double fitness) {
        boolean inserted = false;
        for (int i = 0; i < this.mutants.size(); i++) {
            if (fitness > this.fitness.get(i)) {
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
    
    public Mutant getTopMutant() {
        return mutants.get(0);
    }
    
    public double getTopFitness() {
        return fitness.get(0);
    }
    
    public List<IMutant> toList() {
        return new ArrayList<>(this.mutants);
    }
    
}