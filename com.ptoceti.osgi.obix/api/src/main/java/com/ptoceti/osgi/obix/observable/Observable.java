package com.ptoceti.osgi.obix.observable;

import java.util.ArrayList;
import java.util.List;

public class Observable<T> implements IObservable<T> {
    private final ArrayList<IObserver<? super T>> observers = new ArrayList<IObserver<? super T>>();

    public void addObserver(IObserver<? super T> observer) {
        synchronized (observers) {
        	if( !observers.contains(observer)){
        		observers.add(observer);
        	}
        }
    }

    public void removeObserver(IObserver<? super T> observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }
    
    public List<IObserver<? super T>> getObservers(){
    	return observers;
    }

    protected void notifyObservers(final T t, ObservableEvent event) {
        synchronized (observers) {
            for (IObserver<? super T> observer : observers) {
            	if( observer != null){
            		observer.notify(t, event);
            	}
            }
        }
    }
}
