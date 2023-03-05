package com.ptoceti.osgi.rest.impl.application;

import com.ptoceti.osgi.auth.JwtAuthenticationFilter;
import com.ptoceti.osgi.auth.JwtRoleBaseSecurityFeature;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.AuthServiceProxy;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
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
import org.jboss.resteasy.plugins.providers.sse.SseEventOutputProvider;
import org.jboss.resteasy.plugins.providers.sse.SseEventProvider;
import org.jboss.resteasy.plugins.providers.sse.SseEventSinkInterceptor;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@OpenAPIDefinition(info = @Info(title = "Ptoceti Rest Api", version = "1.0.0"))

public class RootApplication extends Application {

    public RootApplication(@Context ServletConfig servletConfig) {
        super();

        String rootApplicationPrefix = servletConfig.getInitParameter("resteasy.servlet.mapping.prefix");
        OpenAPI oas = new OpenAPI();
        Server server = new Server();
        server.setUrl(rootApplicationPrefix);
        oas.addServersItem(server);
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .resourcePackages(Stream.of("com.ptoceti.osgi.rest.impl.application").collect(Collectors.toSet()));

        try {

            new JaxrsOpenApiContextBuilder()
                    .servletConfig(servletConfig)
                    .application(this)
                    .openApiConfiguration(oasConfig)
                    .ctxId("openapi.context.id.servlet.rest")
                    .buildContext(true);
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(AcceptHeaderOpenApiResource.class);
        classes.add(RootResource.class);
        classes.add(WiresResource.class);
        classes.add(MqttResource.class);
        classes.add(ThingResource.class);
        classes.add(ItemsResource.class);
        classes.add(DriversResource.class);
        classes.add(DevicesResource.class);
        classes.add(MetatypeResource.class);
        classes.add(ConfigurationResource.class);
        classes.add(FactoriesResource.class);

        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<>();

        singletons.add(new EventResource(Activator.getEventAdminEventHandler()));
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

        singletons.add(new SseEventProvider());
        singletons.add(new SseEventOutputProvider());
        singletons.add(new SseEventSinkInterceptor());

        // filters
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.getAllowedOrigins().add("*");
        corsFilter.setAllowedMethods("OPTIONS, GET, POST, DELETE, PUT, PATCH");
        singletons.add(corsFilter);

        JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(new AuthServiceProxy());
        singletons.add(authenticationFilter);

        JwtRoleBaseSecurityFeature jwtRoleBaseSecurityFeature = new JwtRoleBaseSecurityFeature();
        singletons.add(jwtRoleBaseSecurityFeature);

        return singletons;
    }
}
