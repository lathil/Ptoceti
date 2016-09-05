package com.ptoceti.osgi.obix.impl.observer;

import org.osgi.service.log.LogService;

import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.impl.command.AlarmUpdateCommand;
import com.ptoceti.osgi.obix.impl.service.Activator;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.observable.IObserver;
import com.ptoceti.osgi.obix.observable.ObservableEvent;

public class AlarmObserver implements IObserver<Obj> {

	protected String alarmUri;
	AlarmUpdateCommand updateCommand;
	
	public AlarmObserver(String uri, AlarmUpdateCommand command){
		alarmUri = uri;
		updateCommand = command;
	}
	
	@Override
	public void notify(Obj model, ObservableEvent event) {
		try {
			if( event.equals(ObservableEvent.VALCHANGED)){
				updateCommand.execute(alarmUri, ((Val)model));
			}
		} catch (DomainException ex) {
			Activator.log(LogService.LOG_ERROR, "Error in HistoryObserver.notify: " + ex.toString());
		}
	}

}
