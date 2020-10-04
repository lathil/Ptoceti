package com.ptoceti.osgi.rest.impl.application;

import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;

import org.jboss.resteasy.plugins.providers.DocumentProvider;
import org.jboss.resteasy.plugins.providers.DefaultTextPlain;
import org.jboss.resteasy.plugins.providers.DefaultNumberWriter;
import org.jboss.resteasy.plugins.providers.DefaultBooleanWriter;
import org.jboss.resteasy.plugins.providers.StringTextStar;
import org.jboss.resteasy.plugins.providers.SourceProvider;
import org.jboss.resteasy.plugins.providers.InputStreamProvider;
import org.jboss.resteasy.plugins.providers.ReaderProvider;
import org.jboss.resteasy.plugins.providers.ByteArrayProvider;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.plugins.providers.JaxrsFormProvider;

import org.jboss.resteasy.plugins.providers.CompletionStageProvider;
import org.jboss.resteasy.plugins.providers.ReactiveStreamProvider;
import org.jboss.resteasy.plugins.providers.FileProvider;
import org.jboss.resteasy.plugins.providers.FileRangeWriter;
import org.jboss.resteasy.plugins.providers.StreamingOutputProvider;
import org.jboss.resteasy.plugins.providers.IIOImageProvider;
import org.jboss.resteasy.plugins.providers.MultiValuedParamConverterProvider;


import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.resteasy.plugins.providers.jackson.UnrecognizedPropertyExceptionHandler;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlSeeAlsoProvider;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider;
import org.jboss.resteasy.plugins.providers.jaxb.CollectionProvider;
import org.jboss.resteasy.plugins.providers.jaxb.MapProvider;

import com.ptoceti.osgi.rest.impl.resteasy.jaxb.XmlJAXBContextFinder;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@OpenAPIDefinition(info = @Info(title = "Ptoceti Rest Api", version = "1.0.0"))

// only used by servlet container initializer, eg with resteasy: ResteasyServletInitializer.java
//@ApplicationPath("/rest")
public class RootApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        // add swagger resources
        classes.add(OpenApiResource.class);
        classes.add(AcceptHeaderOpenApiResource.class);
        // register root resource
        classes.add(RootResource.class);
        classes.add(WiresResource.class);
        classes.add(MqttResource.class);
        classes.add(TimeSeriesResource.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<>();


        singletons.add(new DocumentProvider());
        singletons.add(new DefaultTextPlain());
        singletons.add(new DefaultNumberWriter());
        singletons.add(new DefaultBooleanWriter());
        singletons.add(new StringTextStar());
        singletons.add(new SourceProvider());
        singletons.add(new InputStreamProvider());
        singletons.add(new ReaderProvider());
        singletons.add(new ByteArrayProvider());
        singletons.add(new FormUrlEncodedProvider());
        singletons.add(new JaxrsFormProvider());
        singletons.add(new CompletionStageProvider());
        singletons.add(new ReactiveStreamProvider());
        singletons.add(new FileProvider());
        singletons.add(new FileRangeWriter());
        singletons.add(new StreamingOutputProvider());
        singletons.add(new IIOImageProvider());
        singletons.add(new MultiValuedParamConverterProvider());
        singletons.add(new GZIPDecodingInterceptor());
        singletons.add(new GZIPEncodingInterceptor());

        // jackson2 providers
        singletons.add(new ResteasyJackson2Provider());
        singletons.add(new UnrecognizedPropertyExceptionHandler());
        //singletons.add(new PatchMethodFilter());

        // jaxb providers

        singletons.add(new JAXBXmlSeeAlsoProvider());
        singletons.add(new JAXBXmlRootElementProvider());
        singletons.add(new JAXBElementProvider());
        singletons.add(new JAXBXmlTypeProvider());
        singletons.add(new CollectionProvider());
        singletons.add(new MapProvider());
        singletons.add(new XmlJAXBContextFinder());
        return singletons;
    }
}
