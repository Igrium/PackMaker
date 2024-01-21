package com.igrium.packmaker.ui;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

import com.igrium.packmaker.App;
import com.igrium.packmaker.App.LoadedPack;
import com.igrium.packmaker.common.modrinth.ModrinthWebAPI;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.ModrinthProjectVersion;
import com.igrium.packmaker.common.pack.FilePackProvider;
import com.igrium.packmaker.common.pack.ModpackProvider;
import com.igrium.packmaker.common.pack.ModrinthPackProvider;
import com.igrium.packmaker.util.EventDispatcher;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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
    private Button showInBrowserButton;

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

    private String cachedModrinthId;
    
    public void loadPackInfo(LoadedPack pack) {
        nameLabel.setText(pack != null ? pack.pack().getIndex().getName() : "");
        mcVerLabel.setText(pack != null ? pack.pack().getIndex().getDependencies().getMinecraft() : "");
        fabricVerLabel.setText(pack != null ? pack.pack().getIndex().getDependencies().getFabricLoader() : "");
        exportButton.setDisable(pack == null);

        if (pack != null && pack.provider() instanceof ModrinthPackProvider modrinth) {
            cachedModrinthId = modrinth.getVersionId();
            showInBrowserButton.setDisable(false);
        } else {
            cachedModrinthId = null;
            showInBrowserButton.setDisable(true);
        }
    }

    @FXML
    public void showInBrowser() {
        if (cachedModrinthId == null) return;

        CompletableFuture.supplyAsync(() -> {
            ModrinthProjectVersion version;
            try {
                version = ModrinthWebAPI.getGlobalInstance().getProjectVersion(cachedModrinthId);
            } catch (IOException | InterruptedException e) {
                throw new CompletionException(e);
            }
            return String.format("https://modrinth.com/project/%s/version/%s", version.projectId, version.id);
        }).whenCompleteAsync((url, e) -> {
            if (e instanceof CompletionException) {
                e = e.getCause();
            }

            if (e != null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error opening browser.");
                alert.setHeaderText("Error opening browser.");
                alert.setContentText("See console for details.");
                return;
            }

            App.getInstance().getHostServices().showDocument(url);

        }, Platform::runLater);
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
