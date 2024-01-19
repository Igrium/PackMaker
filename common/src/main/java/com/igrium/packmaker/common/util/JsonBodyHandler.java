package com.igrium.packmaker.common.util;

import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class JsonBodyHandler<T> implements BodyHandler<T> {

    private static final Gson DEFAULT_GSON = new Gson();

    
    public static <T> JsonBodyHandler<T> ofJson(Class<T> classOfT, Gson gson) {
        return new ClassJsonBodyHandler<>(gson, classOfT);
    }

    public static <T> JsonBodyHandler<T> ofJson(Class<T> classOfT) {
        return ofJson(classOfT, DEFAULT_GSON);
    }

    public static <T> JsonBodyHandler<T> ofJson(TypeToken<T> typeOfT, Gson gson) {
        return new TypeTokenJsonBodyHandler<>(gson, typeOfT);
    }

    public static <T> JsonBodyHandler<T> ofJson(TypeToken<T> typeOfT) {
        return ofJson(typeOfT, DEFAULT_GSON);
    }

    private final Gson gson;
    
    protected abstract T createObject(Gson gson, String str);

    protected JsonBodyHandler(Gson gson) {
        this.gson = gson;
    }
    
    @Override
    public BodySubscriber<T> apply(ResponseInfo responseInfo) {
        BodySubscriber<String> upstream = BodySubscribers.ofString(StandardCharsets.UTF_8);
        return BodySubscribers.mapping(upstream, body -> createObject(gson, body));
    }

    private static class ClassJsonBodyHandler<T> extends JsonBodyHandler<T> {

        private final Class<T> clazz;

        protected ClassJsonBodyHandler(Gson gson, Class<T> clazz) {
            super(gson);
            this.clazz = clazz;
        }

        @Override
        protected T createObject(Gson gson, String str) {
            return gson.fromJson(str, clazz);
        }
        
    }

    private static class TypeTokenJsonBodyHandler<T> extends JsonBodyHandler<T> {

        private final TypeToken<T> token;

        protected TypeTokenJsonBodyHandler(Gson gson, TypeToken<T> token) {
            super(gson);
            this.token = token;
        }
        
        @Override
        protected T createObject(Gson gson, String str) {
            return gson.fromJson(str, token);
        }
    }

}
