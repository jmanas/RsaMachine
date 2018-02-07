package machine;

import layout.TableLayout;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Key generation panel.
 *
 * @author jose a manas
 * @version 5.2.2018
 */
public class GenPanel
        implements ActionListener {
    private final JPanel panel;

    private RSAPublicKey publicKey;
    private RSASecretKey secretKey;

    private JTextField nBitsTF;
    private JTextField numberPTF;
    private JTextField numberQTF;
    private JTextField numberETF;
    private JTextField numberNTF;
    private JTextField numberDTF;
    private JTextField msgTF;
    private JTextField blackMsgTF;
    private JTextField redMsgTF;

    private JButton generateButton;
    private JButton encryptButton;
    private JButton decryptButton;
    private BigInteger msg;
    private BigInteger black;
    private BigInteger red;

    public GenPanel() {
        nBitsTF = new JTextField("16", 20);
        numberPTF = new JTextField();
        numberQTF = new JTextField();
        numberETF = new JTextField();
        numberNTF = new JTextField();
        numberDTF = new JTextField();

        numberPTF.setEditable(false);
        numberQTF.setEditable(false);
        numberNTF.setEditable(false);
        numberETF.setEditable(false);
        numberDTF.setEditable(false);

        msgTF = new JTextField(20);
        blackMsgTF = new JTextField(20);
        redMsgTF = new JTextField();
        redMsgTF.setEditable(false);

        generateButton = new JButton("generate");
        encryptButton = new JButton("encrypt");
        decryptButton = new JButton("decrypt");

        generateButton.addActionListener(this);
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
        add(panel, row++, "n bits", nBitsTF, this.generateButton);
        add(panel, row++, "p", numberPTF);
        add(panel, row++, "q", numberQTF);
        add(panel, row++, "n", numberNTF);
        add(panel, row++, "e", numberETF);
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
        if (src == generateButton)
            generate();
        else if (src == encryptButton)
            encrypt();
        else if (src == decryptButton)
            decrypt();
    }

    private void generate() {
        black = null;
        red = null;
        try {
            secretKey = new RSASecretKey(Integer.parseInt(nBitsTF.getText()));
            publicKey = secretKey.getRSAPublicKey();
            present();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel,
                    e.getMessage(), e.getClass().getSimpleName(),
                    JOptionPane.ERROR_MESSAGE);
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
        numberPTF.setText("");
        numberQTF.setText("");
        numberNTF.setText("");
        numberETF.setText("");
        numberDTF.setText("");

        msgTF.setText("");
        blackMsgTF.setText("");
        redMsgTF.setText("");

        int radix = GUI.getRadix();

        if (secretKey != null) {
            numberPTF.setText(secretKey.getP().toString(radix));
            numberQTF.setText(secretKey.getQ().toString(radix));
            numberNTF.setText(secretKey.getN().toString(radix));
            numberETF.setText(publicKey.getE().toString(radix));
            numberDTF.setText(secretKey.getD().toString(radix));
        }

        if (msg != null)
            msgTF.setText(msg.toString(radix));
        if (black != null)
            blackMsgTF.setText(black.toString(radix));
        if (red != null)
            redMsgTF.setText(red.toString(radix));
    }
}
