package com.ptoceti.osgi.influxdb;

public class QueryResultsHelper {

    public static Serie getSerie(String name, Result result) {

	if (result != null && result.getSeries().size() > 0) {
	    for (Serie serie : result.getSeries()) {
		if (serie.getName().equals(name)) {
		    return serie;
		}
	    }
	}

	return null;

    }

    public static Serie getSerie(int index, Result result) {

	if (result != null && result.getSeries().size() > 0) {
	    return result.getSeries().get(index);
	}

	return null;

    }

}
