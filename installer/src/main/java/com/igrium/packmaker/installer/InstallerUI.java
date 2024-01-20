package com.igrium.packmaker.installer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import com.igrium.packmaker.common.modrinth.ModrinthWebAPI;
import com.igrium.packmaker.installer.fabric.FabricInstaller;
import com.igrium.packmaker.installer.modpack.ModrinthPackProvider;
import com.igrium.packmaker.installer.ui.InstallerProgress;
public class InstallerUI {

    private JFrame frame;
    private JTextField fieldMinecraftDirectory;
    private JTextField fieldVersionId;

    /**
     * Launch the application.
     * @throws UnsupportedLookAndFeelException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws ClassNotFoundException 
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        InstallerUI window = new InstallerUI();
        window.frame.setVisible(true);
    }

    /**
     * Create the application.
     */
    public InstallerUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        frame.getContentPane().add(mainPanel);
        
        JPanel configPanel = new JPanel();
        mainPanel.add(configPanel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        configPanel.setLayout(gbl_panel);
        
        JLabel lblMinecraftDirectory = new JLabel("Minecraft Directory");
        GridBagConstraints gbc_lblMinecraftDirectory = new GridBagConstraints();
        gbc_lblMinecraftDirectory.insets = new Insets(0, 0, 5, 5);
        gbc_lblMinecraftDirectory.anchor = GridBagConstraints.EAST;
        gbc_lblMinecraftDirectory.gridx = 0;
        gbc_lblMinecraftDirectory.gridy = 0;
        configPanel.add(lblMinecraftDirectory, gbc_lblMinecraftDirectory);
        
        fieldMinecraftDirectory = new JTextField();
        GridBagConstraints gbc_fieldMinecraftDirectory = new GridBagConstraints();
        gbc_fieldMinecraftDirectory.insets = new Insets(0, 0, 5, 5);
        gbc_fieldMinecraftDirectory.fill = GridBagConstraints.HORIZONTAL;
        gbc_fieldMinecraftDirectory.gridx = 1;
        gbc_fieldMinecraftDirectory.gridy = 0;
        configPanel.add(fieldMinecraftDirectory, gbc_fieldMinecraftDirectory);
        fieldMinecraftDirectory.setColumns(10);
        
        JButton btnBrowse = new JButton("Browse");
        GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
        gbc_btnBrowse.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
        gbc_btnBrowse.gridx = 2;
        gbc_btnBrowse.gridy = 0;
        configPanel.add(btnBrowse, gbc_btnBrowse);
        btnBrowse.addActionListener(a -> browseMinecraftDirectory());
        
        JLabel lblModpackId = new JLabel("Modpack Version ID");
        GridBagConstraints gbc_lblModpackId = new GridBagConstraints();
        gbc_lblModpackId.anchor = GridBagConstraints.EAST;
        gbc_lblModpackId.insets = new Insets(0, 0, 0, 5);
        gbc_lblModpackId.gridx = 0;
        gbc_lblModpackId.gridy = 1;
        configPanel.add(lblModpackId, gbc_lblModpackId);
        
        fieldVersionId = new JTextField();
        GridBagConstraints gbc_fieldVersionId = new GridBagConstraints();
        gbc_fieldVersionId.insets = new Insets(0, 0, 0, 5);
        gbc_fieldVersionId.fill = GridBagConstraints.HORIZONTAL;
        gbc_fieldVersionId.gridx = 1;
        gbc_fieldVersionId.gridy = 1;
        configPanel.add(fieldVersionId, gbc_fieldVersionId);
        fieldVersionId.setColumns(10);
        
        JButton btnInstallButton = new JButton("Install");
        btnInstallButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        GridBagConstraints gbc_btnInstallButton = new GridBagConstraints();
        gbc_btnInstallButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnInstallButton.anchor = GridBagConstraints.NORTHWEST;
        gbc_btnInstallButton.gridx = 2;
        gbc_btnInstallButton.gridy = 1;
        configPanel.add(btnInstallButton, gbc_btnInstallButton);
        btnInstallButton.addActionListener(e -> doinstall());
        
    }
    
    private void browseMinecraftDirectory() {
        JFileChooser fc = new JFileChooser(fieldMinecraftDirectory.getText());
        fc.setDialogTitle("Minecraft Installation Directory");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(frame);
        fieldMinecraftDirectory.setText(fc.getSelectedFile().toString());
    }

    public void doinstall() {
        String id = fieldVersionId.getText();
        Path targetPath = Paths.get(fieldMinecraftDirectory.getText());

        InstallerProgress progress = new InstallerProgress();
        progress.setVisible(true);
        Thread thread = new Thread(() -> {
            try {
                Installer.install(progress, targetPath, targetPath, new ModrinthPackProvider(ModrinthWebAPI.getGlobalInstance(), id));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            SwingUtilities.invokeLater(() -> progress.setVisible(false));
        });

        thread.start();
    }

}
