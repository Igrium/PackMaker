package com.igrium.packmaker.installer;

import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.igrium.packmaker.common.modrinth.ModrinthWebAPI;
import com.igrium.packmaker.installer.util.ProgressHandle;

public class InstallerUI {

    public static void installModpack(Path gameDir, ProgressHandle progressHandle) {
        
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new InstallerUI().initUI();
    }

    public void initUI() {
        JFrame frame = new JFrame("Installer");
        JButton button = new JButton("Try get data");
        button.addActionListener(e -> tryGetData());

        button.setBounds(150, 200, 200, 50);
        frame.add(button);

        frame.setSize(500, 600);
        frame.setLayout(null);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void tryGetData() {
        // FabricWebApi api = new FabricWebApi();
        // try {
        //     List<LoaderVersion> loaderVersions = api.getLoaderVersions();
        //     System.out.println(loaderVersions.get(0));
        // } catch (IOException | InterruptedException e) {
        //     e.printStackTrace();
        // }
        ModrinthWebAPI api = new ModrinthWebAPI();
        try {
            var version = api.getProjectVersion("LvvmEhPB");
            System.out.println(version);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
