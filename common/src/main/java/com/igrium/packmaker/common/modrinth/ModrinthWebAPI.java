package com.igrium.packmaker.common.modrinth;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

import com.igrium.packmaker.common.WebApi;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.ModrinthProject;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.ModrinthProjectVersion;
import com.igrium.packmaker.common.util.JsonBodyHandler;

public class ModrinthWebAPI extends WebApi {
    public static final String DEFAULT_URL = "https://api.modrinth.com/v2/";

    private static ModrinthWebAPI globalInstance;

    public static ModrinthWebAPI getGlobalInstance() {
        if (globalInstance == null) globalInstance = new ModrinthWebAPI();
        return globalInstance;
    }
    
    public ModrinthWebAPI(URI baseUrl) {
        super(baseUrl);
    }

    public ModrinthWebAPI(String baseUrl) {
        super(baseUrl);
    }

    public ModrinthWebAPI() {
        this(DEFAULT_URL);
    }

    public ModrinthProject getProject(String slug) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(baseUrl.resolve("project/" + slug))
                .GET()
                .build();
        
        return sendRequest(request, JsonBodyHandler.ofJson(ModrinthProject.class));
    }
    
    public ModrinthProjectVersion[] getProjectVersions(String slug) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(baseUrl.resolve("project/" + slug + "/version"))
                .GET()
                .build();
        
        return sendRequest(request, JsonBodyHandler.ofJson(ModrinthProjectVersion[].class));
    }

    public ModrinthProjectVersion getProjectVersion(String versionId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
            .newBuilder(baseUrl.resolve("version/" + versionId))
            .GET()
            .build();
        
        // return httpClient.send(request, JsonBodyHandler.ofJson(JsonObject.class)).body();
        return sendRequest(request, JsonBodyHandler.ofJson(ModrinthProjectVersion.class));
    }
}
