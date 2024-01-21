package com.igrium.packmaker.ui;

import javafx.fxml.FXML;
import javafx.scene.Parent;

public class MainUI {
    @FXML
    private SideBar sidebarController;

    @FXML
    private Parent sidebar;

    public SideBar getSidebarController() {
        return sidebarController;
    }

    public Parent getSidebar() {
        return sidebar;
    }
}
