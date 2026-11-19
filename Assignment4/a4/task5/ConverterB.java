package a4.task5;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.*;

/** Style (b): named inner class - reusable, can access the frame's private fields. */
public class ConverterB extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    private class ConvertListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            try {
                String r = ConverterLogic.convert(amount.getText(), (String) target.getSelectedItem());
                result.setText(r); JOptionPane.showMessageDialog(ConverterB.this, r);
            } catch (NumberFormatException ex) { result.setText("Invalid amount"); JOptionPane.showMessageDialog(ConverterB.this, "Invalid amount"); }
        }
    }

    public ConverterB() {
        super("Converter (b) named inner class");
        setLayout(new FlowLayout());
        JButton convert = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target); add(convert); add(result);
        ConvertListener l = new ConvertListener();         // ONE object reused for both sources
        convert.addActionListener(l);
        amount.addActionListener(l);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { dispose(); System.exit(0); } });
        pack();
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ConverterB().setVisible(true)); }
}
