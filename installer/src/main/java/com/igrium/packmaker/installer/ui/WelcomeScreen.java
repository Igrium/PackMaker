package com.igrium.packmaker.installer.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.igrium.packmaker.installer.InstallerApp;
import com.igrium.packmaker.installer.InstallerUI;

public class WelcomeScreen extends AbstractInstallerScreen implements Screen {
    private final InstallerUI ui;

    private JLabel titleLabel;
    private JLabel descriptionLabel;

    public WelcomeScreen(InstallerUI ui) {
        this.ui = ui;
    }

    public InstallerUI getUi() {
        return ui;
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
        JPanel root = new JPanel();
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        
        titleLabel = new JLabel("[title]");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 21));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(titleLabel);
        
        Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
        root.add(rigidArea);
        
        descriptionLabel = new JLabel("description");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(descriptionLabel);
        
        Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
        root.add(rigidArea_1);
        
        JButton startButton = new JButton("Begin");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(startButton);

        startButton.addActionListener(a -> ui.openLauncherFolderSelect());
        setParam(InstallerApp.getInstance().getConfig().getModpackName());
        return root;
    }
}
