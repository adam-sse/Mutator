package net.ssehub.mutator.evaluation;

import net.ssehub.mutator.mutation.Mutant;

public abstract class Evaluator {

    public abstract TestResult test(Mutant mutant);
    
    public abstract double measureFitness(Mutant mutant);
    
}
