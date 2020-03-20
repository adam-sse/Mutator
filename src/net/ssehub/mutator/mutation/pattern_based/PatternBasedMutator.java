package net.ssehub.mutator.mutation.pattern_based;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.evaluation.EvaluatorFactory;
import net.ssehub.mutator.mutation.AbstractMutator;
import net.ssehub.mutator.mutation.IMutant;
import net.ssehub.mutator.mutation.pattern_based.patterns.IOpportunity;
import net.ssehub.mutator.util.Logger;

public class PatternBasedMutator extends AbstractMutator {

    private static final Logger LOGGER = Logger.get(PatternBasedMutator.class.getSimpleName());
    
    private PatternBasedConfiguration config;
    
    private String unmodifiedId;
    
    private List<IOpportunity> opportunities;
    
    private int iteration;
    
    public PatternBasedMutator(PatternBasedConfiguration config) {
        super(EvaluatorFactory.create(config));
        this.config = config;
    }
    
    @Override
    public List<IMutant> run(File originalAst) {
        LOGGER.println();
        LOGGER.println("Initialization");
        LOGGER.println("--------------");
        
        this.iteration = 0;
        
        this.opportunities = new ArrayList<>();
        // the order here matters, as mutations are applied in this order
        for (String pattern : config.getPatterns()) {
            try {
                Class<?> patternClass = Class.forName("net.ssehub.mutator.mutation.pattern_based.patterns." + pattern);
                
                
                @SuppressWarnings("unchecked")
                Collection<IOpportunity> oppos = (Collection<IOpportunity>)
                        patternClass.getMethod("findOpportunities", File.class).invoke(null, originalAst);
                this.opportunities.addAll(oppos);
                
            } catch (ReflectiveOperationException e) {
                LOGGER.logException(e);
            }
        }
        
        LOGGER.println("Opportunities:");
        for (IOpportunity oppo : opportunities) {
            LOGGER.println(" * " + oppo);
        }
        LOGGER.println();
        
        TopXMutants mutantList = new TopXMutants(5);
        
        Mutant initial = new Mutant(opportunities);
        this.unmodifiedId = initial.getId();
        initial.apply(originalAst);
        
        LOGGER.println("Original fitness:");
        Double initialFitness = evaluate(initial, false, true);
        if (initialFitness == null) {
            LOGGER.println("ERROR: Initial mutant doesn't pass");
            return new LinkedList<>();
        }
        
        mutantList.insertMutant(initial, initialFitness);
        
        if (config.getMaxAnnealingIterations() > 0) {
            simulatedAnnealing(originalAst, mutantList);
        } else {
            hillClimbing(originalAst, mutantList);
        }
        
        return mutantList.toList();
    }

    private void hillClimbing(File originalAst, TopXMutants mutantList) {
        boolean improved;
        do {
            iteration++;
            
            LOGGER.println();
            LOGGER.printf("Iteration %03d\n", iteration);
            LOGGER.println("-------------");
            
            improved = false;
            List<Mutant> neighbors = generateNeighbors(mutantList.getTopMutant());
            LOGGER.println("Generated " + neighbors.size() + " neighbors");
            
            for (Mutant neighbor : neighbors) {
                neighbor.apply(originalAst);
                if (config.getSaveIterations()) {
                    java.io.File dir = new java.io.File(config.getExecDir(),
                            String.format(Locale.ROOT, "iteration_%03d", iteration));
                    dir.mkdir();
                    java.io.File out = new java.io.File(dir, "mutant_" + neighbor.getId() + ".c");
                    try {
                        neighbor.write(out);
                    } catch (IOException e) {
                        LOGGER.logException(e);
                    }
                }
                
                Double fitness = evaluate(neighbor, true, true);
                if (fitness != null) {
                    if (fitness > mutantList.getTopFitness()) {
                        LOGGER.println(" -> " + neighbor.getId() + " is better than "
                                + mutantList.getTopMutant().getId());
                        improved = true;
                    }
                    
                    mutantList.insertMutant(neighbor, fitness);
                }
            }
            
            setBestInIteration(iteration, mutantList.getTopMutant());
            
        } while (improved);
    }
    
    private void simulatedAnnealing(File originalAst, TopXMutants mutantList) {
        double initTemp = config.getInitialTemperature();
        int maxIter = config.getMaxAnnealingIterations();
        
        Mutant currentMutant = mutantList.getTopMutant();
        double currentFitness = mutantList.getTopFitness();
        
        for (iteration = 1; iteration <= maxIter; iteration++) {
            LOGGER.println();
            LOGGER.printf("Iteration %03d\n", iteration);
            LOGGER.println("-------------");
            
            double temperature = initTemp * (maxIter - iteration + 1) / maxIter;
            LOGGER.println("Temperature: " + temperature);
            
            List<Mutant> neighbors = generateNeighbors(currentMutant);
            LOGGER.println("Generated " + neighbors.size() + " neighbors");
            
            Mutant neighbor;
            Double nFitness;
            do {
                neighbor = neighbors.get((int) (Math.random() * neighbors.size()));
                neighbor.apply(originalAst);
                
                if (config.getSaveIterations()) {
                    java.io.File dir = new java.io.File(config.getExecDir(),
                            String.format(Locale.ROOT, "iteration_%03d", iteration));
                    dir.mkdir();
                    java.io.File out = new java.io.File(dir, "mutant_" + neighbor.getId() + ".c");
                    try {
                        neighbor.write(out);
                    } catch (IOException e) {
                        LOGGER.logException(e);
                    }
                }
                
                nFitness = evaluate(neighbor, true, true);
            } while (nFitness == null); 
            
            double delta = currentFitness - nFitness;
            // TODO: normalise delta somehow?
            
            if (delta < 0) {
                LOGGER.println(" -> " + neighbor.getId() + " is better than " + currentMutant.getId());
                mutantList.insertMutant(neighbor, nFitness);
                currentMutant = neighbor;
                currentFitness = nFitness;
            } else if (Math.random() < Math.pow(Math.E, -delta / temperature)) {
                LOGGER.println(" -> " + neighbor.getId() + " selected because of temperature");
                mutantList.insertMutant(neighbor, nFitness);
                currentMutant = neighbor;
                currentFitness = nFitness;
            } else {
                LOGGER.println(" -> " + neighbor.getId() + " not selected");
            }
            
            setBestInIteration(iteration, currentFitness);
        }
        
        LOGGER.println();
        LOGGER.println("Temperature exceeded, falling back to hill climbing");
        iteration--; // hillClimbing starts by increase iteration
        hillClimbing(originalAst, mutantList);
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
