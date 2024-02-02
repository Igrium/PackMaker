package com.igrium.packmaker.ui;

import com.igrium.packmaker.common.InstallerConfig;
import com.igrium.packmaker.common.InstallerConfig.ScreenConfig;
import com.igrium.packmaker.exporter.ExportConfig;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;

public class ConfigEditor {

    @FXML
    private GeneralConfig generalConfigController;

    public GeneralConfig getGeneralConfigController() {
        return generalConfigController;
    }

    @FXML
    private ScreenConfigEditor welcomeScreenController;

    public ScreenConfigEditor getWelcomeScreenController() {
        return welcomeScreenController;
    }

    @FXML
    private ScreenConfigEditor launcherDirScreenController;

    public ScreenConfigEditor getLauncherDirScreenController() {
        return launcherDirScreenController;
    }

    @FXML
    private ScreenConfigEditor gameDirScreenController;

    public ScreenConfigEditor getGameDirScreenController() {
        return gameDirScreenController;
    }

    @FXML
    private ScreenConfigEditor completeScreenController;

    public ScreenConfigEditor getCompleteScreenController() {
        return completeScreenController;
    }



    private StringProperty packNameProperty;

    public String getPackName() {
        return packNameProperty.get();
    }

    public void setPackName(String packName) {
        packNameProperty.set(packName);
    }

    public StringProperty packNameProperty() {
        return packNameProperty;
    }

    @FXML
    private void initialize() {
        welcomeScreenController.setHeaderLabel("Welcome Screen");
        welcomeScreenController.setDefaultHeader("Install {modpack}.");
        welcomeScreenController.setDefaultDescription("Please close the Minecraft Launcher before proceeding.");

        launcherDirScreenController.setHeaderLabel("Launcher Directory Sceen");
        launcherDirScreenController.setDefaultHeader("Select Installation Directory");
        launcherDirScreenController.setDefaultDescription("Please locate the directory in which the Minecraft Launcher is installed.");

        gameDirScreenController.setHeaderLabel("Game Directory Screen");
        gameDirScreenController.setDefaultHeader("Select Game Directory");
        gameDirScreenController.setDefaultDescription("This is the folder where your game-specific files are stored (worlds, etc.)");

        completeScreenController.setHeaderLabel("Complete Screen");
        completeScreenController.setDefaultHeader("Installation Complete!");
        completeScreenController.setDefaultDescription("A profile has been created in your launcher called '{profile}'.");
        resetAll();
    }

    @FXML
    public void resetAll() {
        generalConfigController.reset();
        welcomeScreenController.reset();
        launcherDirScreenController.reset();
        gameDirScreenController.reset();
        completeScreenController.reset();
    }

    public InstallerConfig applyConfig(InstallerConfig config) {
        generalConfigController.applyConfig(config);
        config.setWelcomeScreen(welcomeScreenController.applyConfig(new ScreenConfig()));
        config.setLauncherDirScreen(launcherDirScreenController.applyConfig(new ScreenConfig()));
        config.setGameDirScreen(gameDirScreenController.applyConfig(new ScreenConfig()));
        config.setCompleteScreen(completeScreenController.applyConfig(new ScreenConfig()));
        return config;
    }

    public ExportConfig applyConfig(ExportConfig config) {
        generalConfigController.applyConfig(config);
        return config;
    }
}
