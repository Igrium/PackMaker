package com.igrium.packmaker.common.fabric;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.igrium.packmaker.common.fabric.FabricWebTypes.LoaderVersion;
import com.igrium.packmaker.common.util.JsonBodyHandler;
import com.igrium.packmaker.common.util.WebUtils;

public class FabricWebApi {

    public static final String DEFAULT_URL = "https://meta.fabricmc.net/v2/";
    
    private final URI baseUrl;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(Redirect.ALWAYS)
            .build();

    public FabricWebApi(URI baseUrl) {
        this.baseUrl = baseUrl;
    }

    public FabricWebApi(String baseUrl) {
        this.baseUrl = WebUtils.uri(baseUrl);
    }

    public FabricWebApi() {
        this(DEFAULT_URL);
    }

    public URI getBaseUrl() {
        return baseUrl;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public List<LoaderVersion> getLoaderVersions() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(baseUrl.resolve("versions/loader"))
                .GET()
                .build();
        
        TypeToken<List<LoaderVersion>> type = new TypeToken<>() {};
        return httpClient.send(req, JsonBodyHandler.ofJson(type)).body();
    }

    public LoaderVersion latestLoaderVersion() throws IOException, InterruptedException {
        var versions = getLoaderVersions();
        for (LoaderVersion version : versions) {
            if (version.stable) return version;
        }
        return null;
    }

    public String getVersionJson(String gameVersion, String loaderVersion) throws IOException, InterruptedException {
        URI url = baseUrl.resolve("loader/%s/%s/profile/json".formatted(gameVersion, loaderVersion));
        HttpRequest req = HttpRequest.newBuilder(url).GET().build();

        return httpClient.send(req, BodyHandlers.ofString()).body();
    }

    // public CompletableFuture<JsonObject> downloadJson(Path gameDir, String gameVersion, String loaderVersion) {
    //     String profileName = "fabric-loader-%s-%s".formatted(loaderVersion, gameVersion);
    //     Path versionFile = gameDir.resolve(profileName).resolve(profileName + ".json");
    // }
}