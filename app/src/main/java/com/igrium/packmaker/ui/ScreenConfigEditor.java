package com.igrium.packmaker.ui;

import com.igrium.packmaker.common.InstallerConfig.ScreenConfig;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public final class ScreenConfigEditor {

    @FXML
    private Label headerLabel;

    public Label getHeaderLabel() {
        return headerLabel;
    }

    public void setHeaderLabel(String label) {
        this.headerLabel.setText(label);
    }

    public StringProperty headerLabelProperty() {
        return headerLabel.textProperty();
    }

    @FXML
    private TextField headerTextField;

    public TextField getHeaderTextField() {
        return headerTextField;
    }

    public StringProperty headerProperty() {
        return headerTextField.textProperty();
    }

    @FXML
    private TextArea descriptionTextArea;

    public TextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }
    
    public StringProperty descriptionProperty() {
        return descriptionTextArea.textProperty();
    }

    private final StringProperty defaultHeaderProperty = new SimpleStringProperty();

    public String getDefaultHeader() {
        return defaultHeaderProperty.get();
    }

    public void setDefaultHeader(String value) {
        defaultHeaderProperty.set(value);
    }

    public StringProperty defaultHeaderProperty() {
        return defaultHeaderProperty;
    }

    private final StringProperty defaultDescriptionProperty = new SimpleStringProperty();

    public String getDefaultDescription() {
        return defaultDescriptionProperty.get();
    }

    public void setDefaultDescription(String value) {
        defaultDescriptionProperty.set(value);
    }

    public StringProperty defaultDescriptionProperty() {
        return defaultDescriptionProperty;
    }

    @FXML
    public void reset() {
        headerTextField.setText(getDefaultHeader());
        descriptionTextArea.setText(getDefaultDescription());
    }

    /**
     * Apply this config to a screen config object.
     * @param dest Object to apply to.
     * @return <code>dest</code>
     */
    public ScreenConfig applyConfig(ScreenConfig dest) {
        dest.setHeader(headerTextField.getText());
        dest.setDescription(descriptionTextArea.getText());
        return dest;
    }

}
