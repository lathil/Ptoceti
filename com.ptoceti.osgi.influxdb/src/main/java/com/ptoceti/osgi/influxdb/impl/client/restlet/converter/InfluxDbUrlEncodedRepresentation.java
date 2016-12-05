package com.ptoceti.osgi.influxdb.impl.client.restlet.converter;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.Resource;

import com.ptoceti.osgi.influxdb.ql.Query;

public class InfluxDbUrlEncodedRepresentation<T> extends WriterRepresentation {

    /** The (parsed) object to format. */
    private T object;

    /** The object class to instantiate. */
    private Class<T> objectClass;

    /** The InfluxDb representation to parse. */
    private Representation influxdbRepresentation;

    private Resource resource;

    public InfluxDbUrlEncodedRepresentation(MediaType mediaType, T object, Resource resource) {
	this(mediaType);
	this.object = object;
	this.objectClass = (Class<T>) ((object == null) ? null : object.getClass());
	this.influxdbRepresentation = null;
	this.resource = resource;
    }

    public InfluxDbUrlEncodedRepresentation(MediaType mediaType) {
	super(mediaType);
    }

    @Override
    public void write(Writer writer) throws IOException {
	if (this.influxdbRepresentation != null)
	    this.influxdbRepresentation.write(writer);
	else if (this.object != null){

	    if (object instanceof Query) {
		String formParam = "q=" + ((Query) this.object).toQL();
		writer.write(formParam);
	    }
	}

    }

}
