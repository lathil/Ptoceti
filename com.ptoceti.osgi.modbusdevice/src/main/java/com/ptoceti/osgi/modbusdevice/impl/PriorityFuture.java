package com.ptoceti.osgi.modbusdevice.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PriorityFuture<T> implements RunnableFuture<T> {

    private RunnableFuture<T> src;
    private int priority;

    public PriorityFuture(RunnableFuture<T> other, int priority) {
        this.src = other;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return src.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return src.isCancelled();
    }

    public boolean isDone() {
        return src.isDone();
    }

    public T get() throws InterruptedException, ExecutionException {
        return src.get();
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return src.get();
    }

    public void run() {
        src.run();
    }
}
