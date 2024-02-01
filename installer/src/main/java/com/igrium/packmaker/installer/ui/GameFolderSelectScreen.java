package com.igrium.packmaker.installer.ui;

import java.io.File;
import java.util.Objects;

import javax.swing.JOptionPane;

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
    protected String getDefaultTitle() {
        return "Select Game Directory";
    }

    @Override
    protected String getDefaultDescription() {
        return "This is the folder where your game-specific files are stored (worlds, etc.)";
    }

    @Override
    protected void onSubmit(File folder) {
        String twinGameFolderWarning = ui.getApp().getConfig().getTwinFolderWarning();
        if (Objects.equals(ui.getLauncherDir(), folder) && twinGameFolderWarning != null) {
            String[] buttons = { "Install Anyway", "Go Back" };
            twinGameFolderWarning = this.formatString(twinGameFolderWarning);
            String warningText = "<html><body><p style='width: 200px;'>"+twinGameFolderWarning.replace("\n", "<br>");

            int input = JOptionPane.showOptionDialog(getRoot(), warningText, "Proceed with installation?",
                    JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[1]);

            if (input != 0) return;
        }

        ui.install();
    }

    @Override
    protected void goBack() {
        ui.openLauncherFolderSelect();
    }
    
}
