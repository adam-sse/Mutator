package net.ssehub.mutator.mutation.pattern_based;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.ssehub.mutator.Configuration;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.evaluation.Evaluator;
import net.ssehub.mutator.evaluation.EvaluatorFactory;
import net.ssehub.mutator.evaluation.TestResult;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.IMutator;
import net.ssehub.mutator.mutation.pattern_based.patterns.IOpportunity;
import net.ssehub.mutator.mutation.pattern_based.patterns.LoopUnrolling;

public class Mutator implements IMutator {

    private Configuration config;
    
    private Evaluator evaluator;
    
    private Map<String, Double> fitnessStore;
    
    private String unmodifiedId;
    
    private List<IOpportunity> opportunities;
    
    public Mutator(Configuration config) {
        this.config = config;
    }
    
    private void init() {
        this.fitnessStore = new HashMap<>();
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
        
        double initialFitness = evaluator.measureFitness(initial);
        fitnessStore.put(initial.getId(), initialFitness);
        
        mutantList.insertMutant(initial, initialFitness);
        
        System.out.println("Original fitness: " + initialFitness);
        
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
                if (config.getSaveGenerations()) {
                    java.io.File dir = new java.io.File(String.format(Locale.ROOT, "iteration_%03d", iteration));
                    dir.mkdir();
                    java.io.File out = new java.io.File(dir, "mutant_" + neighbor.getId() + ".c");
                    try {
                        neighbor.write(out);
                    } catch (IOException e) {
                        e.printStackTrace(System.out);
                    }
                }
                
                TestResult testResult = this.evaluator.test(neighbor);
                if (testResult == TestResult.PASS) {
                    
                    double fitness = this.evaluator.measureFitness(neighbor);
                    fitnessStore.put(neighbor.getId(), fitness);
                    System.out.println(neighbor.getId() + ": " + fitness);
                    
                    if (fitness > mutantList.getTopFitness()) {
                        System.out.println(" -> " + neighbor.getId() + " is better than "
                                + mutantList.getTopMutant().getId());
                        improved = true;
                    }
                    
                    mutantList.insertMutant(neighbor, fitness);
                    
                } else {
                    System.out.println(neighbor.getId() + " " + testResult);
                }
            }
            
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
    public Double getFitness(String mutantId) {
        return fitnessStore.get(mutantId);
    }

    @Override
    public void printStatistics() {
        System.out.println("TODO"); // TODO
    }

    @Override
    public String getUnmodifiedId() {
        return unmodifiedId;
    }

}
