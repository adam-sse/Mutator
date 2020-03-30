package net.ssehub.mutator.mutation.genetic;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.operations.AstCloner;
import net.ssehub.mutator.ast.operations.AstPrettyPrinter;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.genetic.mutations.Mutation;

public class Mutant implements IMutant {

    private String id;

    private List<Mutation> mutations;

    private File ast;

    public Mutant(String id, File original) {
        this.id = id;
        this.ast = new AstCloner(null, true).visitFile(original);
        this.mutations = new LinkedList<>();
    }

    public File getAst() {
        return this.ast;
    }

    public boolean addMutation(Mutation mutation) {
        if (this.mutations.contains(mutation))
            return false;

        boolean applies = mutation.apply(this.ast);

        if (applies) {
            this.mutations.add(mutation);
        }

        return applies;
    }

    public List<Mutation> getMutations() {
        return this.mutations;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void write(java.io.File destination) throws IOException {
        try (FileWriter out = new FileWriter(destination)) {
            out.write("/*\n * Mutant " + this.id + "\n");
            for (Mutation mutation : this.mutations) {
                out.write(" *   " + mutation.toString() + "\n");
                if (mutation.getDiff() != null) {
                    for (String line : mutation.getDiff()) {
                        out.write(" *     " + line + "\n");
                    }
                }
            }
            out.write(" */\n\n");
            out.write(this.ast.accept(new AstPrettyPrinter(true)));
        }
    }

    public Mutant clone(String cloneId) {
        Mutant clone = new Mutant(cloneId, this.ast);
        clone.mutations = new LinkedList<>(this.mutations);

        return clone;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        for (Mutation m : this.mutations) {
            sj.add(m.toString());
        }
        return "Mutant " + getId() + " [" + sj.toString() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj instanceof Mutant) {
            Mutant other = (Mutant) obj;
            equal = this.mutations.equals(other.mutations);
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return 223 * this.mutations.hashCode();
    }

}
