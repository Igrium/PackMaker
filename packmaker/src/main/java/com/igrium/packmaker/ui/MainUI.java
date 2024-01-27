package com.igrium.packmaker.ui;

import javafx.fxml.FXML;
import javafx.scene.Parent;

public class MainUI {
    @FXML
    private SideBar sidebarController;

    @FXML
    private Parent sidebar;

    @FXML
    private ConfigEditor configEditorController;

    public SideBar getSidebarController() {
        return sidebarController;
    }

    public Parent getSidebar() {
        return sidebar;
    }

    public ConfigEditor getConfigEditorController() {
        return configEditorController;
    }
}
