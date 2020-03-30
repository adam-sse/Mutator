package net.ssehub.mutator.visualization;

import java.util.Collection;
import java.util.Locale;

import net.ssehub.mutator.mutation.fitness.Fitness;

public class FitnessRenderer3D extends FitnessRenderer {

    private double zMin;
    private double zMax;
    
    private double zStep;
    
    private int zPrecision;
    
    public FitnessRenderer3D(String dotExe, boolean connectWithEdges, boolean preventOverlap) {
        super(dotExe, connectWithEdges, preventOverlap);
    }
    
    @Override
    protected boolean checkDimension(Collection<Fitness> bestFitnesses) {
        for (Fitness fitness : bestFitnesses) {
            if (fitness.numValues() != 3) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected void calcAxisScale(Collection<Fitness> bestFitnesses) {
        super.calcAxisScale(bestFitnesses);
        
        zMin = Double.MAX_VALUE;
        zMax = -Double.MAX_VALUE;
        
        for (Fitness fitness : bestFitnesses) {
            if (fitness.getValue(2) < zMin) {
                zMin = fitness.getValue(2);
            }
            if (fitness.getValue(2) > zMax) {
                zMax = fitness.getValue(2);
            }
        }
        
        zMax = ceilToMagnitude(zMax, magnitude(zMax - zMin));
        zMin = floorToMagnitude(zMin, magnitude(zMax - zMin));
        
        if (zMax == zMin) {
            zMax += 0.5;
            zMin -= 0.5;
        }
        zStep = (zMax - zMin) / 10.0;
        
        zPrecision = Math.max(magnitude(zStep) * -1, 0);
    }
    
    @Override
    protected String getGraphAttributes() {
        return "dimen=3;";
    }
    
    @Override
    protected String getNodeShape() {
        return "point";
    }
    
    @Override
    protected String getArrowAttributes() {
        return "arrowhead=normal, arrowsize=2";
    }
    
    private String getPos(double x, double y, double z) {
        return String.format(Locale.ROOT, "\"%f,%f,%f!\"", x, y, z);
    }
    
    @Override
    protected String getPos(Fitness fitness) {
        double x = (fitness.getValue(0) - xMin) / xStep;
        double y = (fitness.getValue(1) - yMin) / yStep;
        double z = (fitness.getValue(2) - zMin) / zStep;
        
        return getPos(x, y, z);
    }

    @Override
    protected String getPosTooltipp(Fitness fitness) {
        return String.format(Locale.ROOT, "%." + (xPrecision + 1) + "f, %." + (yPrecision + 1) + "f"
                + ", %." + (zPrecision + 1) + "f",
                fitness.getValue(0), fitness.getValue(1), fitness.getValue(2));
    }
    
    @Override
    protected void createAxes(StringBuilder dot) {
        double xZero = -xMin / xStep;
        double yZero = -yMin / yStep;
        double zZero = -zMin / zStep;
        
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
        if (zZero < 0) {
            zZero = 0;
        } else if (zZero > 10) {
            zZero = 10;
        }
        
        dot
            .append("        \"xOrigin\" [label=\"\", pos=" + getPos(0, yZero, zZero) + ", width=0, height=0];\n")
            .append("        \"xHead\" [label=\"\", pos=" + getPos(9.9, yZero, zZero) + ", width=0, height=0];\n")
            .append("        \"yOrigin\" [label=\"\", pos=" + getPos(xZero, 0, zZero) + ", width=0, height=0];\n")
            .append("        \"yHead\" [label=\"\", pos=" + getPos(xZero, 9.9, zZero) + ", width=0, height=0];\n")
            .append("        \"zOrigin\" [label=\"\", pos=" + getPos(xZero, yZero, 0) + ", width=0, height=0];\n")
            .append("        \"zHead\" [label=\"\", pos=" + getPos(xZero, yZero, 9.9) + ", width=0, height=0];\n")
            .append("        \"xOrigin\" -> \"xHead\";\n")
            .append("        \"yOrigin\" -> \"yHead\";\n")
            .append("        \"zOrigin\" -> \"zHead\";\n")
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
                .append(getPos(i, yZero - 0.2, zZero))
                .append(", shape=box, color=white];\n");
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
                .append(getPos(xZero - 0.2, i, zZero))
                .append(", shape=box, color=white];\n");
        }
        dot.append("\n");
        for (int i = 0; i <= 10; i++) {
            double z = zMin + (i * zStep);
            dot
                .append("        \"lz")
                .append(i)
                .append("\" [label=\"")
                .append(String.format(Locale.ROOT, "%." + zPrecision + "f", z))
                .append("\", tooltip=\"")
                .append(String.format(Locale.ROOT, "%." + (zPrecision + 1) + "f", z))
                .append("\", pos=")
                .append(getPos(xZero, yZero - 0.2, i))
                .append(", shape=box, color=white];\n");
        }
    }
    
    @Override
    protected boolean checkDistance(Fitness previous, Fitness current, double minDist) {
        double x1 = (previous.getValue(0) - xMin) / xStep;
        double y1 = (previous.getValue(1) - yMin) / yStep;
        double z1 = (previous.getValue(2) - zMin) / zStep;
        
        double x2 = (current.getValue(0) - xMin) / xStep;
        double y2 = (current.getValue(1) - yMin) / yStep;
        double z2 = (current.getValue(2) - zMin) / zStep;
        
        return dist(x1, y1, z1, x2, y2, z2) > minDist;
    }

    private static double dist(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }
    
    @Override
    protected String getSpecialNodeAttributes(boolean first, boolean last, double colorShade) {
        if (first) {
            return "color=\"#fdc086\"";
        } else if (last) {
            return "color=\"#7fc97f\"";
        } else {
            return String.format(Locale.ROOT, "color=\"0.737 0.179 %.3f\"", colorShade * 0.5 + 0.5);
        }
    }
    
}
