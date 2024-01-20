package com.igrium.packmaker.installer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.igrium.packmaker.installer.InstallerUI;
import com.igrium.packmaker.installer.util.ProgressHandle;

public class InstallingScreen implements Screen, ProgressHandle {
    private final InstallerUI ui;

    private JPanel root;

    private JLabel detailLabel;
    private JProgressBar progressBar;

    public InstallingScreen(InstallerUI ui) {
        this.ui = ui;
    }

    public InstallerUI getUi() {
        return ui;
    }

    public JPanel getRoot() {
        return root;
    }

    public JLabel getDetailLabel() {
        return detailLabel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * @wbp.parser.entryPoint
     */
    public Component initialize() {
        root = new JPanel();
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setLayout(new BorderLayout(0, 0));
        
        JPanel topPanel = new JPanel();
        root.add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Installing");
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));
        topPanel.add(titleLabel);
        
        JSeparator separator = new JSeparator();
        topPanel.add(separator);
        
        Component rigidArea = Box.createRigidArea(new Dimension(0, 20));
        topPanel.add(rigidArea);
        
        detailLabel = new JLabel("Please wait...");
        topPanel.add(detailLabel);
        
        progressBar = new JProgressBar();
        progressBar.setAlignmentY(Component.TOP_ALIGNMENT);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(progressBar);
        
        JPanel bottomPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) bottomPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        root.add(bottomPanel, BorderLayout.SOUTH);
        
        JButton cancelButton = new JButton("Cancel");
        bottomPanel.add(cancelButton);
        return root;
    }

    @Override
    public void log(String message) {
        SwingUtilities.invokeLater(() -> detailLabel.setText(message));
    }

    @Override
    public void updateProgress(int step, int numSteps) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setMaximum(numSteps);
            progressBar.setValue(step);
        });
    }
}
