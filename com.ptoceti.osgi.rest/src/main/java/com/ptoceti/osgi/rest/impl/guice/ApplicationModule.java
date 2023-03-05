package com.ptoceti.osgi.rest.impl.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.ptoceti.osgi.auth.AuthService;
import com.ptoceti.osgi.auth.JwtRoleBaseSecurityFeature;
import com.ptoceti.osgi.rest.impl.*;
import com.ptoceti.osgi.rest.impl.application.*;
import org.jboss.resteasy.plugins.guice.ext.VariantListBuilderProvider;

import javax.ws.rs.core.Variant;

public class ApplicationModule extends AbstractModule {


    public ApplicationModule() {

    }

    @Override
    protected void configure() {
        super.configure();
        bind(JwtRoleBaseSecurityFeature.class);
        bind(WiresResource.class);
        bind(MqttResource.class);
        bind(ThingResource.class);
        bind(ItemsResource.class);
        bind(DriversResource.class);
        bind(DevicesResource.class);
        bind(MetatypeResource.class);
        bind(ConfigurationResource.class);
        bind(FactoriesResource.class);
    }

    @Provides
    WireAdminServiceListener getWireAdminListener() {
        return Activator.getWireAdminServiceListener();
    }

    @Provides
    MqttServiceListener getMqttServiceListener() {
        return Activator.getMqttServiceListener();
    }

    @Provides
    TimeSeriesServiceListener getTimeSeriesServiceListener() {
        return Activator.getTimeSeriesServiceListener();
    }

    @Provides
    DriverServiceTracker getDriverServiceTracker() {
        return Activator.getDriverServiceTracker();
    }

    @Provides
    DeviceServiceTracker getDeviceServiceTracker() {
        return Activator.getDeviceServiceTracker();
    }

    @Provides
    DalServiceTracker getDalServiceTracker() {
        return Activator.getDalServiceTracker();
    }

    @Provides
    FunctionServiceTracker getFunctionServiceTracker() {
        return Activator.getFunctionServiceTracker();
    }

    @Provides
    ConfigAdminServiceListener getConfigurationServicelistener() {
        return Activator.getConfigurationAdminlistener();
    }

    @Provides
    MetaTypeServiceListener getMetaTypeServiceListener() {
        return Activator.getMetaTypeServiceListener();
    }

    @Provides
    DeviceAdminServiceListener getDeviceAdminServiceListener() {
        return Activator.getDeviceAdminServiceListener();
    }

    @Provides
    DeviceAccessServiceListener getDeviceAccessServiceListener() {
        return Activator.getDeviceAccessServiceListener();
    }

    @Provides
    EventAdminEventHandler getGenericEventHandler() {
        return Activator.getEventAdminEventHandler();
    }

    @Provides
    AuthService getAuthServiceImpl() {
        return Activator.getAuthServiceListener().get();
    }

}
