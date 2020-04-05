package ch.epfl.test;

/**
 * @author Oscar Davis
 * Creation date: 05/04/2020
 */
public class Bench {

    public static void printBench(Runnable runnable, int iterations) {
        final long start = System.currentTimeMillis();
        runnable.run();
        final long diff = System.currentTimeMillis() - start;
        System.out.println("Time: " + diff + "ms, ns/op: "
                + ((float) diff / iterations) * 1_000_000f);
    }

}
