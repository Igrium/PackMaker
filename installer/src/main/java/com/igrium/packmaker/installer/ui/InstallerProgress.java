package com.igrium.packmaker.installer.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalExclusionType;

import javax.swing.border.EmptyBorder;

import com.igrium.packmaker.installer.util.ProgressHandle;

import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import java.awt.Window.Type;

public class InstallerProgress implements ProgressHandle {

    private JFrame frame;
    private JLabel progressLabel;
    private JProgressBar progressBar;

    /**
     * Create the application.
     */
    public InstallerProgress() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Installing...");
        frame.setResizable(false);
        frame.setType(Type.POPUP);
        frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        frame.setBounds(100, 100, 300, 96);
        
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        progressLabel = new JLabel("Installing Modpack");
        progressLabel.setAlignmentX(0.5f);
        panel.add(progressLabel);
        
        progressBar = new JProgressBar();
        progressBar.setMaximum(1);
        panel.add(progressBar);
    }

    @Override
    public void log(String message) {
        SwingUtilities.invokeLater(() -> progressLabel.setText(message));
    }

    @Override
    public void updateProgress(int step, int numSteps) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setMaximum(numSteps);
            progressBar.setValue(numSteps);
        });
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
    
}
