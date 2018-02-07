package machine;

import factor.Factor;
import layout.TableLayout;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Key factorization panel.
 *
 * @author jose a manas
 * @version 5.2.2018
 */
public class FactorPanel
        implements ActionListener {
    private final JPanel panel;

    private List<BigInteger> factors;

    private int nBits = -1;
    private RSAPublicKey publicKey;
    private RSASecretKey secretKey;

    private JTextField numberNTF;
    private JTextField numberETF;
    private JTextField nBitsTF;
    private JTextField factorsTF;
    private JTextField numberDTF;

    private JTextField msgTF;
    private JTextField blackMsgTF;
    private JTextField redMsgTF;

    private JButton factorButton;
    private JButton encryptButton;
    private JButton decryptButton;

    private BigInteger msg;
    private BigInteger black;
    private BigInteger red;

    public FactorPanel() {
        numberNTF = new JTextField();
        numberETF = new JTextField(20);

        nBitsTF = new JTextField();
        factorsTF = new JTextField();
        numberDTF = new JTextField();

        nBitsTF.setEditable(false);
        factorsTF.setEditable(false);
        numberDTF.setEditable(false);

        msgTF = new JTextField(20);
        blackMsgTF = new JTextField(20);
        redMsgTF = new JTextField();
        redMsgTF.setEditable(false);

        factorButton = new JButton("crack");
        encryptButton = new JButton("encrypt");
        decryptButton = new JButton("decrypt");

        factorButton.addActionListener(this);
        encryptButton.addActionListener(this);
        decryptButton.addActionListener(this);

        panel = new JPanel();
        panel.setBackground(Color.WHITE);
        double[] cols = new double[3];
        Arrays.fill(cols, TableLayout.PREFERRED);
        double[] rows = new double[9];
        Arrays.fill(rows, TableLayout.PREFERRED);
        TableLayout layout = new TableLayout(cols, rows);
        layout.setHGap(5);
        layout.setVGap(2);
        panel.setLayout(layout);

        int row = 0;
        add(panel, row++, "n", numberNTF);
        add(panel, row++, "e", numberETF, factorButton);
        add(panel, row++, "n bits", nBitsTF);
        add(panel, row++, "factors", factorsTF);
        add(panel, row++, "d", numberDTF);

        add(panel, row++, "msg", msgTF, encryptButton);
        add(panel, row++, "black", blackMsgTF, decryptButton);
        add(panel, row++, "red", redMsgTF);

        Border border = BorderFactory.createEmptyBorder(10, 5, 10, 5);
        panel.setBorder(border);
    }

    public JPanel getPanel() {
        return panel;
    }

    private void add(JPanel panel, int row, String label, JComponent comp1, JComponent comp2) {
        panel.add(new JLabel(label), String.format("%d,%d", 0, row));
        panel.add(comp1, String.format("%d,%d", 1, row));
        panel.add(comp2, String.format("%d,%d", 2, row));
    }

    private void add(JPanel panel, int row, String label, JComponent comp) {
        panel.add(new JLabel(label), String.format("%d,%d", 0, row));
        panel.add(comp, String.format("%d,%d,%d,%d", 1, row, 2, row));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == factorButton)
            crack();
        else if (src == encryptButton)
            encrypt();
        else if (src == decryptButton)
            decrypt();
    }

    private void crack() {
        nBits = -1;
        factors = null;
        secretKey= null;
        publicKey= null;
        black= null;
        red= null;

        if (numberNTF.getText().length() == 0)
            return;
        if (numberETF.getText().length() == 0)
            return;

        int radix = GUI.getRadix();
        try {
            BigInteger n = new BigInteger(numberNTF.getText(), radix);
            BigInteger e = new BigInteger(numberETF.getText(), radix);

            nBits = n.bitLength();
            factors = Factor.doit(n);
            if (factors == null || factors.size() != 2)
                return;
            BigInteger p = factors.get(0);
            BigInteger q = factors.get(1);
            secretKey = RSASecretKey.mk(p, q, e);
            publicKey = secretKey.getRSAPublicKey();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel,
                    e.getMessage(), e.getClass().getSimpleName(),
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            present();
        }
    }

    private void encrypt() {
        if (msgTF.getText().length() == 0)
            return;
        if (publicKey == null)
            return;

        int radix = GUI.getRadix();
        try {
            msg = new BigInteger(msgTF.getText(), radix);
            black = publicKey.encrypt(msg);
            present();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel,
                    e.getMessage(), e.getClass().getSimpleName(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decrypt() {
        if (blackMsgTF.getText().length() == 0)
            return;
        if (secretKey == null)
            return;

        int radix = GUI.getRadix();
        try {
            black = new BigInteger(blackMsgTF.getText(), radix);
            red = secretKey.decrypt(black);
            present();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel,
                    e.getMessage(), e.getClass().getSimpleName(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void present() {
        nBitsTF.setText("");
        factorsTF.setText("");
        numberDTF.setText("");

        msgTF.setText("");
        blackMsgTF.setText("");
        redMsgTF.setText("");

        int radix = GUI.getRadix();

        if (nBits > 0)
            nBitsTF.setText(String.valueOf(nBits));
        factorsTF.setText(present(factors, radix));

        if (secretKey != null)
            numberDTF.setText(secretKey.getD().toString(radix));

        if (msg != null)
            msgTF.setText(msg.toString(radix));
        if (black != null)
            blackMsgTF.setText(black.toString(radix));
        if (red != null)
            redMsgTF.setText(red.toString(radix));
    }

    private String present(List<BigInteger> list, int radix) {
        if (list == null || list.isEmpty())
            return "";
        StringBuilder builder= new StringBuilder();
        for (BigInteger n: list) {
            if (builder.length() > 0)
                builder.append(", ");
            builder.append(n.toString(radix));
        }
        return builder.toString();
    }
}
