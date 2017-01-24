package com.ptoceti.osgi.obix.impl.front.converters;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonCustomConverter extends JacksonConverter {
    @Override
    protected <T> JacksonRepresentation<T> create(MediaType mediaType, T source) {
        ObjectMapper mapper = createMapper();
        JacksonRepresentation<T> jr = new JacksonRepresentation<T>(mediaType, source);
        jr.setObjectMapper(mapper);
        return jr;
    }

    @Override
    protected <T> JacksonRepresentation<T> create(Representation source, Class<T> objectClass) {
        ObjectMapper mapper = createMapper();
        JacksonRepresentation<T> jr = new JacksonRepresentation<T>(source, objectClass);
        jr.setObjectMapper(mapper);
        return jr;
    }


    private ObjectMapper createMapper() {
       return ObjectMapperFactory.configure();
    }

} 
