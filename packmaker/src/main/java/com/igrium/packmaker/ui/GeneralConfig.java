package com.igrium.packmaker.ui;

import com.igrium.packmaker.common.InstallerConfig;
import com.igrium.packmaker.common.pack.ModpackProvider;
import com.igrium.packmaker.common.pack.ModrinthPackProvider;
import com.igrium.packmaker.exporter.ExportConfig;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GeneralConfig {

    private String defaultTwinFolderWarning = "In order to avoid conflicts with other modpacks, it is recommended to store your game files in a dedicated location.";

    public String getDefaultTwinFolderWarning() {
        return defaultTwinFolderWarning;
    }

    public void setDefaultTwinFolderWarning(String defaultTwinFolderWarning) {
        this.defaultTwinFolderWarning = defaultTwinFolderWarning;
    }

    @FXML
    private CheckBox twinFolderWarningCheck;

    public CheckBox getTwinFolderWarningCheck() {
        return twinFolderWarningCheck;
    }

    @FXML
    private TextArea twinFolderWarningText;

    public TextArea getTwinFolderWarningText() {
        return twinFolderWarningText;
    }

    @FXML
    private CheckBox customProfileNameCheck;

    public CheckBox getCustomProfileNameCheck() {
        return customProfileNameCheck;
    }

    @FXML
    private TextField customProfileName;

    public TextField getCustomProfileName() {
        return customProfileName;
    }

    @FXML
    private CheckBox bundleCheckBox;

    public CheckBox getBundleCheckBox() {
        return bundleCheckBox;
    }

    @FXML
    private void initialize() {
        twinFolderWarningText.disableProperty().bind(twinFolderWarningCheck.selectedProperty().not());
        twinFolderWarningText.setText(defaultTwinFolderWarning);
        
        customProfileName.disableProperty().bind(customProfileNameCheck.selectedProperty().not());
    }

    @FXML
    public void reset() {
        twinFolderWarningText.setText(defaultTwinFolderWarning);
        twinFolderWarningCheck.setSelected(true);
        customProfileName.setText("");
    }

    public InstallerConfig applyConfig(InstallerConfig config) {
        if (twinFolderWarningCheck.isSelected()) {
            config.setTwinFolderWarning(twinFolderWarningText.getText().strip());
        } else {
            config.setTwinFolderWarning(null);
        }

        if (customProfileNameCheck.isSelected() && !customProfileName.getText().isBlank()) {
            config.setCustomProfileName(customProfileName.getText().strip());
        } else {
            config.setCustomProfileName(null);
        }
        return config;
    }

    public ExportConfig applyConfig(ExportConfig config) {
        return config.setBundlePack(bundleCheckBox.isSelected());
    }

    public void onUpdateModpackSource(ModpackProvider provider) {
        if (provider instanceof ModrinthPackProvider) {
            bundleCheckBox.setDisable(false);
            bundleCheckBox.setSelected(false);
        } else {
            bundleCheckBox.setDisable(true);
            bundleCheckBox.setSelected(true);
        }
    }
}
