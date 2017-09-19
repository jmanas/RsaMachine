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
    private static final String TITLE =
            String.format("RSA Machine (%s)", Version.VERSION);

    private final JFrame frame;
    private int radix = 16;

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

        container.add(new JLabel(" (academic - prime numbers are not necessarily optimal for RSA) "), BorderLayout.NORTH);
        container.add(mkPane(), BorderLayout.CENTER);
        container.add(mkPresentationPane(), BorderLayout.SOUTH);

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

    private JPanel mkPresentationPane() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        final JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("binary");
        comboBox.addItem("octal");
        comboBox.addItem("decimal");
        comboBox.addItem("hexadecimal");
        comboBox.setSelectedItem("hexadecimal");
        panel.add(comboBox);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String item = (String) comboBox.getSelectedItem();
                switch (item) {
                    case "binary":
                        radix = 2;
                        break;
                    case "octal":
                        radix = 8;
                        break;
                    case "decimal":
                        radix = 10;
                        break;
                    case "hexadecimal":
                        radix = 16;
                        break;
                }
            }
        });

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
            numberPTF.setText("");
            numberQTF.setText("");
            numberNTF.setText("");
            numberETF.setText("");
            numberDTF.setText("");
            secretKey = new RSASecretKey(Integer.parseInt(nBitsTF.getText()));
            publicKey = secretKey.getRSAPublicKey();
            numberPTF.setText(secretKey.getP().toString(radix));
            numberQTF.setText(secretKey.getQ().toString(radix));
            numberNTF.setText(secretKey.getN().toString(radix));
            numberETF.setText(publicKey.getE().toString(radix));
            numberDTF.setText(secretKey.getD().toString(radix));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    e.getMessage(), e.getClass().getSimpleName(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void encrypt() {
        try {
            BigInteger msg = new BigInteger(msgTF.getText(), radix);
            BigInteger black = publicKey.encrypt(msg);
            blackMsgTF.setText(black.toString(radix));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    e.getMessage(), e.getClass().getSimpleName(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decrypt() {
        try {
            BigInteger black = new BigInteger(blackMsgTF.getText(), radix);
            BigInteger red = secretKey.decrypt(black);
            redMsgTF.setText(red.toString(radix));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    e.getMessage(), e.getClass().getSimpleName(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
