package com.igrium.packmaker.installer;

import java.awt.CardLayout;
import java.awt.Container;
import java.io.File;

import javax.swing.JFrame;

import com.igrium.packmaker.installer.ui.GameFolderSelectScreen;
import com.igrium.packmaker.installer.ui.LauncherFolderSelectScreen;
import com.igrium.packmaker.installer.ui.WelcomeScreen;
import com.igrium.packmaker.installer.util.OSUtil;
public class InstallerUI {

    private JFrame frame;
    private Container contentPane;
    private CardLayout cards;

    private final InstallerApp app;

    public InstallerUI(InstallerApp app) {
        this.app = app;
    }

    public InstallerApp getApp() {
        return app;
    }


    /**
     * Initialize the contents of the frame.
     * @wbp.parser.entryPoint
     */
    public void open() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Install " + app.getConfig().getModpackName());
        // setContent(new WelcomeScreen(this).initialize());
        // frame.setVisible(true);
        cards = new CardLayout();
        contentPane = frame.getContentPane();
        frame.getContentPane().setLayout(cards);
        init();

        frame.setVisible(true);
    }

    private WelcomeScreen welcomeScreen;
    private LauncherFolderSelectScreen launcherFolderSelectScreen;
    private GameFolderSelectScreen gameFolderSelectScreen;

    private void init() {
        welcomeScreen = new WelcomeScreen(this);
        frame.getContentPane().add(welcomeScreen.initialize(), "welcome");

        launcherFolderSelectScreen = new LauncherFolderSelectScreen(this);
        frame.getContentPane().add(launcherFolderSelectScreen.initialize(), "selectLauncherFolder");

        gameFolderSelectScreen = new GameFolderSelectScreen(this);
        frame.getContentPane().add(gameFolderSelectScreen.initialize(), "selectGameFolder");
    }
    
    // private void setContent(Component content) {
    //     frame.getContentPane().removeAll();
    //     frame.getContentPane().add(content);
    // }

    public void openWelcomeScreen() {
        cards.show(contentPane, "welcome");
    }

    public void openLauncherFolderSelect() {
        if (!launcherFolderSelectScreen.getFolder().isDirectory()) {
            File defaultPath = OSUtil.getDefaultLauncherPath();
            if (defaultPath != null) launcherFolderSelectScreen.setFolder(defaultPath);
        }

        cards.show(contentPane, "selectLauncherFolder");
    }

    public void openGameFolderSelect() {
        if (!gameFolderSelectScreen.getFolder().isDirectory()) {
            gameFolderSelectScreen.setFolder(launcherFolderSelectScreen.getFolder());
        }

        cards.show(contentPane, "selectGameFolder");
    }

    public JFrame getFrame() {
        return frame;
    }
}
