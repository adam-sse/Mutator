package net.ssehub.mutator.visualization;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.util.Logger;

public class FitnessRenderer extends AbstractDotRenderer {

    private static final Logger LOGGER = Logger.get("FitnessRenderer");
    
    protected double xMin;
    protected double xMax;
    
    protected double yMin;
    protected double yMax;
    
    protected double xStep;
    protected double yStep;
    
    protected int xPrecision;
    protected int yPrecision;
    
    private StringBuilder dot;
    
    private boolean connectWithEdges;
    
    private boolean preventOverlap;
    
    private int nodeIndex;
    
    private int previous;
    
    private Fitness previousFitness;
    
    public FitnessRenderer(String dotExe, boolean connectWithEdges, boolean preventOverlap) {
        super(dotExe);
        this.connectWithEdges = connectWithEdges;
        this.preventOverlap = preventOverlap;
    }
    
    protected boolean checkDimension(Collection<Fitness> bestFitnesses) {
        for (Fitness fitness : bestFitnesses) {
            if (fitness.numValues() != 2) {
                return false;
            }
        }
        return true;
    }
    
    protected void calcAxisScale(Collection<Fitness> bestFitnesses) {
        xMin = Double.MAX_VALUE;
        xMax = -Double.MAX_VALUE;
        yMin = Double.MAX_VALUE;
        yMax = -Double.MAX_VALUE;
        
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
    
    protected String getArrowAttributes() {
        return "arrowhead=vee, arrowsize=0.8";
    }
    
    private String getPos(double x, double y) {
        return String.format(Locale.ROOT, "\"%f,%f!\"", x, y);
    }
    
    protected String getPos(Fitness fitness) {
        double x = (fitness.getValue(0) - xMin) / xStep;
        double y = (fitness.getValue(1) - yMin) / yStep;
        
        return getPos(x, y);
    }
    
    protected String getPosTooltipp(Fitness fitness) {
        return String.format(Locale.ROOT, "%." + (xPrecision + 1) + "f, %." + (yPrecision + 1) + "f",
                fitness.getValue(0), fitness.getValue(1));
    }
    
    protected void createAxes(StringBuilder dot) {
        double xZero = -xMin / xStep;
        double yZero = -yMin / yStep;
        
        if (xZero < 0) {
            xZero = 0;
        } else if (xZero > 10) {
            xZero = 10;
        }
        if (yZero < 0) {
            yZero = 0;
        } else if (yZero > 10) {
            yZero = 10;
        }
        
        dot
            .append("        \"xOrigin\" [label=\"\", pos=" + getPos(0.0, yZero) + ", width=0, height=0];\n")
            .append("        \"xHead\" [label=\"\", pos=" + getPos(10.2, yZero) + ", width=0, height=0];\n")
            .append("        \"yOrigin\" [label=\"\", pos=" + getPos(xZero, 0.0) + ", width=0, height=0];\n")
            .append("        \"yHead\" [label=\"\", pos=" + getPos(xZero, 10.2) + ", width=0, height=0];\n")
            .append("        \"xOrigin\" -> \"xHead\";\n")
            .append("        \"yOrigin\" -> \"yHead\";\n")
            .append("\n");
        
        for (int i = 0; i <= 10; i++) {
            double x = xMin + (i * xStep);
            dot
                .append("        \"lx")
                .append(i)
                .append("\" [label=\"")
                .append(String.format(Locale.ROOT, "%." + xPrecision + "f", x))
                .append("\", tooltip=\"")
                .append(String.format(Locale.ROOT, "%." + (xPrecision + 1) + "f", x))
                .append("\", pos=")
                .append(getPos(i, yZero - 0.2))
                .append("];\n");
        }
        dot.append("\n");
        for (int i = 0; i <= 10; i++) {
            double y = yMin + (i * yStep);
            dot
                .append("        \"ly")
                .append(i)
                .append("\" [label=\"")
                .append(String.format(Locale.ROOT, "%." + yPrecision + "f", y))
                .append("\", tooltip=\"")
                .append(String.format(Locale.ROOT, "%." + (yPrecision + 1) + "f", y))
                .append("\", pos=")
                .append(getPos(xZero - 0.2, i))
                .append("];\n");
        }
    }
    
    protected boolean checkDistance(Fitness previous, Fitness current, double minDist) {
        double x1 = (previous.getValue(0) - xMin) / xStep;
        double y1 = (previous.getValue(1) - yMin) / yStep;
        
        double x2 = (current.getValue(0) - xMin) / xStep;
        double y2 = (current.getValue(1) - yMin) / yStep;
        
        return dist(x1, y1, x2, y2) > minDist;
    }
    
    protected String getSpecialNodeAttributes(boolean first, boolean last) {
        if (first) {
            return "style=filled, fillcolor=\"#fdc086\"";
        } else if (last) {
            return "style=filled, fillcolor=\"#7fc97f\"";
        } else {
            return null;
        }
    }
    
    public boolean init(Collection<Fitness> allFitnesses) {
        if (!checkDimension(allFitnesses)) {
            LOGGER.println("Can only log two-dimensional fitnesses");
            return false;
        }
        
        calcAxisScale(allFitnesses);
        
        dot = new StringBuilder();
        
        // preamble
        dot
            .append("digraph fitness {\n")
            .append("    " + getGraphAttributes() + "\n")
            .append("    node [shape=" + getNodeShape() + ", margin=\"0.1\", width=0.4, height=0.4, fixedsize=true];\n")
            .append("    edge [" + getArrowAttributes() + "];\n")
            .append("\n");
        
        // axes
        dot
            .append("    subgraph axes {\n")
            .append("        node [shape=none, width=1.0, height=1.0, fixedsize=true];\n");
        
        createAxes(dot);
        
        dot.append("    }\n").append("\n");
        
        this.nodeIndex = 0;
        this.previousFitness = null;
        this.previous = -1;
        
        return true;
    }
    
    public void addNode(Fitness fitness, String label, boolean last) {
        boolean first = nodeIndex == 0;
        
        if (!preventOverlap || first || last || checkDistance(previousFitness, fitness, 0.4)) {
            // node
            dot
                .append("    \"")
                .append(String.format(Locale.ROOT, "%03d", nodeIndex))
                .append("\" [label=\"")
                .append(label)
                .append("\", pos=")
                .append(getPos(fitness))
                .append(", tooltip=\"");
            if (!label.isBlank()) {
                dot.append(label)
                    .append(": ");
            }
            dot
                .append(getPosTooltipp(fitness))
                .append("\"");
            
            if (label.length() > 3) {
                dot.append("fontsize=8");
            }
            
            String specialAttr = getSpecialNodeAttributes(first, last);
            if (specialAttr != null) {
                dot.append(", ").append(specialAttr);
            }
            
            dot.append("];\n");
            
            if (connectWithEdges && !first && (!preventOverlap || checkDistance(previousFitness, fitness, 0.45))) {
                // edge
                dot
                    .append("    \"")
                    .append(String.format(Locale.ROOT, "%03d", previous))
                    .append("\" -> \"")
                    .append(String.format(Locale.ROOT, "%03d", nodeIndex))
                    .append("\";\n");
            }
            
            previous = nodeIndex;
            previousFitness = fitness;
            
            nodeIndex++;
        }
    }
    
    public void render(File output) throws IOException {
        dot.append("}\n");
        
        render(dot.toString(), "neato", output);
    }
    
    private static double dist(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt((dx * dx) + (dy * dy));
    }
    
    protected static int magnitude(double d) {
        if (d == 0.0) {
            return 0;
        }
        
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
