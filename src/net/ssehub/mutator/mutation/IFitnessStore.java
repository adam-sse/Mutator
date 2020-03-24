package net.ssehub.mutator.mutation;

import net.ssehub.mutator.mutation.fitness.Fitness;

public interface IFitnessStore {
    
    public Fitness getFitness(String mutantId);

}
