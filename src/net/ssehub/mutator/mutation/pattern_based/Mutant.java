package net.ssehub.mutator.mutation.pattern_based;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.operations.AstCloner;
import net.ssehub.mutator.ast.operations.AstPrettyPrinter;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.pattern_based.patterns.IOpportunity;

public class Mutant implements IMutant {

    private List<IOpportunity> opportunities;
    
    private List<Integer> params;
    
    private File ast;
    
    public Mutant(List<IOpportunity> opportunities) {
        this.opportunities = opportunities;
        this.params = new ArrayList<>(this.opportunities.size());
        for (int i = 0; i < this.opportunities.size(); i++) {
            this.params.add(this.opportunities.get(i).getDefaultParam());
        }
    }
    
    public Mutant(Mutant other) {
        this.opportunities = other.opportunities;
        this.params = new ArrayList<>(other.params);
    }
    
    public void setParam(int index, int value) {
        this.params.set(index, value);
    }
    
    public int getParams(int index) {
        return params.get(index);
    }
    
    public void apply(File originalAst) {
        this.ast = new AstCloner(null, true).visitFile(originalAst);
        
        for (int i = 0; i < this.opportunities.size(); i++) {
            this.opportunities.get(i).apply(params.get(i), ast);
        }
    }
    
    @Override
    public String getId() {
        StringJoiner sj = new StringJoiner(".");
        for (Integer param : params) {
            sj.add(param.toString());
        }
        return sj.toString();
    }
    
    @Override
    public String toString() {
        return "Mutant " + getId();
    }

    public void printToConsole() {
        System.out.print("/*\n * Mutant " + getId() + "\n");
        for (int i = 0; i < this.opportunities.size(); i++) {
            System.out.print(" *   " + this.opportunities.get(i).toString() + " param=" + this.params.get(i) + "\n");
        }
        System.out.print(" */\n\n");
        System.out.print(ast.accept(new AstPrettyPrinter(true)));
    }
    
    @Override
    public void write(java.io.File destination) throws IOException {
        try (FileWriter out = new FileWriter(destination)) {
            out.write("/*\n * Mutant " + getId() + "\n");
            for (int i = 0; i < this.opportunities.size(); i++) {
                out.write(" *   " + this.opportunities.get(i).toString() + " param=" + this.params.get(i) + "\n");
            }
            out.write(" */\n\n");
            out.write(ast.accept(new AstPrettyPrinter(true)));
        }
    }

}
