package com.igrium.packmaker.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface EventDispatcher<T> {
    public void register(T listener);
    public T invoker();

    public static <T> EventDispatcher<T> createArrayBacked(Function<List<T>, T> invokerFactory) {
        return new ArrayBackedEventDispatcher<>(invokerFactory);
    }

    public static <T> EventDispatcher<Consumer<T>> createSimple() {
        return new SimpleEventDispatcher<>();
    }

    public static EventDispatcher<Runnable> createNoArg() {
        return new NoArgEventDispatcher();
    }

    static class ArrayBackedEventDispatcher<T> implements EventDispatcher<T> {

        private final Function<List<T>, T> invokerFactory;
        private T invoker;

        private List<T> listeners = new ArrayList<>();

        public ArrayBackedEventDispatcher(Function<List<T>, T> invokerFactory) {
            this.invokerFactory = invokerFactory;
            this.invoker = invokerFactory.apply(listeners);
        }

        @Override
        public void register(T listener) {
            listeners.add(listener);
            this.invoker = invokerFactory.apply(listeners);
        }

        @Override
        public T invoker() {
            return invoker;
        }
        
    }

    static class SimpleEventDispatcher<T> implements EventDispatcher<Consumer<T>> {

        private List<Consumer<T>> listeners = new ArrayList<>();

        @Override
        public void register(Consumer<T> listener) {
            listeners.add(listener);
        }

        @Override
        public Consumer<T> invoker() {
            return this::invoke;
        }
        
        private void invoke(T val) {
            for (var l : listeners) {
                l.accept(val);
            }
        }
    }

    static class NoArgEventDispatcher implements EventDispatcher<Runnable> {

        private List<Runnable> listeners = new ArrayList<>();

        @Override
        public void register(Runnable listener) {
            listeners.add(listener);
        }

        @Override
        public Runnable invoker() {
            return this::invoke;
        }

        private void invoke() {
            for (var l : listeners) {
                l.run();
            }
        }
        
    }
}
