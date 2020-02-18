package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Class used for the representation of a polynomial with positive integer exponents.
 */
public class Polynomial {
    private final double[] coefficients;

    private Polynomial(double[] coefficients) {
        this.coefficients = coefficients;
    }

    /**
     * Public method to initialize
     *
     * @param coeffDom     Corresponds to the dominant coefficient of the polynom.
     *                     Will be placed at the start of the coefficients list.
     * @param coefficients The rest of the coefficients. Will be placed after coeffDom
     * @return a new Polynomial instance with given coefficients.
     */
    public Polynomial of(double coeffDom, double... coefficients) {
        checkArgument(coeffDom == 0);
        double[] coeffs_list = new double[coefficients.length + 1];
        coeffs_list[0] = coeffDom;
        System.arraycopy(coefficients, 0, coeffs_list, 1, coeffs_list.length);
        return new Polynomial(coeffs_list);
    }

    /**
     * Evaluates the value of the polynomial function at a given point
     *
     * @param x the value at which we evaluate the function
     * @return the value the function attains at the given parameter value
     */
    public double at(double x) {
        double res = 0;
        for (int i = 0; i < coefficients.length - 1; i++) {
            res += coefficients[i];
            res *= x;
        }
        res += coefficients[coefficients.length - 1];
        return res;
    }

    /**
     * @return A visual representation of a polynomial expression
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < coefficients.length; i++) {
            if (!(coefficients[i] == 0)) {
                builder.append(coefficients[i]);
                builder.append("x^");
                builder.append(coefficients.length - 1 - i);
                builder.append(' ');
            }
            if (i < coefficients.length - 1 && coefficients[i + 1] > 0) {
                builder.append('+');
            }
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("unsupported operation");
    }
}
