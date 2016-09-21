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
 * User interface.
 *
 * @author jose a manas
 * @version 16.9.2016
 */
public class GUI
        implements ActionListener {
    private static final String TITLE = "RSA Machine (16.9.2016)";
    private final JFrame frame;

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

    public static void main(String[] args) {
        new GUI();
    }

    private GUI() {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container container = frame.getContentPane();

        container.add(mkPane(), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    private JPanel mkPane() {
        nBitsTF = new JTextField("16", 20);
        numberPTF = new JTextField();
        numberQTF = new JTextField();
        numberETF = new JTextField();
        numberNTF = new JTextField();
        numberDTF = new JTextField();

        nBitsTF.setHorizontalAlignment(SwingConstants.RIGHT);
        numberPTF.setEditable(false);
        numberQTF.setEditable(false);
        numberNTF.setEditable(false);
        numberETF.setEditable(false);
        numberDTF.setEditable(false);

        msgTF = new JTextField(20);
        blackMsgTF = new JTextField(20);
        redMsgTF = new JTextField();
        blackMsgTF.setEditable(false);
        redMsgTF.setEditable(false);

        generateButton = new JButton("generate");
        encryptButton = new JButton("encrypt");
        decryptButton = new JButton("decrypt");

        generateButton.addActionListener(this);
        encryptButton.addActionListener(this);
        decryptButton.addActionListener(this);

        JPanel panel = new JPanel();
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
        try {
            secretKey = new RSASecretKey(Integer.parseInt(nBitsTF.getText()));
            publicKey = secretKey.getRSAPublicKey();
            numberPTF.setText(secretKey.getP().toString(16));
            numberQTF.setText(secretKey.getQ().toString(16));
            numberNTF.setText(secretKey.getN().toString(16));
            numberETF.setText(publicKey.getE().toString(16));
            numberDTF.setText(secretKey.getD().toString(16));
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e, "generate",
                    JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void encrypt() {
        BigInteger msg = new BigInteger(msgTF.getText(), 16);
        BigInteger black = publicKey.encrypt(msg);
        blackMsgTF.setText(black.toString(16));
    }

    private void decrypt() {
        BigInteger black = new BigInteger(blackMsgTF.getText(), 16);
        BigInteger red = secretKey.decrypt(black);
        redMsgTF.setText(red.toString(16));
    }
}
