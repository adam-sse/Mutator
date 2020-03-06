package mutation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import mutation.mutations.Mutation;
import parsing.ast.File;

public class Mutant {
    
    private String id;
    
    private List<Mutation> mutations;
    
    private File ast;
    
    public Mutant(String id, File original) {
        this.id = id;
        this.ast = (File) original.clone(null);
        this.mutations = new LinkedList<>();
    }
    
    public File getAst() {
        return ast;
    }
    
    public boolean addMutation(Mutation mutation) {
        boolean applies = mutation.apply(this.ast);
        
        if (applies) {
            this.mutations.add(mutation);
        }
        
        return applies;
    }
    
    public List<Mutation> getMutations() {
        return mutations;
    }
    
    public String getId() {
        return id;
    }
    
    public void write(java.io.File destination) throws IOException {
        try (FileWriter out = new FileWriter(destination)) {
            out.write("/*\n * Mutant " + id + "\n");
            for (Mutation mutation : mutations) {
                out.write(" *   " + mutation.toString() + "\n");
                if (mutation.getDiff() != null) {
                    for (String line : mutation.getDiff()) {
                        out.write(" *     " + line + "\n");
                    }
                }
            }
            out.write(" */\n\n");
            out.write(ast.print(""));
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
        for (Mutation m : mutations) {
            sj.add(m.toString());
        }
        return "Mutant " + getId() + " [" + sj.toString() + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof Mutant) {
            Mutant other = (Mutant) obj;
            equal = mutations.equals(other.mutations);
        }
        
        
        return equal;
    }

}
