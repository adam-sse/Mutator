package net.ssehub.mutator.util;

import java.util.LinkedList;
import java.util.List;

public class AsciiTable {

    private String[] header;

    private List<String[]> rows;

    public AsciiTable(String... header) {
        this.header = header;
        this.rows = new LinkedList<>();
    }

    public void addRow(Object... row) {
        if (row.length <= this.header.length) {
            String[] cpy = new String[this.header.length];

            for (int i = 0; i < row.length; i++) {
                cpy[i] = row[i].toString();
            }

            for (int i = row.length; i < cpy.length; i++) {
                cpy[i] = "";
            }

            this.rows.add(cpy);
        } else
            throw new IllegalArgumentException("Row has too many columns");
    }

    public void addRow(String... row) {
        if (row.length == this.header.length) {
            this.rows.add(row);
        } else if (row.length < this.header.length) {
            String[] cpy = new String[this.header.length];
            System.arraycopy(row, 0, cpy, 0, row.length);
            for (int i = row.length; i < cpy.length; i++) {
                cpy[i] = "";
            }
            this.rows.add(cpy);
        } else
            throw new IllegalArgumentException("Row has too many columns");
    }

    @Override
    public String toString() {
        int[] widths = new int[this.header.length];

        for (int hi = 0; hi < this.header.length; hi++) {
            widths[hi] = this.header[hi].length();
            for (String[] row : this.rows) {
                widths[hi] = Math.max(widths[hi], row[hi].length());
            }

            widths[hi] += 2;
        }

        StringBuilder result = new StringBuilder();

        for (int hi = 0; hi < this.header.length; hi++) {
            result.append(Util.fillWithSpaces(this.header[hi], widths[hi]));
            if (hi + 1 < this.header.length) {
                result.append('|');
            }
        }
        result.append('\n');

        for (int hi = 0; hi < this.header.length; hi++) {
            result.append("-".repeat(widths[hi]));
            if (hi + 1 < this.header.length) {
                result.append('+');
            }
        }
        result.append('\n');

        for (String[] row : this.rows) {
            for (int hi = 0; hi < this.header.length; hi++) {
                result.append(Util.fillWithSpaces(row[hi], widths[hi]));
                if (hi + 1 < this.header.length) {
                    result.append('|');
                }
            }
            result.append('\n');
        }

        return result.toString();
    }

}
