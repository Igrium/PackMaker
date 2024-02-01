package com.igrium.packmaker.installer.modpack;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.igrium.packmaker.mrpack.MrPack;
import com.igrium.packmaker.mrpack.MrPackFileRef;
import com.igrium.packmaker.mrpack.MrPack.EnvSide;
import com.igrium.packmaker.mrpack.MrPack.EnvSupport;
import com.igrium.packmaker.mrpack.MrPack.MrPackOverride;

public class PackInstaller {

    public static CompletableFuture<Void> install(MrPack pack, Path gameDir) {
        List<CompletableFuture<?>> futures = new LinkedList<>();
        System.out.println("Installing modpack into " + gameDir);
        for (MrPackFileRef file : pack.getIndex().getFiles()) {
            if (file.getEnv().client() == EnvSupport.UNSUPPORTED) continue;

            futures.add(CompletableFuture.runAsync(() -> {
                System.out.println("Downloading " + file.getPath());
                try {
                    file.download(gameDir);
                } catch (IOException | InterruptedException e) {
                    throw new CompletionException(e);
                }
            }));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture<?>[]::new)).thenRun(() -> {
            for (MrPackOverride override : pack.getAllOverrides(EnvSide.CLIENT)) {
                try {
                    override.extract(gameDir);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }
        });
    }
}
