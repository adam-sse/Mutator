package net.ssehub.mutator.visualization;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import net.ssehub.mutator.mutation.fitness.Fitness;

public class BestFitnessRenderer {

    private String dotExe;
    
    private boolean threeD;
    
    public BestFitnessRenderer(String dotExe, boolean threeD) {
        this.dotExe = dotExe;
        this.threeD = threeD;
    }
    
    public void render(List<Fitness> bestFitnesses, File output) throws IOException {
        FitnessRenderer renderer;
        if (this.threeD) {
            renderer = new FitnessRenderer3D(dotExe, true, true);
        } else {
            renderer = new FitnessRenderer(dotExe, true, true);
        }
        
        if (!renderer.init(bestFitnesses)) {
            return;
        }
        
        for (int i = 0; i < bestFitnesses.size(); i++) {
            renderer.addNode(bestFitnesses.get(i), String.format(Locale.ROOT, "%03d", i + 1),
                    i == 0, i == bestFitnesses.size() - 1, (double) i / bestFitnesses.size());
        }
        
        renderer.render(output);
    }
    
}
