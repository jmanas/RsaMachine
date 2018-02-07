package factor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Factor {
    private static final BigInteger THRESHOLD = BigInteger.valueOf(10000);

    public static List<BigInteger> doit(BigInteger n) {
        if (n.compareTo(BigInteger.ONE) <= 0)
            throw new IllegalArgumentException();

        ArrayList<BigInteger> factors = new ArrayList<>();
        ClassicalLoop loop = new ClassicalLoop();
        while (true) {
            BigInteger nn = loop.getFactor(n, THRESHOLD);
            if (nn == null)
                break;
            factors.add(nn);
            n= n.divide(nn);
            if (n.equals(BigInteger.ONE))
                break;
        }

        while (!n.equals(BigInteger.ONE)) {
            if (MilnerRabin.primeTest(n)) {
                factors.add(n);
                break;
            } else {
                BigInteger nn = PollardRho.rhoBrent(n);
                if (nn.equals(BigInteger.ONE)) {
                    factors.add(n);
                    break;
                } else {
                    factors.add(nn);
                    n=n.divide(nn);
                }
            }
        }

        Collections.sort(factors);
        return factors;
    }
}
