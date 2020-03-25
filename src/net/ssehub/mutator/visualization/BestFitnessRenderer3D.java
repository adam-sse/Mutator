package net.ssehub.mutator.visualization;

import java.util.List;
import java.util.Locale;

import net.ssehub.mutator.mutation.fitness.Fitness;

public class BestFitnessRenderer3D extends BestFitnessRenderer {

    private double zMin;
    private double zMax;
    
    private double zStep;
    
    private int zPrecision;
    
    public BestFitnessRenderer3D(String dotExe) {
        super(dotExe);
    }
    
    @Override
    protected boolean checkDimension(List<Fitness> bestFitnesses) {
        for (Fitness fitness : bestFitnesses) {
            if (fitness.numValues() != 3) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected void calcAxisScale(List<Fitness> bestFitnesses) {
        super.calcAxisScale(bestFitnesses);
        
        zMin = Double.MAX_VALUE;
        zMax = Double.MIN_VALUE;
        
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
    protected String getArrowHead() {
        return "triangle";
    }
    
    private String getPos(double x, double y, double z) {
        return String.format(Locale.ROOT, "\"%f,%f,%f!\"", x, y, z);
    }
    
    @Override
    protected String getPos(Fitness fitness) {
        double x = (fitness.getValue(0) - xMin) / xStep + 1.0;
        double y = (fitness.getValue(1) - yMin) / yStep + 1.0;
        double z = (fitness.getValue(2) - zMin) / zStep + 1.0;
        
        return getPos(x, y, z);
    }

    @Override
    protected String getTooltipp(Fitness fitness) {
        return String.format(Locale.ROOT, "\"%." + (xPrecision + 1) + "f, %." + (yPrecision + 1) + "f"
                + ", %." + (zPrecision + 1) + "f\"",
                fitness.getValue(0), fitness.getValue(1), fitness.getValue(2));
    }
    
    @Override
    protected void createAxes(StringBuilder dot) {
        dot
            .append("        \"origin\" [label=\"\", pos=" + getPos(0, 0, 0) + ", width=0, height=0];\n")
            .append("        \"xHead\" [label=\"\", pos=" + getPos(11.2, 0, 0) + ", width=0, height=0];\n")
            .append("        \"yHead\" [label=\"\", pos=" + getPos(0, 11.2, 0) + ", width=0, height=0];\n")
            .append("        \"zHead\" [label=\"\", pos=" + getPos(0, 0, 11.2) + ", width=0, height=0];\n")
            .append("        \"origin\" -> \"xHead\";\n")
            .append("        \"origin\" -> \"yHead\";\n")
            .append("        \"origin\" -> \"zHead\";\n")
            .append("\n");
        
        // axis labels aren't rendered anyway
    }
    
    @Override
    protected boolean checkDistance(Fitness previous, Fitness current, double minDist) {
        double x1 = (previous.getValue(0) - xMin) / xStep + 1.0;
        double y1 = (previous.getValue(1) - yMin) / yStep + 1.0;
        double z1 = (previous.getValue(2) - zMin) / zStep + 1.0;
        
        double x2 = (current.getValue(0) - xMin) / xStep + 1.0;
        double y2 = (current.getValue(1) - yMin) / yStep + 1.0;
        double z2 = (current.getValue(2) - zMin) / zStep + 1.0;
        
        
        return dist(x1, y1, z1, x2, y2, z2) > minDist;
    }

    private static double dist(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }
    
}
