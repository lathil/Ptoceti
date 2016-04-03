package com.ptoceti.osgi.obix.impl.observer;

import org.osgi.service.log.LogService;

import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.impl.command.AddHistoryRecordCommand;
import com.ptoceti.osgi.obix.impl.guice.GuiceContext;
import com.ptoceti.osgi.obix.impl.service.Activator;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.observable.IObserver;
import com.ptoceti.osgi.obix.observable.ObservableEvent;

public class HistoryObserver  implements IObserver<Obj> {

	protected String historyUri;
	
	AddHistoryRecordCommand recordCommand;
	
	public HistoryObserver(String uri, AddHistoryRecordCommand command){
		historyUri = uri;
		recordCommand = command;
	}
	
	@Override
	public void notify(Obj model, ObservableEvent event) {
	
		try {
			recordCommand.execute(historyUri, ((Val)model));
		} catch (DomainException ex) {
			Activator.log(LogService.LOG_ERROR, "Error in HistoryObserver.notify: " + ex.toString());
		}
	}

}
