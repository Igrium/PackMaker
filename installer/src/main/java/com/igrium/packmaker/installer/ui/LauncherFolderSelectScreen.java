package com.igrium.packmaker.installer.ui;

import java.io.File;

import com.igrium.packmaker.installer.InstallerUI;

public class LauncherFolderSelectScreen extends AbstractFolderSelectScreen {
    private final InstallerUI ui;

    public LauncherFolderSelectScreen(InstallerUI ui) {
        this.ui = ui;
    }

    public InstallerUI getUi() {
        return ui;
    }

    @Override
    protected String getTitle() {
        return "Select Installation Directory";
    }

    @Override
    protected String getDescription() {
        return "Please locate the directory in which Minecraft Launcher is installed.";
    }

    @Override
    protected void onSubmit(File folder) {
        ui.openGameFolderSelect();
    }

    @Override
    protected void goBack() {
        ui.openWelcomeScreen();
    }
}
