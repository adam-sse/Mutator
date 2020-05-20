package net.ssehub.mutator.mutation.fitness;

import java.util.Comparator;
import java.util.List;

import net.ssehub.mutator.mutation.IFitnessStore;
import net.ssehub.mutator.mutation.IMutant;

public interface IFitnessComparator extends Comparator<Fitness> {

    public boolean isLower(Fitness f1, Fitness f2);

    public double toSingleValue(Fitness fitness);

    /**
     * Sorts the mutants list (side-effect)
     * 
     * @return A list of ranks for the mutants. Lower ranks are better. This
     *         indicates that solutions may have the same rank, although they are
     *         sorted after each other in the flat mutants array.
     */
    public <T extends IMutant> List<Integer> sort(List<T> mutants, IFitnessStore fitnessStore);

    @Override
    default int compare(Fitness o1, Fitness o2) {
        if (isLower(o1, o2))
            return -1;
        else if (isLower(o2, o1))
            return 1;
        else
            return 0;
    }

}
