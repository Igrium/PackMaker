package com.igrium.packmaker.common;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandler;

import com.igrium.packmaker.common.util.HttpException;
import com.igrium.packmaker.common.util.WebUtils;

public abstract class WebApi {
    protected final URI baseUrl;
    protected final HttpClient httpClient = buildHttpClient();

    protected HttpClient buildHttpClient() {
        return HttpClient.newBuilder()
            .followRedirects(Redirect.ALWAYS)
            .build();
    }

    public WebApi(URI baseUrl) {
        this.baseUrl = baseUrl;
    }

    public WebApi(String baseUrl) {
        this.baseUrl = WebUtils.uri(baseUrl);
    }

    public URI getBaseUrl() {
        return baseUrl;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public <T> T sendRequest(HttpRequest request, BodyHandler<T> bodyHandler) throws IOException, InterruptedException {
        var res = httpClient.send(request, bodyHandler);
        if (res.statusCode() >= 400) {
            throw new HttpException(res.statusCode());
        }
        return res.body();
    }
}
