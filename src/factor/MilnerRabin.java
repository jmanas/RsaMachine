package factor;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Random;

public class MilnerRabin {
    private static final int ITERATIONS = 6;    // to be safe

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);

    public static boolean primeTest(BigInteger n) {
        if (n.compareTo(TWO) < 0)
            return false;
        if (n.equals(TWO))
            return true;
        if (n.getLowestSetBit() > 0)
            return false;

        BigInteger n_1 = n.subtract(ONE);
        BigInteger s = n_1.shiftRight(n_1.getLowestSetBit());
        //s must be odd, it is not checked here

        Random random = new Random();
        for (int i = 0; i < ITERATIONS; i++) {
            BigInteger r = BigInteger.valueOf(random.nextLong()).abs();
            BigInteger a = r.mod(n_1).add(ONE);
            BigInteger tmp = s;
            BigInteger mod = a.modPow(tmp, n);
            while (!tmp.equals(n_1) && !mod.equals(ONE) && !mod.equals(n_1)) {
                mod = mod.multiply(mod).mod(n);
                tmp = tmp.shiftLeft(1);
            }
            if (!mod.equals(n_1) && tmp.getLowestSetBit() > 0)
                return false; // definite
        }
        return true;    // likely
    }

    public static void main(String[] args) {
        for (int n = 0; n < 129; n++)
            test(n);

        test(53 * 53);
        test(53 * 59);

        test(5957);
        test(5958);
        test(5959); // 59, 101
        test(5960);
        test(5961);

        test(41917);    // 167, 251
        test(5510389);    // prime
        test(new BigInteger("2345678917"));   // prime
        test(new BigInteger("871401683722001"));   // 29857357, 29185493 // 50 bits
        test(new BigInteger("887945350734512732287"));   // 33505342861, 26501604667 // 70 bits
        test(new BigInteger("818145333098156554636866568691"));   // 961828124510959, 850614899116349 // 100 bits
    }

    private static void test(int n) {
        BigInteger bn = BigInteger.valueOf(n);
        test(bn);
    }

    private static PrintStream test(BigInteger bn) {
        return System.out.printf("%d: %b%n", bn, primeTest(bn));
    }
}
