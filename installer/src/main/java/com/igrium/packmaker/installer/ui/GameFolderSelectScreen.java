package com.igrium.packmaker.installer.ui;

import java.io.File;

import com.igrium.packmaker.installer.InstallerUI;

public class GameFolderSelectScreen extends AbstractFolderSelectScreen {

    private final InstallerUI ui;

    public GameFolderSelectScreen(InstallerUI ui) {
        this.ui = ui;
    }

    public InstallerUI getUi() {
        return ui;
    }

    @Override
    protected String getTitle() {
        return "Select Game Directory";
    }

    @Override
    protected String getDescription() {
        return "This is the folder where your game-specific files are stored (worlds, etc.)";
    }

    @Override
    protected void onSubmit(File folder) {
        ui.install();
    }

    @Override
    protected void goBack() {
        ui.openLauncherFolderSelect();
    }
    
}
