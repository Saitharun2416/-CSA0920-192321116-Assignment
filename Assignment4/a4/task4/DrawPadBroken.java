package a4.task4;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Task 4.1 / 4.2 - the BUGGY program. It compiles and runs but nothing happens, because the method
 * is spelled "mouseclicked" (lower-case c). It is a NEW unrelated method, not an override of
 * MouseAdapter.mouseClicked, so Java never calls it. The label can only change inside that method.
 * Close this window with the OS close button's Ctrl+C in the IntelliJ console (no window listener).
 */
public class DrawPadBroken extends Frame {
    int count = 0; Label status = new Label("Clicks: 0");
    public DrawPadBroken() {
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            public void mouseclicked(MouseEvent e) {      // BUG: wrong name
                count++;
                Graphics g = getGraphics(); g.fillOval(e.getX(), e.getY(), 6, 6);
            }
        });
        setSize(400, 300); setVisible(true);
    }
    public static void main(String[] args) { new DrawPadBroken(); }
}
