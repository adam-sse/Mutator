package net.ssehub.mutator.visualization;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.util.Logger;

public class BestFitnessRenderer extends AbstractDotRenderer {

    private static final Logger LOGGER = Logger.get("BestFitnessRenderer");
    
    public BestFitnessRenderer(String dotExe) {
        super(dotExe);
    }
    
    public void render(List<Fitness> bestFitnesses, File output) throws IOException {
        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;
        
        for (Fitness fitness : bestFitnesses) {
            if (fitness.numValues() != 2) {
                LOGGER.println("Can only log two-dimensional fitnesses");
                return;
            }
            
            if (fitness.getValue(0) < xMin) {
                xMin = fitness.getValue(0);
            }
            if (fitness.getValue(0) > xMax) {
                xMax = fitness.getValue(0);
            }
            if (fitness.getValue(1) < yMin) {
                yMin = fitness.getValue(1);
            }
            if (fitness.getValue(1) > yMax) {
                yMax = fitness.getValue(1);
            }
        }
        
        double xStep = (xMax - xMin) / 10.0;
        double yStep = (yMax - yMin) / 10.0;
        
        StringBuilder dot = new StringBuilder();
        
        // preamble
        dot
            .append("digraph fitness {\n")
            .append("    node [shape=circle, margin=\"0.1\", width=0.4, height=0.4, fixedsize=true];\n")
            .append("    edge [arrowhead=\"vee\", arrowsize=0.8];\n")
            .append("\n");
        
        // axes
        dot
            .append("    subgraph axes {\n")
            .append("        node [shape=none, width=0.4, height=0.4, fixedsize=true];\n")
            .append("\n")
            .append("        \"origin\" [label=\"\", pos=\"0,0!\", width=0, height=0];\n")
            .append("        \"xHead\" [label=\"\", pos=\"11.2,0!\", width=0, height=0];\n")
            .append("        \"yHead\" [label=\"\", pos=\"0,11.2!\", width=0, height=0];\n")
            .append("        \"origin\" -> \"xHead\";\n")
            .append("        \"origin\" -> \"yHead\";\n")
            .append("\n");
        
        for (int i = 1; i <= 11; i++) {
            dot
                .append("        \"lx")
                .append(i)
                .append("\" [label=\"")
                .append(String.format(Locale.ROOT, "%.2f", xMin + ((i - 1) * xStep)))
                .append("\", pos=\"")
                .append(i)
                .append(",-0.2!\"];\n");
        }
        dot.append("\n");
        for (int i = 1; i <= 11; i++) {
            dot
            .append("        \"ly")
            .append(i)
            .append("\" [label=\"")
            .append(String.format(Locale.ROOT, "%.2f", yMin + ((i - 1) * yStep)))
            .append("\", pos=\"-0.2,")
            .append(i)
            .append("!\"];\n");
        }
        dot.append("    }\n").append("\n");
        
        // iteration nodes and edges
        double previousX = 0.0;
        double previousY = 0.0;
        int previous = -1;
        
        int iteration = 0;
        for (Fitness fitness : bestFitnesses) {
            iteration++;
            
            double x = (fitness.getValue(0) - xMin) / xStep + 1.0;
            double y = (fitness.getValue(1) - yMin) / yStep + 1.0;
            
            if (previous == -1 || dist(x, y, previousX, previousY) > 0.4) {
                // node
                dot
                    .append("    \"")
                    .append(String.format(Locale.ROOT, "%03d", iteration))
                    .append("\" [pos=\"")
                    .append(x)
                    .append(",")
                    .append(y)
                    .append("!\"];\n");
                
                if (previous != -1 && dist(x, y, previousX, previousY) > 0.45) {
                    // edge
                    dot
                        .append("    \"")
                        .append(String.format(Locale.ROOT, "%03d", previous))
                        .append("\" -> \"")
                        .append(String.format(Locale.ROOT, "%03d", iteration))
                        .append("\";\n");
                }
                
                previous = iteration;
                previousX = x;
                previousY = y;
            }
            
        }
        
        dot.append("}\n");
        
        render(dot.toString(), "neato", output);
    }
    
    private static double dist(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt((dx * dx) + (dy * dy));
    }
    
}
