package com.igrium.packmaker.ui;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.igrium.packmaker.common.modrinth.ModrinthWebAPI;
import com.igrium.packmaker.common.pack.FilePackProvider;
import com.igrium.packmaker.common.pack.ModpackProvider;
import com.igrium.packmaker.common.pack.ModrinthPackProvider;
import com.igrium.packmaker.mrpack.MrPackIndex;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class SideBar {
    public static enum ModpackSource { Modrinth, File }

    private final List<Consumer<? super ModpackProvider>> refreshListeners = new LinkedList<>();

    @FXML
    private Parent root;

    @FXML
    private ChoiceBox<ModpackSource> modpackSource;

    @FXML
    private HBox modrinthConfig;

    @FXML
    private TextField modrinthVersionField;

    @FXML
    private HBox localFileConfig;

    @FXML
    private TextField localFileField;

    @FXML
    private Label nameLabel;

    @FXML
    private Label mcVerLabel;

    @FXML
    private Label fabricVerLabel;

    @FXML
    private Button exportButton;

    @FXML
    public void initialize() {
        modpackSource.getItems().addAll(ModpackSource.values());
        modpackSource.getSelectionModel().selectedItemProperty().addListener(this::onModpackSourceChanged);
        modpackSource.getSelectionModel().select(ModpackSource.Modrinth);
    }

    public ChoiceBox<ModpackSource> getModpackSource() {
        return modpackSource;
    }

    public TextField getModrinthVersionField() {
        return modrinthVersionField;
    }

    private void onModpackSourceChanged(ObservableValue<? extends ModpackSource> observable, ModpackSource oldValue,
            ModpackSource newValue) {

        modrinthConfig.setDisable(newValue != ModpackSource.Modrinth);
        localFileConfig.setDisable(newValue != ModpackSource.File);
    }
    
    @FXML
    public void browseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open modpack file.");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Modrinth Modpack Files", "*.mrpack")
        );
        File selectedFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (selectedFile != null) {
            localFileField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    public void loadPackInfo(MrPackIndex pack) {
        nameLabel.setText(pack != null ? pack.getName() : "");
        mcVerLabel.setText(pack != null ? pack.getDependencies().getMinecraft() : "");
        fabricVerLabel.setText(pack != null ? pack.getDependencies().getFabricLoader() : "");
        exportButton.setDisable(pack == null);
    }

    public ModpackProvider parsePackInfo() {
        if (modpackSource.getValue() == ModpackSource.Modrinth) {
            return new ModrinthPackProvider(ModrinthWebAPI.getGlobalInstance(), modrinthVersionField.getText());
        } else {
            return new FilePackProvider(new File(localFileField.getText()));
        }
    }

    public void addRefreshListener(Consumer<? super ModpackProvider> listener) {
        refreshListeners.add(listener);
    }

    @FXML
    public void refresh() {
        ModpackProvider provider = parsePackInfo();
        for (var l : refreshListeners) {
            l.accept(provider);
        }
    }
}
