package com.ptoceti.osgi.influxdb.impl.client.restlet.converter;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.Resource;

import com.ptoceti.osgi.influxdb.Batch;
import com.ptoceti.osgi.influxdb.Point;
import com.ptoceti.osgi.influxdb.converter.LineProtocol;

public class InfluxDbLineProtocolRepresentation<T> extends WriterRepresentation {

    /** The (parsed) object to format. */
    private T object;

    /** The object class to instantiate. */
    private Class<T> objectClass;

    /** The InfluxDb representation to parse. */
    private Representation influxdbRepresentation;

    private Resource resource;

    private LineProtocol lineProtocol;

    public InfluxDbLineProtocolRepresentation(MediaType mediaType, T object, Resource resource) {
	this(mediaType);
	this.object = object;
	this.objectClass = (Class<T>) ((object == null) ? null : object.getClass());
	this.influxdbRepresentation = null;
	this.resource = resource;
	// this.objectMapper = null;
    }

    public InfluxDbLineProtocolRepresentation(Representation representation, Class<T> objectClass, Resource resource) {
	this(representation.getMediaType());
	this.object = null;
	this.objectClass = objectClass;
	this.influxdbRepresentation = representation;
	this.resource = resource;
	// this.objectMapper = null;
    }

    public InfluxDbLineProtocolRepresentation(MediaType mediaType) {
	super(mediaType);
	lineProtocol = new LineProtocol();
    }

    @Override
    public void write(Writer writer) throws IOException {
	if (this.influxdbRepresentation != null)
	    this.influxdbRepresentation.write(writer);
	else if (this.object != null) {

	    if (object instanceof Point) {
		writer.write(lineProtocol.toLine((Point) this.object));
	    } else if (object instanceof Batch) {
		writer.write(lineProtocol.toLine((Batch) this.object));
	    }

	}

    }

}
