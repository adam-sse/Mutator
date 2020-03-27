double* multiply(double* x, int xR, int xC, double* y, int yR, int yC) {
    double* m = malloc(8 * xR * yC);
    for (int row = 0; row < xR; row++) {
        for (int column = 0; column < yC; column++) {
            m[row * yC + column] = 0.0;
            for (int i = 0; i < xC; i++) {
                m[row * yC + column] += x[row * xC + i] * y[i * yC + column];
            }
        }
    }
    return m;
}
