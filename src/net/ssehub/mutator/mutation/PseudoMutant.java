package net.ssehub.mutator.mutation;

import java.io.FileWriter;
import java.io.IOException;

import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.operations.AstPrettyPrinter;

public class PseudoMutant implements IMutant {

    private File ast;
    
    public PseudoMutant(File ast) {
        this.ast = ast;
    }
    
    @Override
    public String getId() {
        return "input";
    }

    @Override
    public void write(java.io.File destination) throws IOException {
        try (FileWriter out = new FileWriter(destination)) {
            out.write(ast.accept(new AstPrettyPrinter(true)));
        }
    }

}
