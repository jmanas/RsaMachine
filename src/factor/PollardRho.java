package factor;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

// http://home.apache.org/~luc/commons-math-3.6-RC2-site/jacoco/org.apache.commons.math3.primes/PollardRho.java.html

public class PollardRho {

    /**
     * Implementation of the Pollard's rho factorization algorithm.
     * <p>
     * This implementation follows the paper "An improved Monte Carlo factorization algorithm"
     * by Richard P. Brent. This avoids the triple computation of f(x) typically found in Pollard's
     * rho implementations. It also batches several gcd computation into 1.
     * <p>
     * The backtracking is not implemented as we deal only with semi-primes.
     *
     * @param n number to factor, must be semi-prime.
     * @return a prime factor of n.
     */
    public static BigInteger rhoBrent(BigInteger n) {
        BigInteger x0 = BigInteger.valueOf(2);
        BigInteger m = BigInteger.valueOf(25);
        BigInteger cst = ClassicalLoop.LAST;
        BigInteger y = x0;
        BigInteger r = ONE;
        do {
            BigInteger x = y;
            for (BigInteger i = ZERO;
                 i.compareTo(r) < 0;
                 i = i.add(ONE)) {
                BigInteger y2 = y.pow(2);
                y = y2.add(cst).mod(n);
            }
            BigInteger k = ZERO;
            do {
                BigInteger bound = m.min(r.subtract(k));
                BigInteger q = ONE;
                for (BigInteger i = BigInteger.valueOf(-3);
                     i.compareTo(bound) < 0;
                     i = i.add(ONE)) { //start at -3 to ensure we enter this loop at least 3 times
                    BigInteger y2 = (y).pow(2);
                    y = ((y2.add(cst)).mod(n));
                    BigInteger divisor = x.subtract(y).abs();
                    if (divisor.equals(ZERO)) {
                        cst = cst.add(ClassicalLoop.LAST);
                        k = ZERO.subtract(m);
                        y = x0;
                        r = BigInteger.valueOf(1);
                        break;
                    }
                    BigInteger prod = divisor.multiply(q);
                    q = (prod.mod(n));
                    if (q.equals(ZERO))
                        return divisor.abs().gcd(n);
                }
                BigInteger out = q.abs().gcd(n);
                if (!out.equals(ONE))
                    return out;
                k = k.add(m);
            } while (k.compareTo(r) < 0);
            r = r.shiftLeft(1);
        } while (true);
    }
}
