package machine;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

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

        int counter = 0;
        do {
            if (counter++ > 100)
                throw new IllegalArgumentException("no primes found!");
            do {
                p = new BigInteger((keyLength + 1) / 2, CERTAINTY, rnd);
                p_1 = p.subtract(BigInteger.ONE);
                gcd = e.gcd(p_1);
            } while (!gcd.equals(BigInteger.ONE));

            do {
                q = new BigInteger(keyLength / 2, CERTAINTY, rnd);
                q_1 = q.subtract(BigInteger.ONE);
                gcd = e.gcd(q_1);
            } while (!gcd.equals(BigInteger.ONE));

            gcd = p.gcd(q);
        } while (!gcd.equals(BigInteger.ONE));

        n = p.multiply(q);
        publicKey = new RSAPublicKey(n, e);

        BigInteger phi = p_1.multiply(q_1);
        d = e.modInverse(phi);
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
        if (size() < 1000)
            return decrypt_raw(enc);
        else
            return decrypt_crt(enc);
    }

    private BigInteger decrypt_raw(BigInteger enc) {
        return enc.modPow(d, getN());
    }

    // CRT _ Chinese Remainder Theorem
    // used in smart cards
    private BigInteger decrypt_crt(BigInteger enc) {
        // precalculations - when d is set
        BigInteger n = getN();

        BigInteger p_1 = p.subtract(BigInteger.ONE);
        BigInteger q_1 = q.subtract(BigInteger.ONE);
        BigInteger d_p_1 = d.mod(p_1);
        BigInteger d_q_1 = d.mod(q_1);

        BigInteger xp = q.multiply(q.modInverse(p));
        BigInteger xq = p.multiply(p.modInverse(q));

        // on black message
        BigInteger mp = enc.modPow(d_p_1, p);
        BigInteger mq = enc.modPow(d_q_1, q);
        BigInteger sp = mp.multiply(xp);
        BigInteger sq = mq.multiply(xq);
        BigInteger m = sp.add(sq).mod(n);
        return m;
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

    public static void main(String[] args) {
        test(256);
        test(512);
        test(1024);
        test(2048);
    }

    private static Random random = new Random();

    private static void test(int keyLength) {
        for (int i = 0; i < 16; i++) {
            RSASecretKey secretKey = new RSASecretKey(keyLength);
            BigInteger red = BigInteger.valueOf(Math.abs(random.nextLong()));
            BigInteger black = secretKey.encrypt(red);
            BigInteger recovered1 = secretKey.decrypt_raw(black);
            BigInteger recovered2 = secretKey.decrypt_crt(black);
            if (!red.equals(recovered1))
                System.err.println("ERROR 1: " + keyLength);
            if (!red.equals(recovered2))
                System.err.println("ERROR 2: " + keyLength);
        }
    }

}
