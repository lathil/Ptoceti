package com.ptoceti.osgi.obix.impl.front;

import com.ptoceti.osgi.obix.impl.front.converters.JacksonObjectMapperProvider;
import com.ptoceti.osgi.obix.impl.front.converters.XmlMessageBodyReader;
import com.ptoceti.osgi.obix.impl.front.converters.XmlMessageBodyWriter;
import com.ptoceti.osgi.obix.impl.front.filters.CorsFilter;
import com.ptoceti.osgi.obix.impl.front.resources.*;
import com.ptoceti.osgi.obix.impl.guice.GuiceContext;
import com.ptoceti.osgi.obix.impl.hk2.ApplicationBinder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;


public class ObixApplicationConfig extends ResourceConfig {

    @Inject
    public ObixApplicationConfig(ServiceLocator serviceLocator) {


        //register(new ApplicationBinder());

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(GuiceContext.Instance.getInjector());


        register(new CorsFilter());

        registerClasses(AboutServerResource.class);
        registerClasses(AlarmAckServerResource.class);
        registerClasses(AlarmServerResource.class);
        registerClasses(AlarmServiceServerResource.class);
        registerClasses(BatchServerResource.class);
        registerClasses(HistoryQueryServerResource.class);
        registerClasses(HistoryRollupServerResource.class);
        registerClasses(HistoryServerResource.class);
        registerClasses(HistoryServiceServerResource.class);
        registerClasses(LobbyServerResource.class);
        registerClasses(ObjServerResource.class);
        registerClasses(RangeAlarmMaxServerResource.class);
        registerClasses(RangeAlarmMinServerResource.class);
        registerClasses(SearchServerResource.class);
        registerClasses(WatchAddServerResource.class);
        registerClasses(WatchDeleteServerResource.class);
        registerClasses(WatchPoolChangesServerResource.class);
        registerClasses(WatchPoolRefreshServerResource.class);
        registerClasses(WatchRemoveServerResource.class);
        registerClasses(WatchServerResource.class);
        registerClasses(WatchServiceServerResource.class);

        register(org.glassfish.jersey.jackson.JacksonFeature.class);
        register(JacksonObjectMapperProvider.class);

        register(XmlMessageBodyReader.class);
        register(XmlMessageBodyWriter.class);

    }
}
