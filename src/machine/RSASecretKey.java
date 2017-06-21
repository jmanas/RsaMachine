package machine;

import java.math.BigInteger;
import java.security.SecureRandom;

// 31.4.2012 check p = q.

/**
 * RSA keys: secret part.
 *
 * @author Jose A. Manas
 * @version 17.9.2016
 */
public class RSASecretKey {
    private static final int CERTAINTY = 80;
    public static final int F0 = 3;
    public static final int F1 = 5;
    public static final int F2 = 17;
    public static final int F3 = 257;
    public static final int F4 = 65537;

    private RSAPublicKey publicKey;     // public
    private BigInteger d;               // private

    private BigInteger p;
    private BigInteger q;

    public RSASecretKey(int keyLength) {
        BigInteger n, e;
        if (keyLength > 20)
            e = BigInteger.valueOf(F4);
        else
            e = BigInteger.valueOf(3);

        SecureRandom rnd = new SecureRandom();
        BigInteger p_1, q_1;
        BigInteger gcd;

        do {
            do {
                p = new BigInteger(keyLength / 2, CERTAINTY, rnd);
                p_1 = p.subtract(BigInteger.ONE);
                gcd = e.gcd(p_1);
            } while (!gcd.equals(BigInteger.ONE));

            do {
                q = new BigInteger(keyLength / 2, CERTAINTY, rnd);
                q_1 = q.subtract(BigInteger.ONE);
                gcd = e.gcd(q_1);
            } while (!gcd.equals(BigInteger.ONE));
        } while (p.equals(q));

        n = p.multiply(q);
        BigInteger phi = p_1.multiply(q_1);
        d = e.modInverse(phi);
        publicKey = new RSAPublicKey(n, e);
    }

    public RSASecretKey(BigInteger n, BigInteger e, BigInteger d) {
        publicKey = new RSAPublicKey(n, e);
        this.d = d;
    }

    public int size() {
        return publicKey.size();
    }

    public RSAPublicKey getRSAPublicKey() {
        return publicKey;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getN() {
        return publicKey.getN();
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger encrypt(BigInteger msg) {
        return publicKey.encrypt(msg);
    }

    public BigInteger decrypt(BigInteger enc) {
        return enc.modPow(d, getN());
    }

    public BigInteger sign(BigInteger msg) {
        return msg.modPow(d, getN());
    }

    public BigInteger verify(BigInteger sig) {
        return publicKey.verify(sig);
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("n= ").append(publicKey.getN().toString(16)).append('\n');
        buffer.append("e= ").append(publicKey.getE().toString(16)).append('\n');
        buffer.append("d= ").append(d.toString(16)).append('\n');
        return buffer.toString();
    }
}
