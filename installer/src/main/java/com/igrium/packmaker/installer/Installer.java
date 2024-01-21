package com.igrium.packmaker.installer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonObject;
import com.igrium.packmaker.common.fabric.FabricWebApi;
import com.igrium.packmaker.common.pack.ModpackProvider;
import com.igrium.packmaker.installer.fabric.FabricInstaller;
import com.igrium.packmaker.installer.modpack.PackInstaller;
import com.igrium.packmaker.installer.util.ImageEncoder;
import com.igrium.packmaker.installer.util.ProgressHandle;
import com.igrium.packmaker.mrpack.MrPack;
import com.igrium.packmaker.mrpack.MrPack.MrPackOverride;
import com.igrium.packmaker.mrpack.MrPackIndex.MrPackDependencies;

public class Installer {

    /**
     * Synchronously install the desired modpack into the vanilla launcher.
     * @param handle A handle to recieve UI updates.
     * @param launcherDir Launcher root directory (presumably <code>%appdata%/.minecraft</code>).
     * @param gameDir Target Minecraft run directory.
     * @param modpack Mod pack to install.
     * @return The name of the profile that was installed.
     * @throws Exception If something goes wrong during install. May be wrapped in a <code>CompletionException</code>.
     */
    public static String install(ProgressHandle handle, Path launcherDir, Path gameDir, ModpackProvider modpack) throws Exception {
        handle.log("Fetching modpack metadata");
        handle.updateProgress(0, 4);
        MrPack pack = modpack.downloadPack();

        MrPackDependencies dependencies = pack.getIndex().getDependencies();
        String mcVersion = dependencies.getMinecraft();
        String loaderVersion = dependencies.getFabricLoader();

        if (loaderVersion == null) {
            throw new IllegalStateException("This installer only works for Fabric modpacks!");
        }

        handle.log("Installing Fabric");
        handle.updateProgress(1, 4);
        System.out.println("Installing Fabric %s into %s".formatted(loaderVersion, launcherDir));
        FabricWebApi fabricWeb = new FabricWebApi();
        JsonObject versionJson = FabricInstaller.downloadJson(launcherDir, mcVersion, loaderVersion, fabricWeb);

        handle.log("Downloading Modpack");
        handle.updateProgress(2, 4);
        
        if (!Files.isDirectory(gameDir)) {
            Files.createDirectories(gameDir);
        }
        PackInstaller.install(pack, gameDir).join();

        handle.log("Initializing Launcher Profile");
        handle.updateProgress(3, 4);

        String icon = "Crafting_Table";
        for (MrPackOverride override : pack.getGlobalOverrides()) {

            if (override.getPath().contains("icon.png")) {
                try(InputStream in = new BufferedInputStream(override.openStream())) {
                    icon = ImageEncoder.encodeImage(in);
                }
            }
        }

        FabricInstaller.installProfile(launcherDir, gameDir, pack.getIndex().getName(), icon, versionJson);

        handle.log("Finished");
        handle.updateProgress(4, 4);

        return pack.getIndex().getName();
    }
}
