package com.ptoceti.osgi.auth.impl.application;

import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.auth.impl.PreferenceServiceListener;
import com.ptoceti.osgi.auth.impl.application.model.PreferencePropertyEntry;
import com.ptoceti.osgi.auth.impl.application.model.PreferencesWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

@Path("prefs")
@Tags({@Tag(name = "prefs")})
@Secured
public class PreferencesResource {

    @Inject
    PreferenceServiceListener preferenceServiceListener;

    @GET
    @Path("node/{nodePath: .+}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public PreferencesWrapper getPreferences(@Context SecurityContext securityContext, @PathParam("nodePath") @DefaultValue("") String nodePath) {

        Principal principal = securityContext.getUserPrincipal();
        PreferencesService preferenceService = preferenceServiceListener.getPreferenceservice();
        if (preferenceService != null) {

            Preferences prefs = preferenceService.getUserPreferences(principal.getName());
            if (nodePath.length() > 0) {
                prefs = prefs.node(nodePath);
            }

            try {
                return new PreferencesWrapper(prefs);
            } catch (BackingStoreException ex) {
                throw new ServiceUnavailableException();
            }
        }

        return null;
    }

    @DELETE
    @Path("node/{nodePath: .+}")
    public void deletePreferences(@Context SecurityContext securityContext, @PathParam("nodePath") @DefaultValue("") String nodePath) {

        Principal principal = securityContext.getUserPrincipal();
        PreferencesService preferenceService = preferenceServiceListener.getPreferenceservice();
        if (preferenceService != null) {
            try {
                Preferences pref = preferenceService.getUserPreferences(principal.getName());
                if (pref.nodeExists(nodePath)) {
                    pref.node(nodePath).removeNode();
                    ;
                }
            } catch (BackingStoreException ex) {
                throw new ServiceUnavailableException();
            }
        }
    }

    @POST
    @Path("props/{nodePath: .+}")
    public void addPreferencesProperty(@Context SecurityContext securityContext, @PathParam("nodePath") @DefaultValue("") String nodePath, PreferencePropertyEntry property) {
        Principal principal = securityContext.getUserPrincipal();
        PreferencesService preferenceService = preferenceServiceListener.getPreferenceservice();
        if (preferenceService != null) {
            try {
                Preferences pref = preferenceService.getUserPreferences(principal.getName());
                if (pref.nodeExists(nodePath)) {
                    pref.node(nodePath).put(property.getKey(), property.getValue());
                }
                throw new NotFoundException();
            } catch (BackingStoreException ex) {
                throw new ServiceUnavailableException();
            }
        }
    }

    @DELETE
    @Path("props/{nodePath: .+}")
    public void deletePreferencesProperty(@Context SecurityContext securityContext, @PathParam("nodePath") @DefaultValue("") String nodePath, @QueryParam("name") String name) {
        Principal principal = securityContext.getUserPrincipal();
        PreferencesService preferenceService = preferenceServiceListener.getPreferenceservice();
        if (preferenceService != null) {
            try {
                Preferences pref = preferenceService.getUserPreferences(principal.getName());
                if (pref.nodeExists(nodePath)) {
                    pref.node(nodePath).remove(name);
                }
                throw new NotFoundException();
            } catch (BackingStoreException ex) {
                throw new ServiceUnavailableException();
            }
        }
    }

    @GET
    @Path("props/{nodePath: .+}")
    public PreferencePropertyEntry getPreferencesProperty(@Context SecurityContext securityContext, @PathParam("nodePath") @DefaultValue("") String nodePath, @QueryParam("name") String name) {
        Principal principal = securityContext.getUserPrincipal();
        PreferencesService preferenceService = preferenceServiceListener.getPreferenceservice();
        if (preferenceService != null) {
            try {
                Preferences pref = preferenceService.getUserPreferences(principal.getName());
                if (pref.nodeExists(nodePath)) {
                    return new PreferencePropertyEntry(name, pref.node(nodePath).get(name, ""));
                }
                throw new NotFoundException();
            } catch (BackingStoreException ex) {
                throw new ServiceUnavailableException();
            }
        }
        return null;
    }
}
