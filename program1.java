import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

public class program1 {
    private static final int NUM_THREADS = 8;
    private static final int RANGE_START = 2; // Starts at 2 to excluse 0 and 1
    private static final int RANGE_END = 100000000;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        List<Thread> threads = new ArrayList<>();
        Queue<Integer> primes = new ConcurrentLinkedQueue<>();
        AtomicInteger counter = new AtomicInteger(RANGE_START); // Shared counter for dynamic segment allocation

        for (int i = 0; i < NUM_THREADS; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    int current = counter.getAndIncrement(); // Atomic so "synchronized not" needed here
                    if (current > RANGE_END) {
                        break;
                    }
                    processNumber(current, primes);
                }
            });

            threads.add(thread);
        }


        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        double executionTime = (endTime - startTime) / 1000.0; // Divide by 1000.0 to convert milliseconds to seconds

        writeToFile(executionTime, primes);
    }

    private static void writeToFile(double executionTime, Queue<Integer> primes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("primes.txt"))) {

            List<Integer> primesList = new ArrayList<>(primes);
            primesList.sort(Integer::compareTo);

            writer.write(String.format("%.3f seconds, %d, %d", executionTime, primes.size(), calculateSum(primesList)));
            writer.newLine();

            List<Integer> topTenPrimes = getTopTenPrimes(primesList); // Getting a list with top 10 largest primes
            for (int prime : topTenPrimes) {
                writer.write(prime + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processNumber(int number, Queue<Integer> primes) {
        if (isPrime(number)) {
            primes.add(number);
        }
    }

    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) { // Efficient prime algorith as square root of n has less iterations compared to n/2
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    private static long calculateSum(List<Integer> primes) {
        return primes.stream().mapToLong(Integer::intValue).sum();
    }

    private static List<Integer> getTopTenPrimes(List<Integer> primes) {
        int size = primes.size();
        return (size > 10) ? primes.subList(size - 10, size) : primes;
    }
}
