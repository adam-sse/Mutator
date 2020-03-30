package net.ssehub.mutator.mutation.fitness;

import java.util.Comparator;

public interface IFitnessComparator extends Comparator<Fitness> {

    public boolean isLower(Fitness f1, Fitness f2);

    public double toSingleValue(Fitness fitness);

    @Override
    default int compare(Fitness o1, Fitness o2) {
        if (isLower(o1, o2)) {
            return -1;
        } else if (isLower(o2, o1)) {
            return 1;
        } else {
            return 0;
        }
    }

}
