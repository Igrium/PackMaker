package com.igrium.packmaker.ui;

import java.io.File;
import java.io.IOException;

import com.igrium.packmaker.exporter.Exporter.ExportType;
import com.igrium.packmaker.util.EventDispatcher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public final class ExportWindow {

    private static String lastText;

    public static interface ExportEvent {
        public void onExport(File file, ExportType exportType);
    }

    private final EventDispatcher<ExportEvent> exportEvent = EventDispatcher.createArrayBacked(
        listeners -> (file, exportType) -> {
            for (var l : listeners) {
                l.onExport(file, exportType);
            }
        }
    );
    
    public EventDispatcher<ExportEvent> getExportEvent() {
        return exportEvent;
    }

    private final EventDispatcher<Runnable> cancelEvent = EventDispatcher.createNoArg();

    public EventDispatcher<Runnable> getCancelEvent() {
        return cancelEvent;
    }

    @FXML
    private Parent root;

    public Parent getRoot() {
        return root;
    }

    @FXML
    private TextField targetFileField;

    public TextField getTargetFileField() {
        return targetFileField;
    }

    @FXML
    private ToggleGroup exportType;

    @FXML
    private RadioButton exeButton;

    @FXML
    private RadioButton jarButton;

    @FXML
    protected void initialize() {
        exeButton.setUserData(ExportType.EXE);
        jarButton.setUserData(ExportType.JAR);

        if (lastText != null) {
            targetFileField.setText(lastText);
        }

        exportType.selectedToggleProperty().addListener(e -> updateFileExtension());
        targetFileField.focusedProperty().addListener((prop, oldVal, newVal) -> updateFileExtension());
        targetFileField.textProperty().addListener((prop, oldVal, newVal) -> lastText = newVal);
    }

    public File getSelectedFile() {
        String text = targetFileField.getText();
        return (text.isBlank()) ? null : new File(text);
    }

    public ExportType getExportType() {
        return (ExportType) exportType.getSelectedToggle().getUserData();
    }

    @FXML
    protected void updateFileExtension() {
        targetFileField.setText(processFileExtension(targetFileField.getText(), getExportType().getExtension()));
    }

    @FXML
    public void browseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export installer");
        File selected = getSelectedFile();
        if (selected != null) {
            chooser.setInitialDirectory(selected.getParentFile());
            chooser.setInitialFileName(selected.getName());
        }

        ExtensionFilter exeFilter = new ExtensionFilter("Windows Executables", "*.exe");
        ExtensionFilter jarFilter = new ExtensionFilter("Java Executables", "*.jar");

        chooser.getExtensionFilters().add(exeFilter);
        chooser.getExtensionFilters().add(jarFilter);
        chooser.setSelectedExtensionFilter(getExportType() == ExportType.EXE ? exeFilter : jarFilter);

        File file = chooser.showSaveDialog(root.getScene().getWindow());
        if (file != null) {
            String filename = file.toString();
            if (filename.endsWith(".exe")) {
                exeButton.setSelected(true);
            } else if (filename.endsWith(".jar")) {
                jarButton.setSelected(true);
            }

            targetFileField.setText(filename);
        }
    }

    private String processFileExtension(String filename, String desired) {
        if (filename == null || filename.isBlank()) return filename;
        if (filename.endsWith(desired)) return filename;

        int dot = filename.lastIndexOf('.');
        if (dot < 0) return filename + desired;

        String stripped = filename.substring(0, dot);
        return stripped + desired;
    }

    @FXML
    public void export() {
        exportEvent.invoker().onExport(getSelectedFile(), getExportType());
    }

    @FXML
    public void cancel() {
        cancelEvent.invoker().run();
    }

    public static ExportWindow open(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(ExportWindow.class.getResource("/ui/exportWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Export Installer");

            ExportWindow controller = loader.getController();

            controller.getCancelEvent().addListener(() -> stage.close());
            controller.getExportEvent().addListener((file, type) -> stage.close());

            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.show();
            return controller;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
