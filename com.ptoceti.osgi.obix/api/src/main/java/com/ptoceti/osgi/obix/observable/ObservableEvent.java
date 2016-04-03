package com.ptoceti.osgi.obix.observable;

public enum ObservableEvent {

	CHANGED( 1, "changed");
	
	final protected String name;
	final protected int id;

	ObservableEvent(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public static ObservableEvent getEventFromName( String name){
		
		ObservableEvent result = null;
		for( ObservableEvent event : ObservableEvent.values()){
			if( event.getName().equals(name)) {result = event; break;}
		}
		return result;
	}
	
	public static ObservableEvent getEventFromId( int id){
		
		ObservableEvent result = null;
		for( ObservableEvent event : ObservableEvent.values()){
			if( event.getId() == id) {result = event; break;}
		}
		return result;
	}
}
