package com.ptoceti.osgi.auth.impl.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.ptoceti.osgi.auth.AuthService;
import com.ptoceti.osgi.auth.JwtRoleBaseSecurityFeature;
import com.ptoceti.osgi.auth.impl.Activator;
import com.ptoceti.osgi.auth.impl.AuthServiceImpl;
import com.ptoceti.osgi.auth.impl.PreferenceServiceListener;
import com.ptoceti.osgi.auth.impl.application.LoginResource;
import com.ptoceti.osgi.auth.impl.application.PreferencesResource;
import org.osgi.framework.ServiceListener;
import org.osgi.service.prefs.PreferencesService;

public class ApplicationModule extends AbstractModule {


    public ApplicationModule() {

    }

    @Override
    protected void configure() {
        super.configure();
        bind(JwtRoleBaseSecurityFeature.class);
        bind(LoginResource.class);
        bind(PreferencesResource.class);

    }

    @Provides
    AuthService getAuthServiceImpl() {
        return Activator.getAuthService();
    }

    @Provides
    PreferenceServiceListener getPreferencesServiceListener() {
        return Activator.getPreferenceServiceListener();
    }


}
