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
        String twinGameFolderWarning = ui.getApp().getConfig().getTwinGameFolderWarning();
        if (Objects.equals(ui.getLauncherDir(), folder) && twinGameFolderWarning != null) {
            // int input = JOptionPane.showMessageDialog(null, folder, twinGameFolderWarning, JOptionPane.WARNING_MESSAGE);
            int input = JOptionPane.showConfirmDialog(getRoot(), twinGameFolderWarning, "Proceed with install?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (input != 0) return;
        }

        ui.install();
    }

    @Override
    protected void goBack() {
        ui.openLauncherFolderSelect();
    }
    
}
