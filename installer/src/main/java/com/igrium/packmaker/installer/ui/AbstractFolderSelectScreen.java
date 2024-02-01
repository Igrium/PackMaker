package com.igrium.packmaker.installer.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JPanel;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Font;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JSeparator;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.FlowLayout;

public abstract class AbstractFolderSelectScreen extends AbstractInstallerScreen implements Screen {
	private JTextField textField;
    private JButton nextButton;
    private JPanel root;

    private JLabel titleLabel;
    private JLabel descriptionLabel;

    protected abstract String getDefaultTitle();
    protected abstract String getDefaultDescription();

    protected abstract void onSubmit(File folder);

    protected abstract void goBack();


    public JTextField getTextField() {
        return textField;
    }

    public JPanel getRoot() {
        return root;
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
        
        titleLabel = new JLabel(getDefaultTitle());
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));
        topPanel.add(titleLabel);
        
        descriptionLabel = new JLabel(getDefaultDescription());
        topPanel.add(descriptionLabel);
        
        JSeparator separator = new JSeparator();
        topPanel.add(separator);
        
        Component rigidArea = Box.createRigidArea(new Dimension(0, 20));
        topPanel.add(rigidArea);
        
        JPanel formPanel = new JPanel();
        root.add(formPanel, BorderLayout.CENTER);
        GridBagLayout gbl_formPanel = new GridBagLayout();
        gbl_formPanel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_formPanel.rowHeights = new int[]{0, 0};
        gbl_formPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_formPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        formPanel.setLayout(gbl_formPanel);
        
        JLabel selectFolderLabel = new JLabel("Select Folder\r\n");
        GridBagConstraints gbc_selectFolderLabel = new GridBagConstraints();
        gbc_selectFolderLabel.insets = new Insets(0, 0, 0, 5);
        gbc_selectFolderLabel.anchor = GridBagConstraints.EAST;
        gbc_selectFolderLabel.gridx = 0;
        gbc_selectFolderLabel.gridy = 0;
        formPanel.add(selectFolderLabel, gbc_selectFolderLabel);
        
        textField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 0, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 1;
        gbc_textField.gridy = 0;
        formPanel.add(textField, gbc_textField);
        textField.setColumns(10);

        textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onTextUpdate();
            }
            
        });
        
        JButton browseButton = new JButton("Browse");
        GridBagConstraints gbc_browseButton = new GridBagConstraints();
        gbc_browseButton.gridx = 2;
        gbc_browseButton.gridy = 0;
        formPanel.add(browseButton, gbc_browseButton);
        browseButton.addActionListener(a -> browse());
        
        JPanel bottomPanel = new JPanel();
        root.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        
        JButton prevButton = new JButton("Previous");
        bottomPanel.add(prevButton);
        
        nextButton = new JButton("Next");
        nextButton.setEnabled(false);
        bottomPanel.add(nextButton);

        nextButton.addActionListener(a -> trySubmit(getFolder()));
        prevButton.addActionListener(a -> goBack());

        return root;
    }

    protected void onTextUpdate() {
        nextButton.setEnabled(!textField.getText().isBlank());
    }

    protected void trySubmit(File folder) {
        if (!folder.isDirectory()) {
            JOptionPane.showMessageDialog(root, "Please select a valid directory");
            return;
        }
        onSubmit(folder);
    }

    protected void browse() {
        JFileChooser fc = new JFileChooser(textField.getText());
        fc.setDialogTitle(getDefaultTitle());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(root);
        if (fc.getSelectedFile() != null) {
            setFolder(fc.getSelectedFile());
        }
    }

    public File getFolder() {
        return new File(textField.getText());
    }

    public void setFolder(File folder) {
        textField.setText(folder.getAbsolutePath());
    }
}
