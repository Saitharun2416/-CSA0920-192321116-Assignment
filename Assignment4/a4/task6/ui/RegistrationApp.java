package a4.task6.ui;

import a4.task6.dao.DuplicateRegistrationException;
import a4.task6.dao.InMemoryRegistrationDao;
import a4.task6.dao.JdbcRegistrationDao;
import a4.task6.dao.RegistrationDao;
import a4.task6.model.Registration;
import a4.task6.service.RegistrationService;
import a4.task6.service.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * Task 6 - Campus Event Registration (UI layer). Every database call runs in a SwingWorker,
 * so the event-dispatch thread (EDT) never blocks.
 *
 * DB settings come from environment variables DB_URL, DB_USER, DB_PASSWORD
 * (default jdbc:mysql://localhost:3306/campus_events, root, empty). If MySQL is unreachable the
 * app falls back to an in-memory store so the GUI can still be demonstrated.
 */
public class RegistrationApp extends JFrame {
    private final RegistrationService service;
    private final JTextField studentId = new JTextField(10), studentName = new JTextField(14), email = new JTextField(16);
    private final JComboBox<String> eventBox = new JComboBox<>();
    private final JComboBox<String> filterBox = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Student ID", "Name", "Email", "Event"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JLabel status = new JLabel("Ready"), pageLabel = new JLabel("Page 1");
    private final JButton register = new JButton("Register"), prev = new JButton("Prev"), next = new JButton("Next");
    private int page = 0, pages = 1;

    public RegistrationApp(RegistrationService service, String backendName) {
        super("Campus Event Registration  [" + backendName + "]");
        this.service = service;

        // ---- menu bar ----
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File"), view = new JMenu("View"), help = new JMenu("Help");
        JMenuItem export = new JMenuItem("Export CSV..."), exit = new JMenuItem("Exit");
        JMenuItem refresh = new JMenuItem("Refresh"), about = new JMenuItem("About");
        file.add(export); file.addSeparator(); file.add(exit); view.add(refresh); help.add(about);
        bar.add(file); bar.add(view); bar.add(help);
        setJMenuBar(bar);

        // ---- form (NORTH) ----
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("New registration"));
        form.add(new JLabel("ID:")); form.add(studentId);
        form.add(new JLabel("Name:")); form.add(studentName);
        form.add(new JLabel("Email:")); form.add(email);
        form.add(new JLabel("Event:")); form.add(eventBox);
        form.add(register);

        // ---- bottom bar (SOUTH): filter, paging, status ----
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nav.add(new JLabel("Filter by event:")); nav.add(filterBox); nav.add(prev); nav.add(pageLabel); nav.add(next);
        JPanel south = new JPanel(new BorderLayout());
        south.add(nav, BorderLayout.CENTER); south.add(status, BorderLayout.SOUTH);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        // ---- handlers ----
        register.addActionListener(e -> onRegister());
        filterBox.addActionListener(e -> { page = 0; loadPage(); });
        prev.addActionListener(e -> { if (page > 0) { page--; loadPage(); } });
        next.addActionListener(e -> { if (page < pages - 1) { page++; loadPage(); } });
        refresh.addActionListener(e -> loadPage());
        export.addActionListener(e -> onExport());
        exit.addActionListener(e -> System.exit(0));
        about.addActionListener(e -> JOptionPane.showMessageDialog(this, "Campus Event Registration\nSwing + JDBC, layered design"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 480);
        setLocationRelativeTo(null);
        loadEvents();
    }

    private void loadEvents() {
        new SwingWorker<List<String>, Void>() {
            @Override protected List<String> doInBackground() throws Exception { return service.events(); }
            @Override protected void done() {
                try {
                    List<String> ev = get();
                    for (String s : ev) eventBox.addItem(s);
                    filterBox.addItem("All");
                    for (String s : ev) filterBox.addItem(s);
                    loadPage();
                } catch (Exception ex) { fail(ex); }
            }
        }.execute();
    }

    /** Pseudocode (Filter): event = filterBox.selected; worker: rows = service.page(event, page); pages = pageCount; EDT: fill table. */
    private void loadPage() {
        String event = (String) filterBox.getSelectedItem();
        status.setText("Loading...");
        new SwingWorker<Object[], Void>() {
            @Override protected Object[] doInBackground() throws Exception {
                return new Object[]{service.page(event, page), service.pageCount(event)};
            }
            @Override @SuppressWarnings("unchecked") protected void done() {
                try {
                    Object[] r = get();
                    model.setRowCount(0);
                    for (Registration x : (List<Registration>) r[0])
                        model.addRow(new Object[]{x.studentId(), x.studentName(), x.email(), x.eventName()});
                    pages = (Integer) r[1];
                    if (page >= pages) page = pages - 1;
                    pageLabel.setText("Page " + (page + 1) + " of " + pages);
                    status.setText("Showing " + model.getRowCount() + " row(s)");
                } catch (Exception ex) { fail(ex); }
            }
        }.execute();
    }

    /** Pseudocode (Register): read fields -> worker: service.register(...) -> success: clear form + reload; else show error dialog. */
    private void onRegister() {
        String id = studentId.getText(), name = studentName.getText(), mail = email.getText();
        String event = (String) eventBox.getSelectedItem();
        register.setEnabled(false); status.setText("Saving...");
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception { service.register(id, name, mail, event); return null; }
            @Override protected void done() {
                register.setEnabled(true);
                try {
                    get();
                    studentId.setText(""); studentName.setText(""); email.setText("");
                    status.setText("Registered " + id.trim() + " for " + event);
                    loadPage();
                } catch (Exception ex) {
                    Throwable t = ex.getCause() != null ? ex.getCause() : ex;
                    if (t instanceof ValidationException) {
                        JOptionPane.showMessageDialog(RegistrationApp.this, t.getMessage(), "Invalid input", JOptionPane.WARNING_MESSAGE);
                    } else if (t instanceof DuplicateRegistrationException) {
                        JOptionPane.showMessageDialog(RegistrationApp.this, t.getMessage(), "Duplicate registration", JOptionPane.ERROR_MESSAGE);
                    } else fail(ex);
                    status.setText("Not saved");
                }
            }
        }.execute();
    }

    private void onExport() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("registrations.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File out = fc.getSelectedFile();
        String event = (String) filterBox.getSelectedItem();
        new SwingWorker<Integer, Void>() {
            @Override protected Integer doInBackground() throws Exception {
                List<Registration> rows = service.all(event);
                try (PrintWriter w = new PrintWriter(out, "UTF-8")) {
                    w.println("student_id,student_name,email,event");
                    for (Registration r : rows)
                        w.println(r.studentId() + ",\"" + r.studentName().replace("\"", "\"\"") + "\"," + r.email() + ",\"" + r.eventName() + "\"");
                }
                return rows.size();
            }
            @Override protected void done() {
                try { status.setText("Exported " + get() + " row(s) to " + out.getName()); } catch (Exception ex) { fail(ex); }
            }
        }.execute();
    }

    private void fail(Exception ex) {
        Throwable t = ex.getCause() != null ? ex.getCause() : ex;
        status.setText("Error: " + t.getMessage());
        JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/campus_events");
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String pwd = System.getenv().getOrDefault("DB_PASSWORD", "");
        RegistrationDao dao; String backend;
        try {
            DriverManager_setTimeout();
            JdbcRegistrationDao jdbc = new JdbcRegistrationDao(url, user, pwd);
            jdbc.ping();
            dao = jdbc; backend = "MySQL";
        } catch (Exception e) {
            System.out.println("MySQL not available (" + e.getMessage() + ") -> using in-memory store");
            dao = new InMemoryRegistrationDao(); backend = "in-memory demo";
        }
        RegistrationService service = new RegistrationService(dao);
        String finalBackend = backend;
        SwingUtilities.invokeLater(() -> new RegistrationApp(service, finalBackend).setVisible(true));
    }

    private static void DriverManager_setTimeout() { java.sql.DriverManager.setLoginTimeout(3); }
}
