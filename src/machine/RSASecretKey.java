package machine;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import static java.math.BigInteger.ONE;

/**
 * RSA keys: secret part.
 *
 * @author Jose A. Manas
 * @version 17.9.2016
 */
public class RSASecretKey {
    public static final int F0 = 3;
    public static final int F1 = 5;
    public static final int F2 = 17;
    public static final int F3 = 257;
    private static final int F4 = 65537;

    private RSAPublicKey publicKey;     // public
    private BigInteger d;               // private

    private BigInteger p;
    private BigInteger q;

    public RSASecretKey(int keyLength) {
        BigInteger n = null;

        BigInteger e;
        if (keyLength > 20)
            e = BigInteger.valueOf(F4);
        else
            e = BigInteger.valueOf(3);

        SecureRandom rnd = new SecureRandom();
        BigInteger p_1 = null;
        BigInteger q_1 = null;

        // let's try p & q of equal length size/2
        // if after some rounds there is no way, relax equal size requirement

        // e.g. for 6 bits, there is no 3+3 solution
        //      e= 3; p= 3; q= 11; n= 33;
        int counter = 0;
        while (true) {      // let's try until all conditions are satisfied
            counter++;
            int length_p;
            if (counter < 50)
                length_p = keyLength / 2;
            else if (counter < 100)
                length_p = keyLength / 2 - 1;
            else
                throw new IllegalArgumentException("no primes found!");

            p = BigInteger.probablePrime(length_p, rnd);
            p_1 = p.subtract(ONE);
            if (!e.gcd(p_1).equals(ONE))
                continue;

            int length_q = keyLength - length_p;
            q = BigInteger.probablePrime(length_q, rnd);

            // we need two different primes
            if (p.equals(q))
                continue;

            n = p.multiply(q);
            // be sure n = p*q is of the desired length
            // e.g. for 10 bits
            //      p= 17 (5 bits)
            //      q= 23 (5 bits)
            //      n= 391 (9 bits)
            if (n.bitLength() != keyLength)
                continue;

            q_1 = q.subtract(ONE);
            if (!e.gcd(q_1).equals(ONE))
                continue;

            if (p.gcd(q).equals(ONE))
                break;
        }

        publicKey = new RSAPublicKey(n, e);

        BigInteger phi = p_1.multiply(q_1);
        d = e.modInverse(phi);
    }

    public static RSASecretKey mk(BigInteger p, BigInteger q, BigInteger e) {
        BigInteger p_1 = p.subtract(ONE);
        BigInteger q_1 = q.subtract(ONE);
        BigInteger phi = p_1.multiply(q_1);
        if (!e.gcd(phi).equals(ONE))
            throw new IllegalArgumentException("gcd(e, phi) != 1");

        BigInteger n = p.multiply(q);
        BigInteger d = e.modInverse(phi);
        return new RSASecretKey(n, e, d);
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

        BigInteger p_1 = p.subtract(ONE);
        BigInteger q_1 = q.subtract(ONE);
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
