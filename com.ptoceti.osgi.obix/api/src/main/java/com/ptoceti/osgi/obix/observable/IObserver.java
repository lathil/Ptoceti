package com.ptoceti.osgi.obix.observable;

public interface IObserver<T> {
    void notify(T model, ObservableEvent event);
}