/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.igrium.packmaker;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.igrium.packmaker.common.pack.ModpackProvider;
import com.igrium.packmaker.common.util.HttpException;
import com.igrium.packmaker.exporter.Exporter;
import com.igrium.packmaker.mrpack.MrPack;
import com.igrium.packmaker.ui.MainUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private URL installerJar;

    private MainUI mainUI;
    private Parent root;

    public MainUI getMainUI() {
        return mainUI;
    }

    public Parent getRoot() {
        return root;
    }

    private Stage primaryStage;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;

        Scene scene = initUi();
        primaryStage.setScene(scene);
        primaryStage.show();

        installerJar = getClass().getResource("/installer.jar");
        if (installerJar == null) {
            System.err.println("Installer jar not found. Installer export will not work.");
            showNoInstallerError();
        }
    }

    public void showNoInstallerError() {
        Alert a = new Alert(AlertType.WARNING);
        a.setTitle("Installer not found");
        a.setHeaderText("Installer not found");
        a.setContentText("The bundled installer jar was not found. Installer export will not work.");
        a.show();
    }

    private Scene initUi() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
        root = loader.load();
        mainUI = loader.getController();
        mainUI.getSidebarController().getRefreshEvent().addListener(this::refreshModpack);
        mainUI.getSidebarController().getExportInstallerEvent().addListener(this::export);

        Scene scene = new Scene(root);
        return scene;
    }

    public record LoadedPack(ModpackProvider provider, MrPack pack) {};

    private LoadedPack currentPack;

    public LoadedPack getCurrentPack() {
        return currentPack;
    }

    public void setCurrentPack(LoadedPack currentPack) {
        this.currentPack = currentPack;
        mainUI.getSidebarController().loadPackInfo(currentPack);
    }

    public void setCurrentPack(ModpackProvider provider, MrPack pack) {
        setCurrentPack(new LoadedPack(provider, pack));
    }

    public void refreshModpack(ModpackProvider modpack) {
        mainUI.getSidebar().setDisable(true);
        CompletableFuture.supplyAsync(() -> {
            try {
                return modpack.downloadPack();
            } catch (IOException | InterruptedException e) {
                throw new CompletionException(e);
            }
        }).whenCompleteAsync((pack, ex) -> {
            mainUI.getSidebar().setDisable(false);
            if (ex != null) {
                handleLoadFail(ex);
                return;
            }
            if (pack.getIndex().getDependencies().getFabricLoader() == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Invalid loader.");
                alert.setHeaderText("Invalid loader.");
                alert.setContentText("Only Fabric modpacks are supported.");
                alert.show();
                return;
            }
            setCurrentPack(new LoadedPack(modpack, pack));
        }, Platform::runLater);
    }

    private void handleLoadFail(Throwable ex) {
        if (ex instanceof CompletionException) {
            ex = ex.getCause();
        }

        if (ex instanceof HttpException http) {
            if (http.getStatusCode() == 404) {
                doLoadFailedAlert("The specified modpack version was not found.");
            } else {
                doLoadFailedAlert("(status code %d)".formatted(http.getStatusCode()));
            }
        } else if (ex instanceof FileNotFoundException) {
            doLoadFailedAlert("The selected file could not be found.");
        } else {
            doLoadFailedAlert("An unknown error occurd. See console for details.");
            ex.printStackTrace();
        }
    }

    private void doLoadFailedAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Unable to load modpack");
        alert.setHeaderText("Unable to load modpack.");
        alert.setContentText(message);
        alert.show();
    }

    public CompletableFuture<?> export(ModpackProvider provider, String name) {
        if (installerJar == null) {
            showNoInstallerError();
            return CompletableFuture.failedFuture(new IllegalStateException("No installer"));
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export installer");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Java Executables", "*.jar"));

        File file = fileChooser.showSaveDialog(getPrimaryStage());
        if (file != null) {
            if (!file.getName().endsWith(".jar")) {
                file = new File(file.getAbsolutePath() + ".jar");
            }
            return doExport(file, provider, name);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }


    public CompletableFuture<?> doExport(File target, ModpackProvider provider, String name) {
        root.setDisable(true);
        
        return CompletableFuture.runAsync(() -> {
            try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {
                new Exporter(installerJar).export(out, name, provider);
                
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).whenCompleteAsync((val, ex) -> {
            root.setDisable(false);
            if (ex instanceof CompletionException) {
                ex = ex.getCause();
            }

            if (ex != null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error exporting modpack.");
                alert.setHeaderText("Error exporting modpack!");
                alert.setContentText(ex.getMessage());
                ex.printStackTrace();
                alert.show();

            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Export complete!");
                alert.setHeaderText("Export complete!");
                alert.setContentText("Wrote to " + target);
                alert.show();
            }
        }, Platform::runLater);
    }
}
