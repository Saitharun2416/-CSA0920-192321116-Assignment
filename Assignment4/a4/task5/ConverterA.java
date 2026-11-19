package a4.task5;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.*;

/** Style (a): the frame itself implements ActionListener. Enter key + dialog added (Task 5.2). */
public class ConverterA extends JFrame implements ActionListener {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");
    private final JButton convert = new JButton("Convert");

    public ConverterA() {
        super("Converter (a) frame implements ActionListener");
        setLayout(new FlowLayout());
        add(new JLabel("INR")); add(amount); add(target); add(convert); add(result);
        convert.addActionListener(this);
        amount.addActionListener(this);                    // Enter key  (+1 line)
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { dispose(); System.exit(0); } });
        pack();
    }
    @Override public void actionPerformed(ActionEvent e) {   // one method serves every source
        try {
            String r = ConverterLogic.convert(amount.getText(), (String) target.getSelectedItem());
            result.setText(r);
            JOptionPane.showMessageDialog(this, r);          // dialog (+1 line)
        } catch (NumberFormatException ex) { result.setText("Invalid amount"); JOptionPane.showMessageDialog(this, "Invalid amount"); }
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ConverterA().setVisible(true)); }
}
