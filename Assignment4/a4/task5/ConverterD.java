package a4.task5;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.*;

/** Style (d): lambda - shortest; also shows the method-reference alternative (this::convert). */
public class ConverterD extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    public ConverterD() {
        super("Converter (d) lambda");
        setLayout(new FlowLayout());
        JButton convert = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target); add(convert); add(result);
        ActionListener l = e -> convertNow();               // or:  this::convertNow  (method reference)
        convert.addActionListener(l);
        amount.addActionListener(l);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { dispose(); System.exit(0); } });
        pack();
    }
    private void convertNow() {
        try {
            String r = ConverterLogic.convert(amount.getText(), (String) target.getSelectedItem());
            result.setText(r); JOptionPane.showMessageDialog(this, r);
        } catch (NumberFormatException ex) { result.setText("Invalid amount"); JOptionPane.showMessageDialog(this, "Invalid amount"); }
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ConverterD().setVisible(true)); }
}
