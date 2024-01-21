package com.igrium.packmaker.ui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

import com.igrium.packmaker.common.modrinth.ModrinthWebAPI;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.ModrinthProject;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.ModrinthProjectType;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.ModrinthProjectVersion;
import com.igrium.packmaker.common.util.HttpException;
import com.igrium.packmaker.util.EventDispatcher;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ModrinthBrowser {
    
    private final EventDispatcher<Runnable> cancelEvent = EventDispatcher.createNoArg();
    private final EventDispatcher<Consumer<String>> selectEvent = EventDispatcher.createSimple();

    @FXML
    private TextField urlTextField;

    @FXML
    private Button openButton;

    @FXML
    private Button selectButton;
    
    @FXML private TableView<ModrinthProjectVersion> versionTable;
    @FXML private TableColumn<ModrinthProjectVersion, String> nameColumn;
    @FXML private TableColumn<ModrinthProjectVersion, String> versionColumn;
    @FXML private TableColumn<ModrinthProjectVersion, String> gameVersionColumn;

    public EventDispatcher<Runnable> getCancelEvent() {
        return cancelEvent;
    }

    public EventDispatcher<Consumer<String>> getSelectEvent() {
        return selectEvent;
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name));
        versionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().versionNumber));
        gameVersionColumn.setCellValueFactory(
                data -> new SimpleStringProperty(String.join(", ", data.getValue().gameVersions)));

        versionTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        versionTable.getSelectionModel().selectedItemProperty().addListener(this::onUpdateSelection);
    }
    
    @FXML
    public void cancel() {
        cancelEvent.invoker().run();
    }

    @FXML
    public void select() {
        ModrinthProjectVersion version = versionTable.getSelectionModel().getSelectedItem();
        if (version == null) {
            System.err.println("Tried to commit selection with nothing selected!");
            return;
        }

        selectEvent.invoker().accept(version.id);
    }

    private void onUpdateSelection(ObservableValue<? extends ModrinthProjectVersion> observable,
            ModrinthProjectVersion old, ModrinthProjectVersion newVal) {
        selectButton.setDisable(newVal == null);
    }

    @FXML
    public void loadUrl() {
        String url = urlTextField.getText();
        if (url == null || url.isBlank()) {
            showLoadErrorScreen(new IllegalArgumentException("Please enter a modpack url."), url);
            return;
        }

        loadUrlString(url);
    }

    public CompletableFuture<?> loadUrlString(String url) {
        openButton.setDisable(true);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return doLoadUrl(url);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).whenCompleteAsync((versions, e) -> {
            openButton.setDisable(false);

            if (e instanceof CompletionException) {
                e = e.getCause();
            }

            if (e != null) {
                showLoadErrorScreen(e, url);
            } else {
                showVersions(versions);
            }
            
        }, Platform::runLater);
    }

    public CompletableFuture<?> loadFromId(String id) {
        openButton.setDisable(true);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return doLoadVersions(id);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).whenCompleteAsync((versions, e) -> {
            openButton.setDisable(false);

            if (e instanceof CompletionException) {
                e = e.getCause();
            }

            if (e != null) {
                showLoadErrorScreen(e, id);
            } else {
                showVersions(versions);
            }
        }, Platform::runLater);
    }

    private static ModrinthProjectVersion[] doLoadUrl(String url) throws Exception {
        URI uri = new URI(url);
        if (!"modrinth.com".equals(uri.getAuthority())) {
            throw new IllegalArgumentException("Only Modrinth urls are allowed.");
        }

        String[] path = uri.getPath().split("/");
        String slug = path[path.length - 1];

        return doLoadVersions(slug);
    }

    private static ModrinthProjectVersion[] doLoadVersions(String slug) throws IOException, InterruptedException {
        ModrinthWebAPI api = ModrinthWebAPI.getGlobalInstance();

        ModrinthProject project = api.getProject(slug);
        if (project.projectType != ModrinthProjectType.MODPACK) {
            throw new IllegalArgumentException("Selected project is not a modpack!");
        }

        return api.getProjectVersions(slug);
    }

    private void showVersions(ModrinthProjectVersion[] versions) {
        versionTable.getSelectionModel().clearSelection();
        versionTable.getItems().clear();
        versionTable.getItems().addAll(versions);
    }

    private void showLoadErrorScreen(Throwable e, String slug) {
        String text;
        if (e instanceof IllegalArgumentException) {
            text = e.getMessage();
        } else if (e instanceof URISyntaxException) {
            text = "Invalid URL: " + e.getMessage();
        } else if (e instanceof HttpException http) {
            if (http.getStatusCode() == 404) {
                text = "Unable to find modpack '" + slug + "'";
            } else {
                text = "Modrinth returned error code " + http.getStatusCode();
            }
        } else {
            text = "An unexpected error occured loading the modpack. See console for details.";
            e.printStackTrace();
        }

        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error loading modpack.");
        alert.setHeaderText("Error loading modpack:");
        alert.setContentText(text);

        alert.show();
    }

    public static String prevUrl;

    public static ModrinthBrowser open(Consumer<String> onSelect) {
        try {
            FXMLLoader loader = new FXMLLoader(ModrinthBrowser.class.getResource("/ui/modrinthBrowser.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Select Modpack Version");

            ModrinthBrowser controller = loader.getController();
            controller.getCancelEvent().addListener(() -> {
                stage.close();
            });

            controller.getSelectEvent().addListener(val -> {
                stage.close();
                onSelect.accept(val);
                prevUrl = controller.urlTextField.getText();
            });

            if (prevUrl != null) {
                controller.urlTextField.setText(prevUrl);
                controller.loadUrl();
            }

            stage.show();
            return controller;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
}
