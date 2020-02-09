package com.ptoceti.osgi.auth.impl.application;

import com.ptoceti.osgi.auth.JwtAuthenticationFilter;
import com.ptoceti.osgi.auth.JwtRoleBaseSecurityFeature;
import com.ptoceti.osgi.auth.impl.Activator;
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
import org.jboss.resteasy.plugins.providers.*;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.resteasy.plugins.providers.jackson.UnrecognizedPropertyExceptionHandler;
import org.jboss.resteasy.plugins.providers.jaxb.*;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@OpenAPIDefinition(info = @Info(title = "Ptoceti Auth Api", version = "1.0.0"))
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
                .resourcePackages(Stream.of("com.ptoceti.osgi.auth.impl.application").collect(Collectors.toSet()));

        try {

            new JaxrsOpenApiContextBuilder()
                    .servletConfig(servletConfig)
                    .application(this)
                    .openApiConfiguration(oasConfig)
                    .ctxId("openapi.context.id.servlet.auth")
                    .buildContext(true);
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(AcceptHeaderOpenApiResource.class);
        classes.add(LoginResource.class);

        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<>();

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
        singletons.add(new FileRangeWriter());
        singletons.add(new StreamingOutputProvider());
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


        // filters
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.getAllowedOrigins().add("*");
        corsFilter.setAllowedMethods("OPTIONS, GET, POST, DELETE, PUT, PATCH");
        singletons.add(corsFilter);

        JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(Activator.getAuthService());
        singletons.add(authenticationFilter);

        JwtRoleBaseSecurityFeature jwtRoleBaseSecurityFeature = new JwtRoleBaseSecurityFeature();
        singletons.add(jwtRoleBaseSecurityFeature);

        return singletons;
    }
}
