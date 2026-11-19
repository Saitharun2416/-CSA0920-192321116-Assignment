package a4.task3;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Task 3 - Library search screen.
 * Frame                    BorderLayout
 *  NORTH  panel            BorderLayout
 *     NORTH   title label
 *     CENTER  search row   FlowLayout   (keeps natural size)
 *  CENTER scroll pane      (results; the only region that grows)
 *  SOUTH  panel            BorderLayout
 *     CENTER  button row   FlowLayout   (Prev / Next / Reserve keep natural size)
 *     SOUTH   status label
 */
public class LibrarySearch extends JFrame {
    private final JTextField query = new JTextField(20);
    private final JComboBox<String> category = new JComboBox<>(new String[]{"All", "Java", "Databases", "Networks"});
    private final JTextArea results = new JTextArea();
    private final JLabel status = new JLabel("Type a query and press Search");
    private final List<String[]> books = List.of(
            new String[]{"Java", "Core Java Volume I"}, new String[]{"Java", "Effective Java"},
            new String[]{"Databases", "Database System Concepts"}, new String[]{"Networks", "Computer Networks"},
            new String[]{"Java", "Java Concurrency in Practice"}, new String[]{"Databases", "SQL Antipatterns"});
    private int page = 0;

    public LibrarySearch() {
        super("Campus Library Search");
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Campus Library", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton search = new JButton("Search");
        searchRow.add(new JLabel("Title:")); searchRow.add(query); searchRow.add(category); searchRow.add(search);
        JPanel north = new JPanel(new BorderLayout());
        north.add(title, BorderLayout.NORTH); north.add(searchRow, BorderLayout.CENTER);

        results.setEditable(false);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prev = new JButton("Prev"), next = new JButton("Next"), reserve = new JButton("Reserve");
        buttons.add(prev); buttons.add(next); buttons.add(reserve);
        JPanel south = new JPanel(new BorderLayout());
        south.add(buttons, BorderLayout.CENTER); south.add(status, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(results), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        search.addActionListener(e -> { page = 0; doSearch(); });
        prev.addActionListener(e -> { if (page > 0) page--; doSearch(); });
        next.addActionListener(e -> { page++; doSearch(); });
        reserve.addActionListener(e -> status.setText("Reserved (demo)"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 380);
        setLocationRelativeTo(null);
    }

    private void doSearch() {
        String q = query.getText().trim().toLowerCase();
        String cat = (String) category.getSelectedItem();
        StringBuilder sb = new StringBuilder();
        int shown = 0;
        for (String[] b : books) {
            if ((cat.equals("All") || b[0].equals(cat)) && b[1].toLowerCase().contains(q)) { sb.append(b[1]).append("  [").append(b[0]).append("]\n"); shown++; }
        }
        results.setText(sb.toString());
        status.setText(shown + " result(s) - page " + (page + 1) + " | resize the window: only the results area grows");
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new LibrarySearch().setVisible(true)); }
}
