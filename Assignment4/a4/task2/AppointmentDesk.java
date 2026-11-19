package a4.task2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Task 2 - Hospital outpatient appointment desk.
 *
 * Event table:
 *  JComboBox department  -> ItemEvent   -> ItemListener   -> refresh doctor list
 *  JButton   Book        -> ActionEvent -> ActionListener -> validate + book (background)
 *  JFrame    close       -> WindowEvent -> WindowListener -> confirm if booking in progress
 *
 * Containment tree: JFrame > JPanel(form, GridLayout) > {JLabel, JTextField, JComboBox x3, JButton};
 *                   JFrame SOUTH > JLabel status.
 */
public class AppointmentDesk extends JFrame {
    record Doctor(String name) { @Override public String toString() { return name; } }

    private final Map<String, List<Doctor>> doctorsByDept = new LinkedHashMap<>();   // loaded once: O(1) lookup
    private final JTextField patientId = new JTextField(12);
    private final JComboBox<String> department = new JComboBox<>();
    private final JComboBox<Doctor> doctor = new JComboBox<>();
    private final JComboBox<String> slot = new JComboBox<>(new String[]{"09:00", "09:30", "10:00", "10:30", "11:00"});
    private final JButton book = new JButton("Book");
    private final JLabel status = new JLabel("Ready");
    private volatile boolean bookingInProgress = false;

    public AppointmentDesk() {
        super("Outpatient Appointment Desk");
        doctorsByDept.put("Cardiology", List.of(new Doctor("Dr. Rao"), new Doctor("Dr. Iyer")));
        doctorsByDept.put("Orthopaedics", List.of(new Doctor("Dr. Khan"), new Doctor("Dr. Mehta"), new Doctor("Dr. Nair")));
        doctorsByDept.put("Paediatrics", List.of(new Doctor("Dr. Das")));
        doctorsByDept.keySet().forEach(department::addItem);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Patient ID:")); form.add(patientId);
        form.add(new JLabel("Department:")); form.add(department);
        form.add(new JLabel("Doctor:"));     form.add(doctor);
        form.add(new JLabel("Time slot:"));  form.add(slot);
        form.add(new JLabel());              form.add(book);
        add(form, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        department.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) refreshDoctors(); });
        book.addActionListener(e -> bookAppointment());
        refreshDoctors();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {                      // logs the window-event order (Task 2.4)
            @Override public void windowOpened(WindowEvent e)      { System.out.println("windowOpened"); }
            @Override public void windowActivated(WindowEvent e)   { System.out.println("windowActivated"); }
            @Override public void windowDeactivated(WindowEvent e) { System.out.println("windowDeactivated"); }
            @Override public void windowIconified(WindowEvent e)   { System.out.println("windowIconified"); }
            @Override public void windowDeiconified(WindowEvent e) { System.out.println("windowDeiconified"); }
            @Override public void windowClosed(WindowEvent e)      { System.out.println("windowClosed"); System.exit(0); }
            @Override public void windowClosing(WindowEvent e) {
                System.out.println("windowClosing");
                if (bookingInProgress) {
                    int r = JOptionPane.showConfirmDialog(AppointmentDesk.this, "A booking is in progress. Close anyway?",
                            "Confirm", JOptionPane.YES_NO_OPTION);
                    if (r != JOptionPane.YES_OPTION) return;
                }
                dispose();
            }
        });
        pack();
        setLocationRelativeTo(null);
    }

    /** Pseudocode (a): ON department change -> clear doctor combo; FOR each doctor in map.get(dept): add. */
    private void refreshDoctors() {
        doctor.removeAllItems();
        for (Doctor d : doctorsByDept.getOrDefault((String) department.getSelectedItem(), List.of())) doctor.addItem(d);
    }

    /** Pseudocode (b): validate id -> set inProgress -> background booking -> show confirmation/error. */
    private void bookAppointment() {
        String id = patientId.getText().trim();
        if (!id.matches("P\\d{4,8}")) { status.setText("Error: Patient ID must look like P1234"); return; }
        if (doctor.getSelectedItem() == null) { status.setText("Error: choose a doctor"); return; }
        bookingInProgress = true; book.setEnabled(false); status.setText("Booking...");
        String text = id + " with " + doctor.getSelectedItem() + " at " + slot.getSelectedItem();
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception { Thread.sleep(2000); return null; }
            @Override protected void done() {
                bookingInProgress = false; book.setEnabled(true);
                status.setText("Confirmed: " + text);
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppointmentDesk().setVisible(true));
    }
}
