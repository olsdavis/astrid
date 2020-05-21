package ch.epfl.test;

/**
 * @author Oscar Davis
 * Creation date: 05/04/2020
 */
public class Bench {

    public static void printBench(Runnable runnable, int iterations) {
        final long start = System.nanoTime();
        runnable.run();
        final long diff = System.nanoTime() - start;
        System.out.println("Time: " + diff / 1000000L + "ms, ns/op: "
                + ((double) diff / iterations));
    }

}
