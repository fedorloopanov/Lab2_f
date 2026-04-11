package app;

import app.ui.MissionDashboardFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MissionDashboardFrame().setVisible(true));
    }
}
