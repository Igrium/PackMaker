package com.igrium.packmaker.common.util;

import java.io.IOException;

public class HttpException extends IOException {
    private final int statusCode;

    public HttpException(int statusCode) {
        super("Server returned HTTP status code " + statusCode);
        this.statusCode = statusCode;
    }

    public HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
