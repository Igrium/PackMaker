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
import com.igrium.packmaker.util.EventDispatcher;

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

    public static interface ExportInstallerEvent {
        public void export(ModpackProvider provider, String title);
    }

    public static enum ModpackSource { Modrinth, File }

    // private final List<Consumer<? super ModpackProvider>> refreshListeners = new LinkedList<>();
    private final EventDispatcher<Consumer<ModpackProvider>> refreshEvent = EventDispatcher.createSimple();

    private final EventDispatcher<ExportInstallerEvent> exportInstallerEvent = EventDispatcher.createArrayBacked(
        listeners -> (provider, title) -> {
            for (var l : listeners) {
                l.export(provider, title);
            }
        }
    );

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
            refresh();
        }
    }

    @FXML
    public void browseModrinth() {
        ModrinthBrowser.open(version -> {
            modrinthVersionField.setText(version);
            refresh();
        });
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

    public void export() {
        exportInstallerEvent.invoker().export(parsePackInfo(), nameLabel.getText());
    }

    public EventDispatcher<Consumer<ModpackProvider>> getRefreshEvent() {
        return refreshEvent;
    }

    public EventDispatcher<ExportInstallerEvent> getExportInstallerEvent() {
        return exportInstallerEvent;
    }

    @FXML
    public void refresh() {
        ModpackProvider provider = parsePackInfo();
        refreshEvent.invoker().accept(provider);
    }
}
