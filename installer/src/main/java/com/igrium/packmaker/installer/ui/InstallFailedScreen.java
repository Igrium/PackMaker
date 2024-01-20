package com.igrium.packmaker.installer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import com.igrium.packmaker.installer.InstallerUI;
import java.awt.Color;
import javax.swing.JTextArea;

public class InstallFailedScreen implements Screen {

    private final InstallerUI ui;

    private JPanel root;
    private JTextArea detailField;

    public InstallFailedScreen(InstallerUI ui) {
        this.ui = ui;
    }

    public JPanel getRoot() {
        return root;
    }

    public JTextArea getDetailField() {
        return detailField;
    }

    public void setException(Throwable e) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try(PrintStream ps = new PrintStream(bos)) {
            e.printStackTrace(ps);
        }
        detailField.setText(bos.toString());
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
        
        JLabel titleLabel = new JLabel("Installation Failed.");
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));
        topPanel.add(titleLabel);
        
        JSeparator separator = new JSeparator();
        topPanel.add(separator);
        
        JPanel centerPanel = new JPanel();
        root.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        Component rigidArea = Box.createRigidArea(new Dimension(0, 20));
        centerPanel.add(rigidArea);
        
        detailField = new JTextArea();
        detailField.setForeground(new Color(128, 0, 0));
        detailField.setEditable(false);
        detailField.setLineWrap(true);
        detailField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailField.setText("[detail text]");
        detailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(detailField);
        
        JPanel bottomPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) bottomPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        root.add(bottomPanel, BorderLayout.SOUTH);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(a -> close());
        bottomPanel.add(closeButton);

        return root;
    }

    private void close() {
        ui.getFrame().dispose();
    }
}
