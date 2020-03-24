package net.ssehub.mutator.evaluation;

import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.fitness.Fitness;

public abstract class Evaluator {

    public static final Fitness RUNTIME_ERROR = null;
    
    public abstract TestResult test(IMutant mutant);
    
    public abstract Fitness measureFitness(IMutant mutant);
    
}
