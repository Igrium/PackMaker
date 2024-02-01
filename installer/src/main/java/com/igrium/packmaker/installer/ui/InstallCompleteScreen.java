package com.igrium.packmaker.installer.ui;

import java.awt.Component;

import javax.swing.JPanel;

import com.igrium.packmaker.installer.InstallerUI;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.border.EmptyBorder;
import javax.swing.JSeparator;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.FlowLayout;

public class InstallCompleteScreen extends AbstractInstallerScreen implements Screen {

    private final InstallerUI ui;

    private JPanel root;
    private JLabel descriptionLabel;
    private JLabel titleLabel;
    
    public InstallCompleteScreen(InstallerUI ui) {
        this.ui = ui;
    }

    public InstallerUI getUi() {
        return ui;
    }

    public JPanel getRoot() {
        return root;
    }

    public JLabel getDescriptionLabel() {
        return descriptionLabel;
    }

    @Override
    protected void updateTitle(String titleText) {
        titleLabel.setText(titleText);
    }

    @Override
    protected void updateDescription(String descriptionText) {
        descriptionLabel.setText(descriptionText);
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
        
        titleLabel = new JLabel("Installation Complete!");
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));
        topPanel.add(titleLabel);
        
        JSeparator separator = new JSeparator();
        topPanel.add(separator);
        
        JPanel centerPanel = new JPanel();
        root.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        Component rigidArea = Box.createRigidArea(new Dimension(0, 20));
        centerPanel.add(rigidArea);
        
        descriptionLabel = new JLabel();
        centerPanel.add(descriptionLabel);
        
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
