package net.ssehub.mutator.visualization;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import net.ssehub.mutator.mutation.fitness.Fitness;
import net.ssehub.mutator.util.Logger;

public class BestFitnessRenderer3D extends AbstractDotRenderer {

    private static final Logger LOGGER = Logger.get("BestFitnessRenderer3D");
    
    public BestFitnessRenderer3D(String dotExe) {
        super(dotExe);
    }
    
    public void render(List<Fitness> bestFitnesses, File output) throws IOException {
        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;
        double zMin = Double.MAX_VALUE;
        double zMax = Double.MIN_VALUE;
        
        for (Fitness fitness : bestFitnesses) {
            if (fitness.numValues() != 3) {
                LOGGER.println("Can only log three-dimensional fitnesses");
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
            if (fitness.getValue(2) < zMin) {
                zMin = fitness.getValue(2);
            }
            if (fitness.getValue(2) > zMax) {
                zMax = fitness.getValue(2);
            }
        }
        
        xMax = ceilToMagnitude(xMax, magnitude(xMax - xMin));
        yMax = ceilToMagnitude(yMax, magnitude(yMax - yMin));
        zMax = ceilToMagnitude(zMax, magnitude(zMax - zMin));
        xMin = floorToMagnitude(xMin, magnitude(xMax - xMin));
        yMin = floorToMagnitude(yMin, magnitude(yMax - yMin));
        zMin = floorToMagnitude(zMin, magnitude(zMax - zMin));
        
        if (xMax == xMin) {
            xMax += 0.5;
            xMin -= 0.5;
        }
        if (yMax == yMin) {
            yMax += 0.5;
            yMin -= 0.5;
        }
        if (zMax == zMin) {
            zMax += 0.5;
            zMin -= 0.5;
        }
        
        double xStep = (xMax - xMin) / 10.0;
        double yStep = (yMax - yMin) / 10.0;
        double zStep = (zMax - zMin) / 10.0;
        
        int xPrecision = Math.max(magnitude(xStep) * -1, 0);
        int yPrecision = Math.max(magnitude(yStep) * -1, 0);
        int zPrecision = Math.max(magnitude(zStep) * -1, 0);
        
        StringBuilder dot = new StringBuilder();
        
        // preamble
        dot
            .append("digraph fitness {\n")
            .append("    dimen=3;")
            .append("    node [shape=point, margin=\"0.1\", width=0.4, height=0.4, fixedsize=true];\n")
            .append("\n");
        
        // axes
        dot
            .append("    subgraph axes {\n")
            .append("        node [shape=none, width=1.0, height=1.0, fixedsize=true];\n")
            .append("\n")
            .append("        \"origin\" [label=\"\", pos=\"0,0,0!\", width=0, height=0];\n")
            .append("        \"xHead\" [label=\"\", pos=\"11.2,0,0!\", width=0, height=0];\n")
            .append("        \"yHead\" [label=\"\", pos=\"0,11.2,0!\", width=0, height=0];\n")
            .append("        \"zHead\" [label=\"\", pos=\"0,0,11.2!\", width=0, height=0];\n")
            .append("        \"origin\" -> \"xHead\";\n")
            .append("        \"origin\" -> \"yHead\";\n")
            .append("        \"origin\" -> \"zHead\";\n")
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
                .append("\", pos=\"")
                .append(i)
                .append(",-0.2,0!\"];\n");
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
                .append("\", pos=\"-0.2,")
                .append(i)
                .append(",0!\"];\n");
        }
        dot.append("\n");
        for (int i = 1; i <= 11; i++) {
            double z = zMin + ((i - 1) * zStep);
            dot
            .append("        \"lz")
            .append(i)
            .append("\" [label=\"")
            .append(String.format(Locale.ROOT, "%." + zPrecision + "f", z))
            .append("\", tooltip=\"")
            .append(String.format(Locale.ROOT, "%." + (zPrecision + 1) + "f", z))
            .append("\", pos=\"-0.2,")
            .append(i)
            .append(",0!\"];\n");
        }
        dot.append("    }\n").append("\n");
        
        // iteration nodes and edges
        double previousX = 0.0;
        double previousY = 0.0;
        double previousZ = 0.0;
        int previous = -1;
        
        int iteration = 0;
        for (Fitness fitness : bestFitnesses) {
            iteration++;
            
            double x = (fitness.getValue(0) - xMin) / xStep + 1.0;
            double y = (fitness.getValue(1) - yMin) / yStep + 1.0;
            double z = (fitness.getValue(2) - zMin) / zStep + 1.0;
            
            if (previous == -1 || dist(x, y, z, previousX, previousY, previousZ) > 0.4) {
                // node
                dot
                    .append("    \"")
                    .append(String.format(Locale.ROOT, "%03d", iteration))
                    .append("\" [pos=\"")
                    .append(x)
                    .append(",")
                    .append(y)
                    .append(",")
                    .append(z)
                    .append("!\", tooltip=\"")
                    .append(String.format(Locale.ROOT, "%." + (xPrecision + 1) + "f, %." + (yPrecision + 1) +"f"
                            + ", %." + (zPrecision + 1) +"f",
                            fitness.getValue(0), fitness.getValue(1), fitness.getValue(2)))
                    .append("\"];\n");
                
                if (previous != -1 && dist(x, y, z, previousX, previousY, previousZ) > 0.45) {
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
    
    private static double dist(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }
    
    private static int magnitude(double d) {
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
    
    private static double ceilToMagnitude(double d, int magnitude) {
        double factor = Math.pow(10, magnitude);
        return Math.ceil(d / factor) * factor;
    }
    
    private static double floorToMagnitude(double d, int magnitude) {
        double factor = Math.pow(10, magnitude);
        return Math.floor(d / factor) * factor;
    }
    
}
