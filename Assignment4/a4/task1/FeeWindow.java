package a4.task1;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Task 1.2 - the window only collects input and shows results. One listener per button. */
public class FeeWindow extends JFrame {
    private final JTextField regNo = new JTextField(15);
    private final JTextField amount = new JTextField(15);
    private final JButton pay = new JButton("Pay");
    private final JButton clear = new JButton("Clear");
    private final JButton receipt = new JButton("Receipt");
    private final JLabel status = new JLabel("Enter details and press Pay");
    private final FeeService service;
    private String lastReceipt = null;

    public FeeWindow(FeeService service) {
        super("Fee Payment");
        this.service = service;
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Register No:")); form.add(regNo);
        form.add(new JLabel("Amount:"));      form.add(amount);
        form.add(pay); form.add(clear);
        form.add(receipt); form.add(status);
        add(form);

        pay.addActionListener(e -> onPay());
        clear.addActionListener(e -> { regNo.setText(""); amount.setText(""); status.setText("Cleared"); });
        receipt.addActionListener(e -> {
            if (lastReceipt == null) JOptionPane.showMessageDialog(this, "No payment made yet", "Receipt", JOptionPane.WARNING_MESSAGE);
            else JOptionPane.showMessageDialog(this, lastReceipt, "Receipt", JOptionPane.INFORMATION_MESSAGE);
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); System.exit(0); }
        });
        pack();
        setLocationRelativeTo(null);
    }

    private void onPay() {
        try { service.validate(regNo.getText(), amount.getText()); }       // fast: fine on the EDT
        catch (FeeValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        pay.setEnabled(false);
        status.setText("Processing payment...");
        new SwingWorker<String, Void>() {                                   // slow work off the EDT
            @Override protected String doInBackground() throws Exception { return service.pay(regNo.getText(), amount.getText()); }
            @Override protected void done() {
                pay.setEnabled(true);
                try { lastReceipt = get(); status.setText("Payment successful"); }
                catch (Exception ex) { status.setText("Payment failed: " + ex.getCause().getMessage()); }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FeeWindow(new FeeService(new InMemoryFeeRepository())).setVisible(true));
    }
}
