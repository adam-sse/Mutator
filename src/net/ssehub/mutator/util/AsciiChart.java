package net.ssehub.mutator.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AsciiChart {

    private int rows;

    private List<Point> points;

    private int minX;

    private int maxX;

    private double minY;

    private double maxY;

    public AsciiChart(int rows) {
        this.rows = rows;
        this.points = new LinkedList<>();
    }

    public void addPoint(int x, double y) {
        if (points.isEmpty()) {
            this.minX = x;
            this.maxX = x;
            this.minY = y;
            this.maxY = y;
        }

        Point p = new Point(x, y);
        this.points.add(p);

        this.minX = Math.min(this.minX, x);
        this.maxX = Math.max(this.maxX, x);
        this.minY = Math.min(this.minY, y);
        this.maxY = Math.max(this.maxY, y);
    }

    @Override
    public String toString() {
        int width = this.maxX - this.minX + 1;

        double ySteps;
        int rows = this.rows;
        if (this.minY == this.maxY) {
            rows = 1;
            ySteps = 1;
        } else {
            ySteps = (this.maxY - this.minY) / (rows - 1);
        }

        boolean[][] dots = new boolean[width][rows];
        for (Point p : this.points) {
            int ry = rows - 1 - (int) Math.round((p.y - this.minY) / ySteps);
            dots[p.x - this.minX][ry] = true;
        }

        int columnWidth = Math.max(Integer.toString(this.maxX).length(), Integer.toString(this.minX).length()) + 1;
        columnWidth = Math.max(columnWidth, 3);

        StringBuilder result = new StringBuilder();

        for (int row = 0; row < rows; row++) {

            result.append(String.format(Locale.ROOT, "%10.2f | ", (rows - row - 1) * ySteps + this.minY));

            for (int column = 0; column < width; column++) {
                if (dots[column][row]) {
                    result.append(Util.fillWithSpaces("*", columnWidth));
                } else {
                    result.append(" ".repeat(columnWidth));
                }
            }

            result.append('\n');
        }

        result.append("-".repeat(11)).append('+').append("-".repeat(columnWidth * width)).append('\n');
        result.append(" ".repeat(11)).append('|');
        for (int xLbl = this.minX; xLbl <= this.maxX; xLbl++) {
            result.append(String.format(" %0" + (columnWidth - 1) + "d", xLbl));
        }
        result.append('\n');

        return result.toString();
    }

    private static final class Point {

        private int x;

        private double y;

        public Point(int x, double y) {
            this.x = x;
            this.y = y;
        }

    }

}
