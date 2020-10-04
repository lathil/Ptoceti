package com.ptoceti.osgi.rest.impl.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.MqttServiceListener;
import com.ptoceti.osgi.rest.impl.TimeSeriesServiceListener;
import com.ptoceti.osgi.rest.impl.WireAdminServiceListener;
import com.ptoceti.osgi.rest.impl.application.MqttResource;
import com.ptoceti.osgi.rest.impl.application.TimeSeriesResource;
import com.ptoceti.osgi.rest.impl.application.WiresResource;

public class ApplicationModule extends AbstractModule {


    public ApplicationModule() {

    }

    @Override
    protected void configure() {
        super.configure();
        bind(WiresResource.class);
        bind(MqttResource.class);
        bind(TimeSeriesResource.class);
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

}
