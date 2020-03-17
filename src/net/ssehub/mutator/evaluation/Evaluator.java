package net.ssehub.mutator.evaluation;

import net.ssehub.mutator.mutation.genetic.Mutant;

public abstract class Evaluator {

    public static final double RUNTIME_ERROR = -10000;
    
    public abstract TestResult test(Mutant mutant);
    
    public abstract double measureFitness(Mutant mutant);
    
}
