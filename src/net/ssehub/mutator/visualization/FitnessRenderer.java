package net.ssehub.mutator.visualization;

import java.awt.Color;
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
            if (fitness.numValues() != 2)
                return false;
        }
        return true;
    }

    protected void calcAxisScale(Collection<Fitness> bestFitnesses) {
        this.xMin = Double.MAX_VALUE;
        this.xMax = -Double.MAX_VALUE;
        this.yMin = Double.MAX_VALUE;
        this.yMax = -Double.MAX_VALUE;

        for (Fitness fitness : bestFitnesses) {
            if (fitness.getValue(0) < this.xMin) {
                this.xMin = fitness.getValue(0);
            }
            if (fitness.getValue(0) > this.xMax) {
                this.xMax = fitness.getValue(0);
            }
            if (fitness.getValue(1) < this.yMin) {
                this.yMin = fitness.getValue(1);
            }
            if (fitness.getValue(1) > this.yMax) {
                this.yMax = fitness.getValue(1);
            }
        }

        this.xMax = ceilToMagnitude(this.xMax, magnitude(this.xMax - this.xMin));
        this.yMax = ceilToMagnitude(this.yMax, magnitude(this.yMax - this.yMin));
        this.xMin = floorToMagnitude(this.xMin, magnitude(this.xMax - this.xMin));
        this.yMin = floorToMagnitude(this.yMin, magnitude(this.yMax - this.yMin));

        if (this.xMax == this.xMin) {
            this.xMax += 0.5;
            this.xMin -= 0.5;
        }
        if (this.yMax == this.yMin) {
            this.yMax += 0.5;
            this.yMin -= 0.5;
        }

        this.xStep = (this.xMax - this.xMin) / 10.0;
        this.yStep = (this.yMax - this.yMin) / 10.0;

        this.xPrecision = Math.max(magnitude(this.xStep) * -1, 0);
        this.yPrecision = Math.max(magnitude(this.yStep) * -1, 0);
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
        double x = (fitness.getValue(0) - this.xMin) / this.xStep;
        double y = (fitness.getValue(1) - this.yMin) / this.yStep;

        return getPos(x, y);
    }

    protected String getPosTooltipp(Fitness fitness) {
        return String.format(Locale.ROOT, "%." + (this.xPrecision + 1) + "f, %." + (this.yPrecision + 1) + "f",
                fitness.getValue(0), fitness.getValue(1));
    }

    protected void createAxes(StringBuilder dot) {
        double xZero = -this.xMin / this.xStep;
        double yZero = -this.yMin / this.yStep;

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

        dot.append("        \"xOrigin\" [label=\"\", pos=" + getPos(0.0, yZero) + ", width=0, height=0];\n")
                .append("        \"xHead\" [label=\"\", pos=" + getPos(10.2, yZero) + ", width=0, height=0];\n")
                .append("        \"yOrigin\" [label=\"\", pos=" + getPos(xZero, 0.0) + ", width=0, height=0];\n")
                .append("        \"yHead\" [label=\"\", pos=" + getPos(xZero, 10.2) + ", width=0, height=0];\n")
                .append("        \"xOrigin\" -> \"xHead\";\n").append("        \"yOrigin\" -> \"yHead\";\n")
                .append("\n");

        for (int i = 0; i <= 10; i++) {
            double x = this.xMin + (i * this.xStep);
            dot.append("        \"lx").append(i).append("\" [label=\"")
                    .append(String.format(Locale.ROOT, "%." + this.xPrecision + "f", x)).append("\", tooltip=\"")
                    .append(String.format(Locale.ROOT, "%." + (this.xPrecision + 1) + "f", x)).append("\", pos=")
                    .append(getPos(i, yZero - 0.2)).append("];\n");
        }
        dot.append("\n");
        for (int i = 0; i <= 10; i++) {
            double y = this.yMin + (i * this.yStep);
            dot.append("        \"ly").append(i).append("\" [label=\"")
                    .append(String.format(Locale.ROOT, "%." + this.yPrecision + "f", y)).append("\", tooltip=\"")
                    .append(String.format(Locale.ROOT, "%." + (this.yPrecision + 1) + "f", y)).append("\", pos=")
                    .append(getPos(xZero - 0.2, i)).append("];\n");
        }
    }

    protected boolean checkDistance(Fitness previous, Fitness current, double minDist) {
        double x1 = (previous.getValue(0) - this.xMin) / this.xStep;
        double y1 = (previous.getValue(1) - this.yMin) / this.yStep;

        double x2 = (current.getValue(0) - this.xMin) / this.xStep;
        double y2 = (current.getValue(1) - this.yMin) / this.yStep;

        return dist(x1, y1, x2, y2) > minDist;
    }

    protected String getSpecialNodeAttributes(boolean first, boolean last, double colorShade) {
        if (first)
            return "style=filled, fillcolor=\"#fdc086\"";
        else if (last)
            return "style=filled, fillcolor=\"#7fc97f\"";
        else {
            Color color = Color.getHSBColor(0.737f, 0.179f, (float) colorShade * 0.5f + 0.5f);
            return String.format(Locale.ROOT, "style=filled, fillcolor=\"#%02x%02x%02x%02x\"", color.getRed(),
                    color.getGreen(), color.getBlue(), 127);
        }
    }

    public boolean init(Collection<Fitness> allFitnesses) {
        if (!checkDimension(allFitnesses)) {
            FitnessRenderer.LOGGER.println("Can only log two-dimensional fitnesses");
            return false;
        }

        calcAxisScale(allFitnesses);

        this.dot = new StringBuilder();

        // preamble
        this.dot.append("digraph \"Fitness Graph\" {\n").append("    " + getGraphAttributes() + "\n")
                .append("    node [shape=" + getNodeShape()
                        + ", margin=\"0.1\", width=0.4, height=0.4, fixedsize=true];\n")
                .append("    edge [" + getArrowAttributes() + "];\n").append("\n");

        // axes
        this.dot.append("    subgraph axes {\n")
                .append("        node [shape=none, width=1.0, height=1.0, fixedsize=true];\n");

        createAxes(this.dot);

        this.dot.append("    }\n").append("\n");

        this.nodeIndex = 0;
        this.previousFitness = null;
        this.previous = -1;

        return true;
    }

    public void addNode(Fitness fitness, String label, boolean first, boolean last, double colorShade) {
        if (!this.preventOverlap || first || last || checkDistance(this.previousFitness, fitness, 0.4)) {
            // node
            this.dot.append("    \"").append(String.format(Locale.ROOT, "%03d", this.nodeIndex)).append("\" [label=\"")
                    .append(label).append("\", pos=").append(getPos(fitness)).append(", tooltip=\"");
            if (!label.isBlank()) {
                this.dot.append(label).append(": ");
            }
            this.dot.append(getPosTooltipp(fitness)).append("\"");

            if (label.length() > 3) {
                this.dot.append("fontsize=8");
            }

            String specialAttr = getSpecialNodeAttributes(first, last, colorShade);
            if (specialAttr != null) {
                this.dot.append(", ").append(specialAttr);
            }

            this.dot.append("];\n");

            if (this.connectWithEdges && !first
                    && (!this.preventOverlap || checkDistance(this.previousFitness, fitness, 0.45))) {
                // edge
                this.dot.append("    \"").append(String.format(Locale.ROOT, "%03d", this.previous)).append("\" -> \"")
                        .append(String.format(Locale.ROOT, "%03d", this.nodeIndex)).append("\";\n");
            }

            this.previous = this.nodeIndex;
            this.previousFitness = fitness;

            this.nodeIndex++;
        }
    }

    public void render(File output) throws IOException {
        this.dot.append("}\n");

        render(this.dot.toString(), "neato", output);
    }

    private static double dist(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    protected static int magnitude(double d) {
        if (d == 0.0)
            return 0;

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
