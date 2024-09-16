package se.lenborje.tdi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

import static java.util.Arrays.*;

/**
 * TDI, i.e. Test primality by Division of Integers, implemented using java Streams
 * @see <a href="https://en.wikipedia.org/wiki/Wheel_factorization">Wheel factorization</a>
 */
@SuppressWarnings("java:S106")
public class StreamTDI {
    // The first primes. It is not especially helpful to extend this list beyond 3 or maybe 4 elements
    private static final int[] pa = {2,3,5};
    // The product of the first primes
    private static final int product = IntStream.of(pa).reduce(1, (a, b) -> a*b);
    // The first wheel, i.e. the numbers that are not divisible by any of the first primes
    // Consecutive wheels are generated by adding the product of the first primes to the elements of the first wheel
    private static int[] firstWheel = getFirstWheel(pa, product);
    // The increments between the elements of the wheel
    private static int[] increments = getIncrements(firstWheel, product);

    /**
     * The prime candidates are the first primes and the numbers in the wheel, generated from the increments
     * @return a stream of prime candidates
     */
    private static IntStream primeCandicates() {
        return IntStream.concat(IntStream.of(pa), IncrementedStream.of(firstWheel[0], increments));
    }

    /**
     * Generate the first wheel, i.e. the numbers that are not divisible by any of the first primes
     * @param pa the first primes
     * @param product the product of the first primes
     * @return the first wheel
     */
    private static int[] getFirstWheel(int[] pa, int product) {
        return IntStream.rangeClosed(pa[pa.length - 1] + 1, product + 1)
                .filter(n -> IntStream.of(pa).noneMatch(p -> n % p == 0))
                .toArray();
    }

    /**
     * Generate the increments between the elements of the wheel
     * @param wheel the wheel
     * @param product the product of the first primes
     * @return the increments
     */
    private static int[] getIncrements(int[] wheel, int product) {
        int[] ia = Arrays.copyOfRange(wheel, 0, wheel.length);
        for (int i = 0; i < ia.length-1; i++) {
            ia[i] = wheel[i+1] - wheel[i];
        }
        ia[ia.length-1] = product - wheel[wheel.length-1] + wheel[0];
        return ia;
    }


    public static void main(String[] args) {
        Queue<String> cmds = new LinkedList<>(asList(args));

        System.out.printf("pa: %s%n", Arrays.toString(pa));
        System.out.printf("product: %d%n", product);
        System.out.printf("firstWheel: %s%n", Arrays.toString(firstWheel));
        System.out.printf("increments: %s%n", Arrays.toString(increments));

        try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            String ans = null;
            while (args.length==0 || !cmds.isEmpty()) {
                if (args.length==0) {
                    System.out.println("Maximum?");
                    ans = in.readLine();
                } else {
                    ans = cmds.poll();
                }
                if (ans == null)
                    break;
                int max = Integer.parseInt(ans);

                long start = System.currentTimeMillis();

                long count = primeCandicates().takeWhile(n -> n <= max)
                    .filter(n -> primeCandicates().takeWhile(p -> p*p <= n).noneMatch(p -> n % p == 0))
                    .count();

                long end = System.currentTimeMillis();

                System.out.printf("Primes: %d in %d ms%n", count, end-start);

            }
        } catch (IOException | SecurityException | IllegalArgumentException e) {
            e.printStackTrace(System.err);
        }
    }
}

class IncrementedStream implements IntSupplier {
    private int nextp;
    private final int[] ia;
    private int i = 0;

    private IncrementedStream(int firstp, int[] ia) {
        this.nextp = firstp;
        this.ia = ia;
    }

    @Override
    public int getAsInt() {
        int result = nextp;
        nextp += ia[i++];
        if (i==ia.length) {
            i = 0;
        }
        return result;
    }

    public static IntStream of(int firstp, int[] ia) {
        return IntStream.generate(new IncrementedStream(firstp, ia));
    }

}