package machine;

import java.math.BigInteger;

/**
 * RSA keys: public part.
 *
 * @author Jose A. Manas
 * @version 17.9.2016
 */
public class RSAPublicKey {
    private BigInteger n;        // modulus
    private BigInteger e;        // exponent

    public RSAPublicKey(BigInteger n, BigInteger e) {
        this.n = n;
        this.e = e;
    }

    public int size() {
        return n.bitLength();
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger encrypt(BigInteger msg) {
        return msg.modPow(e, n);
    }

    public BigInteger verify(BigInteger sig) {
        return sig.modPow(e, n);
    }

    @SuppressWarnings({"HardcodedLineSeparator"})
    public String toString() {
        StringBuilder buffer = new StringBuilder("RSA public:\n");
        buffer.append("n: ")
                .append(n.toString(16))
                .append('\n');
        buffer.append("e: ")
                .append(e.toString(16))
                .append('\n');
        return buffer.toString();
    }
}
