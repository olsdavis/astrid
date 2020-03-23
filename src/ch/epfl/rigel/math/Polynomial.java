package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Class used for the representation of a polynomial with positive integer exponents.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 17/02/2020
 */
public class Polynomial {
    private final double[] coefficients;

    private Polynomial(double[] coefficients) {
        this.coefficients = coefficients;
    }

    /**
     * @param coeffDom The dominant coefficient of the polynomial function.
     *                 Will be placed at the start of the coefficients list.
     * @param c        The rest of the coefficients. Will be placed after coeffDom.
     * @return a new Polynomial instance with given coefficients.
     */
    public static Polynomial of(double coeffDom, double... c) {
        checkArgument(coeffDom != 0);
        double[] coefficients = new double[c.length + 1];
        coefficients[0] = coeffDom;
        System.arraycopy(c, 0, coefficients, 1, c.length);
        return new Polynomial(coefficients);
    }

    /**
     * Evaluates the value of the Polynomial instance at a given point.
     *
     * @param x the value at which we evaluate the function.
     * @return the value the function attains at the given parameter value.
     */
    public double at(double x) {
        double res = 0;
        for (int i = 0; i < coefficients.length - 1; ++i) {
            res += coefficients[i];
            res *= x;
        }
        res += coefficients[coefficients.length - 1];
        return res;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < coefficients.length; ++i) {
            // do not write 0x^c
            if (coefficients[i] != 0) {
                double coefficient = coefficients[i];
                // do not write -1x^c nor 1x^c
                if (Math.abs(coefficient) == 1.0d) {
                    // add the minus if the coefficient is -1
                    if (coefficient == -1.0d) {
                        builder.append("-");
                    }
                } else {
                    builder.append(coefficient);
                }
                int power = coefficients.length - 1 - i;
                // write x^c if c is not null (constant coefficient)
                if (power != 0) {
                    builder.append("x");
                    // do not write x^1
                    if (power != 1) {
                        builder.append('^').append(power);
                    }
                }

                // if it is not the last element, and the next element is positive
                if (i < coefficients.length - 1 && coefficients[i + 1] > 0) {
                    builder.append('+');
                }
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
