package net.ssehub.mutator.mutation.pattern_based;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.evaluation.Evaluator;
import net.ssehub.mutator.evaluation.EvaluatorFactory;
import net.ssehub.mutator.mutation.AbstractMutator;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.pattern_based.patterns.IOpportunity;
import net.ssehub.mutator.mutation.pattern_based.patterns.LoopUnrolling;

public class PatternBasedMutator extends AbstractMutator {

    private PatternBasedConfiguration config;
    
    private Evaluator evaluator;
    
    private String unmodifiedId;
    
    private List<IOpportunity> opportunities;
    
    public PatternBasedMutator(PatternBasedConfiguration config) {
        this.config = config;
    }
    
    private void init() {
        this.evaluator = EvaluatorFactory.create(config);
    }

    @Override
    public List<IMutant> run(File originalAst) {
        init();
        
        System.out.println();
        System.out.println("Initialization");
        System.out.println("--------------");
        
        this.opportunities = new ArrayList<>();
        opportunities.addAll(LoopUnrolling.findOpportunities(originalAst));
        
        System.out.println("Opportunities:");
        for (IOpportunity oppo : opportunities) {
            System.out.println(" * " + oppo);
        }
        System.out.println();
        
        TopXMutants mutantList = new TopXMutants(5);
        
        Mutant initial = new Mutant(opportunities);
        this.unmodifiedId = initial.getId();
        initial.apply(originalAst);
        
        Double initialFitness = evaluate(initial, evaluator, false);
        if (initialFitness == null) {
            System.out.println("ERROR: Initial mutant doesn't pass");
            return new LinkedList<>();
        }
        
        System.out.println("Original fitness: " + initialFitness);
        mutantList.insertMutant(initial, initialFitness);
        
        int iteration = 0;
        boolean improved;
        do {
            iteration++;
            
            System.out.println();
            System.out.printf(Locale.ROOT, "Iteration %03d\n", iteration);
            System.out.println("-------------");
            
            improved = false;
            List<Mutant> neighbors = generateNeighbors(mutantList.getTopMutant());
            System.out.println("Generated " + neighbors.size() + " neighbors");
            
            for (Mutant neighbor : neighbors) {
                neighbor.apply(originalAst);
                if (config.getSaveIterations()) {
                    java.io.File dir = new java.io.File(String.format(Locale.ROOT, "iteration_%03d", iteration));
                    dir.mkdir();
                    java.io.File out = new java.io.File(dir, "mutant_" + neighbor.getId() + ".c");
                    try {
                        neighbor.write(out);
                    } catch (IOException e) {
                        e.printStackTrace(System.out);
                    }
                }
                
                Double fitness = evaluate(neighbor, evaluator, true);
                if (fitness != null && fitness > mutantList.getTopFitness()) {
                    System.out.println(" -> " + neighbor.getId() + " is better than "
                            + mutantList.getTopMutant().getId());
                    improved = true;
                }
            }
            
            setBestInIteration(iteration, mutantList.getTopMutant());
            
        } while (improved);
        
        return mutantList.toList();
    }
    
    private List<Mutant> generateNeighbors(Mutant base) {
        List<Mutant> result = new LinkedList<>();
        
        for (int i = 0; i < this.opportunities.size(); i++) {
            IOpportunity oppo = this.opportunities.get(i);
            int paramBase = base.getParams(i);
            
            if (paramBase - 1 >= oppo.getMinParam()) {
                Mutant newNeighbor = new Mutant(base);
                newNeighbor.setParam(i, paramBase - 1);
                result.add(newNeighbor);
            }
            
            if (paramBase + 1 <= oppo.getMaxParam()) {
                Mutant newNeighbor = new Mutant(base);
                newNeighbor.setParam(i, paramBase + 1);
                result.add(newNeighbor);
            }
        }
        
        return result;
    }
    
    @Override
    public String getUnmodifiedId() {
        return unmodifiedId;
    }

}
