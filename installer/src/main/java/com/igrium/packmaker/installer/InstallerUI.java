package com.igrium.packmaker.installer;

import java.awt.CardLayout;
import java.awt.Container;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletionException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.igrium.packmaker.common.InstallerConfig;
import com.igrium.packmaker.common.pack.ModpackProvider;
import com.igrium.packmaker.installer.ui.GameFolderSelectScreen;
import com.igrium.packmaker.installer.ui.InstallCompleteScreen;
import com.igrium.packmaker.installer.ui.InstallFailedScreen;
import com.igrium.packmaker.installer.ui.InstallingScreen;
import com.igrium.packmaker.installer.ui.LauncherFolderSelectScreen;
import com.igrium.packmaker.installer.ui.WelcomeScreen;
import com.igrium.packmaker.installer.util.OSUtil;
public class InstallerUI {


    private JFrame frame;
    private Container contentPane;
    private CardLayout cards;

    private final InstallerApp app;
    private final Context context = new Context();

    public InstallerUI(InstallerApp app) {
        this.app = app;
    }

    public InstallerApp getApp() {
        return app;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Initialize the contents of the frame.
     * @wbp.parser.entryPoint
     */
    public void open() {
        context.modpackName = app.getConfig().getModpackName();
        SwingUtilities.invokeLater(() -> {
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
        });
    }

    private WelcomeScreen welcomeScreen;
    private LauncherFolderSelectScreen launcherFolderSelectScreen;
    private GameFolderSelectScreen gameFolderSelectScreen;
    private InstallingScreen installingScreen;
    private InstallCompleteScreen completeScreen;
    private InstallFailedScreen installFailedScreen;

    private boolean isInitialized;

    public boolean isInitialized() {
        return isInitialized;
    }

    private void init() {
        InstallerConfig config = InstallerApp.getInstance().getConfig();

        welcomeScreen = new WelcomeScreen(this);
        frame.getContentPane().add(welcomeScreen.initialize(), "welcome");
        welcomeScreen.setFromConfig(config.getWelcomeScreen());

        launcherFolderSelectScreen = new LauncherFolderSelectScreen(this);
        frame.getContentPane().add(launcherFolderSelectScreen.initialize(), "selectLauncherFolder");
        launcherFolderSelectScreen.setFromConfig(config.getLauncherDirScreen());

        gameFolderSelectScreen = new GameFolderSelectScreen(this);
        frame.getContentPane().add(gameFolderSelectScreen.initialize(), "selectGameFolder");
        gameFolderSelectScreen.setFromConfig(config.getGameDirScreen());

        installingScreen = new InstallingScreen(this);
        frame.getContentPane().add(installingScreen.initialize(), "installing");

        completeScreen = new InstallCompleteScreen(this);
        frame.getContentPane().add(completeScreen.initialize(), "complete");
        completeScreen.setFromConfig(config.getCompleteScreen());

        installFailedScreen = new InstallFailedScreen(this);
        frame.getContentPane().add(installFailedScreen.initialize(), "installFailed");

        isInitialized = true;
        context.update();
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
            Path defaultPath = OSUtil.findDefaultLauncherPath();
            if (defaultPath != null) launcherFolderSelectScreen.setFolder(defaultPath.toFile());
        }

        cards.show(contentPane, "selectLauncherFolder");
    }

    public void openGameFolderSelect() {
        if (!gameFolderSelectScreen.getFolder().isDirectory()) {
            gameFolderSelectScreen.setFolder(launcherFolderSelectScreen.getFolder());
        }

        cards.show(contentPane, "selectGameFolder");
    }

    public void openInstallingScreen() {
        cards.show(contentPane, "installing");
    }

    public void install() {
        openInstallingScreen();
        Thread installThread = new Thread(this::doInstall, "Installer");
        installThread.start();
    }
    
    protected void doInstall() {
        try {
            ModpackProvider modpack = ModpackProvider.fromConfig(app.getConfig());
            Path launcherDir = launcherFolderSelectScreen.getFolder().toPath();
            Path gameDir = gameFolderSelectScreen.getFolder().toPath();

            String profileName = app.getConfig().getCustomProfileName();
            if (profileName == null) profileName = app.getConfig().getModpackName();

            String profile = Installer.install(installingScreen, profileName, launcherDir, gameDir, modpack);
            SwingUtilities.invokeLater(() -> openCompleteScreen(profile));

        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof CompletionException) {
                e = e.getCause();
            }
            openInstallFailedScreen(e);
        }
    }

    public File getLauncherDir() {
        return launcherFolderSelectScreen.getFolder();
    }

    public File getGameDir() {
        return gameFolderSelectScreen.getFolder();
    }

    public void openCompleteScreen(String profileName) {
        context.setProfileName(profileName);
        cards.show(contentPane, "complete");
    }

    public void openInstallFailedScreen(Throwable ex) {
        installFailedScreen.setException(ex);
        cards.show(contentPane, "installFailed");
    }

    public JFrame getFrame() {
        return frame;
    }

    public class Context {
        private String modpackName;
        
        public String getModpackName() {
            return modpackName;
        }

        public void setModpackName(String modpackName) {
            this.modpackName = modpackName;
            update();
        }

        private String profileName;

        public String getProfileName() {
            return profileName;
        }

        public void setProfileName(String profileName) {
            this.profileName = profileName;
            update();
        }

        protected void update() {
            welcomeScreen.updateContext(this);
            launcherFolderSelectScreen.updateContext(this);
            gameFolderSelectScreen.updateContext(this);
            completeScreen.updateContext(this);
        }
    }
}
