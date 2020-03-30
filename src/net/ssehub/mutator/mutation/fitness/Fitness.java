package net.ssehub.mutator.mutation.fitness;

import java.util.Locale;
import java.util.StringJoiner;

public class Fitness {

    private double[] values;

    public Fitness(double... values) {
        this.values = values;
    }

    public double getValue(int index) {
        return values[index];
    }

    public int numValues() {
        return values.length;
    }

    public double[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(";");
        for (double d : values) {
            sj.add(String.format(Locale.ROOT, "%4.2f", d));
        }
        return sj.toString();
    }

}
