package com.igrium.packmaker.installer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.igrium.packmaker.common.InstallerConfig;

public class InstallerApp {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static InstallerApp instance;

    public static InstallerApp getInstance() {
        if (instance == null) instance = new InstallerApp();
        return instance;
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        getInstance().start(args);
    }

    private InstallerConfig config = new InstallerConfig();

    public InstallerConfig getConfig() {
        return config;
    }

    public void setConfig(InstallerConfig config) {
        this.config = config;
    }

    public void start(String[] args) {
        try {
            initConfig();
        } catch (Exception e) {
            System.err.println("Unable to load config.");
            e.printStackTrace();
            System.out.println("Config format:");
            System.out.println(GSON.toJson(getConfig()));

            System.exit(1);
        }
        new InstallerUI(this).open();

    }

    private void initConfig() throws IOException {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/config.json")))) {
            setConfig(GSON.fromJson(reader, InstallerConfig.class));
        }
    }
}
