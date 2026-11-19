package a4.task5;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.*;

/** Style (c): anonymous inner class - defined where used; must be repeated (or stored in a variable) per trigger. */
public class ConverterC extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    public ConverterC() {
        super("Converter (c) anonymous inner class");
        setLayout(new FlowLayout());
        JButton convert = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target); add(convert); add(result);
        ActionListener l = new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                try {
                    String r = ConverterLogic.convert(amount.getText(), (String) target.getSelectedItem());
                    result.setText(r); JOptionPane.showMessageDialog(ConverterC.this, r);
                } catch (NumberFormatException ex) { result.setText("Invalid amount"); JOptionPane.showMessageDialog(ConverterC.this, "Invalid amount"); }
            }
        };
        convert.addActionListener(l);
        amount.addActionListener(l);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { dispose(); System.exit(0); } });
        pack();
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ConverterC().setVisible(true)); }
}
