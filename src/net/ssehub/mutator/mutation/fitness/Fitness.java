package net.ssehub.mutator.mutation.fitness;

import java.util.Locale;
import java.util.StringJoiner;

public class Fitness {

    private double[] values;

    public Fitness(double... values) {
        this.values = values;
    }

    public double getValue(int index) {
        return this.values[index];
    }

    public int numValues() {
        return this.values.length;
    }

    public double[] getValues() {
        return this.values;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(";");
        for (double d : this.values) {
            sj.add(String.format(Locale.ROOT, "%4.2f", d));
        }
        return sj.toString();
    }

}
