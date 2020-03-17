package net.ssehub.mutator.mutation.pattern_based;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
        
        Mutant currentBest = new Mutant(opportunities);
        this.unmodifiedId = currentBest.getId();
        currentBest.apply(originalAst);
        
        double currentBestFitness = evaluator.measureFitness(currentBest);
        fitnessStore.put(currentBest.getId(), currentBestFitness);
        
        System.out.println("Original fitness: " + currentBestFitness);
        
        boolean improved;
        do {
            System.out.println();
            System.out.println("Next Round");
            System.out.println("----------");
            
            improved = false;
            List<Mutant> neighbors = generateNeighbors(currentBest);
            System.out.println("Generated " + neighbors.size() + " neighbors");
            
            for (Mutant neighbor : neighbors) {
                neighbor.apply(originalAst);
                
                TestResult testResult = this.evaluator.test(neighbor);
                if (testResult == TestResult.PASS) {
                    
                    double fitness = this.evaluator.measureFitness(neighbor);
                    fitnessStore.put(neighbor.getId(), fitness);
                    System.out.println(neighbor.getId() + ": " + fitness);
                    
                    if (fitness > currentBestFitness) {
                        System.out.println(neighbor.getId() + " is better than " + currentBest.getId());
                        improved = true;
                        currentBest = neighbor;
                        currentBestFitness = fitness;
                    }
                } else {
                    System.out.println(neighbor.getId() + " failed tests");
                }
            }
            
        } while (improved);
        
        return new LinkedList<>(Arrays.asList(currentBest));
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