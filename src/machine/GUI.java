package machine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User interface.
 *
 * @author jose a manas
 * @version 16.9.2016
 */
public class GUI {
    private static final String TITLE =
            String.format("RSA Machine (%s)", Version.VERSION);

    private final GenPanel genPanel;
    private final FactorPanel factorPanel;

    private static int radix = 16;

    public static void main(String[] args) {
        new GUI();
    }

    private GUI() {
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container container = frame.getContentPane();
        container.setBackground(Color.WHITE);

        genPanel = new GenPanel();
        factorPanel = new FactorPanel();
        JPanel radixPanel = mkPresentationPane();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.add("generate", genPanel.getPanel());
        tabbedPane.add("crack", factorPanel.getPanel());

        container.add(new JLabel(" (academic - prime numbers are not necessarily optimal for RSA) "), BorderLayout.NORTH);
        container.add(tabbedPane, BorderLayout.CENTER);
        container.add(radixPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static int getRadix() {
        return radix;
    }

    private JPanel mkPresentationPane() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);

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
                genPanel.present();
                factorPanel.present();
            }
        });

        return panel;
    }
}
