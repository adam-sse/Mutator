package net.ssehub.mutator.evaluation;

import net.ssehub.mutator.mutation.IMutant;

public abstract class Evaluator {

    public static final double RUNTIME_ERROR = -10000;
    
    public abstract TestResult test(IMutant mutant);
    
    public abstract double measureFitness(IMutant mutant);
    
}
