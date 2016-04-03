package com.ptoceti.osgi.obix.observable;

public interface IObservable<T> {
    void addObserver(IObserver<? super T> observer);
    void removeObserver(IObserver<? super T> observer);
}
