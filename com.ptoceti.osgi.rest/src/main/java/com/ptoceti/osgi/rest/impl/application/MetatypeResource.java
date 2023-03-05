package com.ptoceti.osgi.rest.impl.application;

import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.MetaTypeServiceListener;
import com.ptoceti.osgi.rest.impl.application.model.MetatypeWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.framework.Bundle;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.ObjectClassDefinition;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("metatype")
@Tags({@Tag(name = "metatype")})
@Secured
public class MetatypeResource {

    @Inject
    MetaTypeServiceListener metaTypeServiceListener;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public MetatypeWrapper[] getMetaTypes() {

        List<MetatypeWrapper> metatypeWrappers = new ArrayList<MetatypeWrapper>();
        for (Bundle bundle : Activator.getBundleContext().getBundles()) {
            MetaTypeInformation info = metaTypeServiceListener.get().getMetaTypeInformation(bundle);
            if (info != null) {
                try {
                    for (var pid : info.getPids()) {
                        ObjectClassDefinition ocd = info.getObjectClassDefinition(pid, null);
                        MetatypeWrapper metatypeWrapper = new MetatypeWrapper(pid, null, ocd);
                        metatypeWrappers.add(metatypeWrapper);
                    }
                    for (var factoryPid : info.getFactoryPids()) {
                        ObjectClassDefinition ocd = info.getObjectClassDefinition(factoryPid, null);
                        MetatypeWrapper metatypeWrapper = new MetatypeWrapper(null, factoryPid, ocd);
                        metatypeWrappers.add(metatypeWrapper);
                    }
                } catch (IllegalArgumentException ex) {

                }
            }
        }

        return metatypeWrappers.toArray(new MetatypeWrapper[metatypeWrappers.size()]);
    }

}
