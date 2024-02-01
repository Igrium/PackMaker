package com.igrium.packmaker.ui;

import com.igrium.packmaker.common.InstallerConfig;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;

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
    private void initialize() {
        twinFolderWarningText.disableProperty().bind(twinFolderWarningCheck.selectedProperty().not());
        twinFolderWarningText.setText(defaultTwinFolderWarning);
    }

    @FXML
    public void reset() {
        twinFolderWarningText.setText(defaultTwinFolderWarning);
        twinFolderWarningCheck.setSelected(true);
    }

    public InstallerConfig applyConfig(InstallerConfig config) {
        if (twinFolderWarningCheck.isSelected()) {
            config.setTwinFolderWarning(twinFolderWarningText.getText().strip());
        } else {
            config.setTwinFolderWarning(null);
        }
        return config;
    }
}
