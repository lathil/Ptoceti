package com.ptoceti.osgi.obix.impl.observer;

import java.util.ArrayList;
import java.util.List;

import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.observable.IObserver;
import com.ptoceti.osgi.obix.observable.ObservableEvent;

public class WatchObserver implements IObserver<Obj> {

	List<Obj> changedObjs = new ArrayList<Obj>();
	Object lock = new Object();
	
	@Override
	public void notify(Obj observable, ObservableEvent event) {
		if( event.equals(ObservableEvent.CHANGED)){
			synchronized(lock){
				if(!changedObjs.contains(observable)){
					changedObjs.add(observable);
				}
			}
		}
	}
	
	
	public List<Obj> getChangedObj(){
		List<Obj> result;
		synchronized(lock){
			result = new ArrayList<Obj>(changedObjs);
			changedObjs.clear();
		}
		return result;
	}
}
