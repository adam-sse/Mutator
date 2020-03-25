package net.ssehub.mutator.visualization;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.util.Logger;

public class BestFitnessRenderer extends AbstractDotRenderer {

    private static final Logger LOGGER = Logger.get("BestFitnessRenderer");
    
    protected double xMin;
    protected double xMax;
    
    protected double yMin;
    protected double yMax;
    
    protected double xStep;
    protected double yStep;
    
    protected int xPrecision;
    protected int yPrecision;
    
    public BestFitnessRenderer(String dotExe) {
        super(dotExe);
    }
    
    protected boolean checkDimension(List<Fitness> bestFitnesses) {
        for (Fitness fitness : bestFitnesses) {
            if (fitness.numValues() != 2) {
                return false;
            }
        }
        return true;
    }
    
    protected void calcAxisScale(List<Fitness> bestFitnesses) {
        xMin = Double.MAX_VALUE;
        xMax = Double.MIN_VALUE;
        yMin = Double.MAX_VALUE;
        yMax = Double.MIN_VALUE;
        
        for (Fitness fitness : bestFitnesses) {
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
        
        xMax = ceilToMagnitude(xMax, magnitude(xMax - xMin));
        yMax = ceilToMagnitude(yMax, magnitude(yMax - yMin));
        xMin = floorToMagnitude(xMin, magnitude(xMax - xMin));
        yMin = floorToMagnitude(yMin, magnitude(yMax - yMin));
        
        if (xMax == xMin) {
            xMax += 0.5;
            xMin -= 0.5;
        }
        if (yMax == yMin) {
            yMax += 0.5;
            yMin -= 0.5;
        }
        
        xStep = (xMax - xMin) / 10.0;
        yStep = (yMax - yMin) / 10.0;
        
        xPrecision = Math.max(magnitude(xStep) * -1, 0);
        yPrecision = Math.max(magnitude(yStep) * -1, 0);
    }
    
    protected String getGraphAttributes() {
        return "";
    }
    
    protected String getNodeShape() {
        return "circle";
    }
    
    protected String getArrowHead() {
        return "vee";
    }
    
    private String getPos(double x, double y) {
        return String.format(Locale.ROOT, "\"%f,%f!\"", x, y);
    }
    
    protected String getPos(Fitness fitness) {
        double x = (fitness.getValue(0) - xMin) / xStep + 1.0;
        double y = (fitness.getValue(1) - yMin) / yStep + 1.0;
        
        return getPos(x, y);
    }
    
    protected String getTooltipp(Fitness fitness) {
        return String.format(Locale.ROOT, "\"%." + (xPrecision + 1) + "f, %." + (yPrecision + 1) + "f\"",
                fitness.getValue(0), fitness.getValue(1));
    }
    
    protected void createAxes(StringBuilder dot) {
        dot
            .append("        \"origin\" [label=\"\", pos=" + getPos(0, 0) + ", width=0, height=0];\n")
            .append("        \"xHead\" [label=\"\", pos=" + getPos(11.2, 0) + ", width=0, height=0];\n")
            .append("        \"yHead\" [label=\"\", pos=" + getPos(0, 11.2) + ", width=0, height=0];\n")
            .append("        \"origin\" -> \"xHead\";\n")
            .append("        \"origin\" -> \"yHead\";\n")
            .append("\n");
        
        for (int i = 1; i <= 11; i++) {
            double x = xMin + ((i - 1) * xStep);
            dot
                .append("        \"lx")
                .append(i)
                .append("\" [label=\"")
                .append(String.format(Locale.ROOT, "%." + xPrecision + "f", x))
                .append("\", tooltip=\"")
                .append(String.format(Locale.ROOT, "%." + (xPrecision + 1) + "f", x))
                .append("\", pos=")
                .append(getPos(i, -0.2))
                .append("];\n");
        }
        dot.append("\n");
        for (int i = 1; i <= 11; i++) {
            double y = yMin + ((i - 1) * yStep);
            dot
                .append("        \"ly")
                .append(i)
                .append("\" [label=\"")
                .append(String.format(Locale.ROOT, "%." + yPrecision + "f", y))
                .append("\", tooltip=\"")
                .append(String.format(Locale.ROOT, "%." + (yPrecision + 1) + "f", y))
                .append("\", pos=")
                .append(getPos(-0.2, i))
                .append("];\n");
        }
    }
    
    protected boolean checkDistance(Fitness previous, Fitness current, double minDist) {
        double x1 = (previous.getValue(0) - xMin) / xStep + 1.0;
        double y1 = (previous.getValue(1) - yMin) / yStep + 1.0;
        
        double x2 = (current.getValue(0) - xMin) / xStep + 1.0;
        double y2 = (current.getValue(1) - yMin) / yStep + 1.0;
        
        return dist(x1, y1, x2, y2) > minDist;
    }
    
    public void render(List<Fitness> bestFitnesses, File output) throws IOException {
        if (!checkDimension(bestFitnesses)) {
            LOGGER.println("Can only log two-dimensional fitnesses");
            return;
        }
        
        calcAxisScale(bestFitnesses);
        
        StringBuilder dot = new StringBuilder();
        
        // preamble
        dot
            .append("digraph fitness {\n")
            .append("    " + getGraphAttributes() + "\n")
            .append("    node [shape=" + getNodeShape() + ", margin=\"0.1\", width=0.4, height=0.4, fixedsize=true];\n")
            .append("    edge [arrowhead=" + getArrowHead() + ", arrowsize=0.8];\n")
            .append("\n");
        
        // axes
        dot
            .append("    subgraph axes {\n")
            .append("        node [shape=none, width=1.0, height=1.0, fixedsize=true];\n");
        
        createAxes(dot);
        
        dot.append("    }\n").append("\n");
        
        // iteration nodes and edges
        Fitness previousFitness = null;
        int previous = -1;
        
        int iteration = 0;
        for (Fitness fitness : bestFitnesses) {
            iteration++;
            
            if (previous == -1 || checkDistance(previousFitness, fitness, 0.4)) {
                // node
                dot
                    .append("    \"")
                    .append(String.format(Locale.ROOT, "%03d", iteration))
                    .append("\" [pos=")
                    .append(getPos(fitness))
                    .append(", tooltip=")
                    .append(getTooltipp(fitness))
                    .append("];\n");
                
                if (previous != -1 && checkDistance(previousFitness, fitness, 0.45)) {
                    // edge
                    dot
                        .append("    \"")
                        .append(String.format(Locale.ROOT, "%03d", previous))
                        .append("\" -> \"")
                        .append(String.format(Locale.ROOT, "%03d", iteration))
                        .append("\";\n");
                }
                
                previous = iteration;
                previousFitness = fitness;
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
    
    protected static int magnitude(double d) {
        d = Math.abs(d);
        int magnitude = 0;
        if (d < 1.0) {
            while (d < 1.0) {
                d *= 10;
                magnitude--;
            }
        } else {
            while (d >= 10) {
                d /= 10;
                magnitude++;
            }
        }

        return magnitude;
    }
    
    protected static double ceilToMagnitude(double d, int magnitude) {
        double factor = Math.pow(10, magnitude);
        return Math.ceil(d / factor) * factor;
    }
    
    protected static double floorToMagnitude(double d, int magnitude) {
        double factor = Math.pow(10, magnitude);
        return Math.floor(d / factor) * factor;
    }
    
}
