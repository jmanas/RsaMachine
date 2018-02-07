package factor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigInteger.ZERO;

public class ClassicalLoop {
    public static final BigInteger LAST;

    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static List<BigInteger> primes;

    static {
        int[] smallprimes = {
                2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};
        primes = new ArrayList<>();
        for (int n : smallprimes)
            primes.add(BigInteger.valueOf(n));
        LAST = primes.get(smallprimes.length-1);
    }

    private int next;

    public ClassicalLoop() {
        next = 0;
    }

    // returns a prime factor of n,
    // smaller then upto
    // return n if n is prime
    // return null if no prime is found
    public BigInteger getFactor(BigInteger n, BigInteger upto) {
        // check preloaded prime list
        for (; next < primes.size(); next++) {
            BigInteger prime = primes.get(next);
            if (prime.compareTo(n) >= 0)
                return n;
            BigInteger r = n.mod(prime);
            if (r.equals(ZERO))
                return prime;
        }

        BigInteger test = primes.get(primes.size() - 1).add(TWO);
        BigInteger max = sqroot(n);
        while (true) {
            if (test.compareTo(max) > 0)
                return n;
            if (test.compareTo(upto) >= 0)
                break;
            BigInteger r = n.mod(test);
            if (r.equals(ZERO))
                return test;
            test = test.add(TWO);
        }

        return null;
    }

    public static List<BigInteger> getFactors(BigInteger n) {
        if (n.compareTo(BigInteger.ONE) <= 0)
            throw new IllegalArgumentException();
        List<BigInteger> factors = new ArrayList<>();
        for (BigInteger prime : primes) {
            n = getFactors(factors, prime, n);
            if (n.equals(BigInteger.ONE))
                break;
        }

        if (n.compareTo(BigInteger.ONE) > 0) {
            BigInteger test = primes.get(primes.size() - 1).add(TWO);
            BigInteger max = sqroot(n);
            while (test.compareTo(max) <= 0) {
                BigInteger n2 = getFactors(factors, test, n);
                if (!n2.equals(n)) {
                    n = n2;
                    max = sqroot(n);
                }
                test = test.add(TWO);
            }
        }

        if (!n.equals(BigInteger.ONE))
            factors.add(n);
        return factors;
    }

    private static BigInteger getFactors(List<BigInteger> factors, BigInteger prime, BigInteger n) {
        for (; ; ) {
            BigInteger r = n.mod(prime);
            if (!r.equals(ZERO))
                return n;
            factors.add(prime);
            n = n.divide(prime);
            if (n.compareTo(prime) < 0)
                return n;
        }
    }

    private static BigInteger sqroot(BigInteger x) {
        if (x.compareTo(TWO) < 0)
            return x;
        BigInteger y;
        // starting with y = x / 2 avoids magnitude issues with x squared
        y = x.divide(TWO);
        while (y.compareTo(x.divide(y)) > 0)
            y = ((x.divide(y)).add(y)).divide(TWO);
        return y;
    }
}
