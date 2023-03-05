package com.ptoceti.osgi.rest.impl.application;


import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.FunctionServiceTracker;
import com.ptoceti.osgi.rest.impl.application.model.FunctionDataWrapper;
import com.ptoceti.osgi.rest.impl.application.model.FunctionPropertyDataWrapper;
import com.ptoceti.osgi.rest.impl.application.model.ItemWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Path("items")
@Tags({@Tag(name = "items")})
@Secured
public class ItemsResource {

    @Inject
    FunctionServiceTracker functionServiceTracker;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<ItemWrapper> getItems() {
        ArrayList<ItemWrapper> result = new ArrayList<ItemWrapper>();

        ServiceReference[] dalFuncRefs = functionServiceTracker.getServiceReferences();
        if (dalFuncRefs != null) {
            for (ServiceReference sRef : dalFuncRefs) {
                result.add(new ItemWrapper(sRef));
            }
        }
        return result;

    }

    @GET
    @Path("{uid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ItemWrapper getItem(@PathParam("uid") String uid) {
        ItemWrapper result = null;
        ServiceReference[] dalFuncRefs = functionServiceTracker.getServiceReferences();
        if (dalFuncRefs != null) {
            for (ServiceReference sRef : dalFuncRefs) {
                if (sRef.getProperty(Function.SERVICE_UID).equals(uid)) {
                    result = new ItemWrapper(sRef);
                }
            }
        }

        return result;
    }

    @GET
    @Path("{uid}/properties")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<FunctionPropertyDataWrapper> getPropertiesValues(@PathParam("uid") String uid) {
        List<FunctionPropertyDataWrapper> result = new ArrayList();
        ServiceReference[] dalFuncRefs = functionServiceTracker.getServiceReferences();
        if (dalFuncRefs != null) {
            for (ServiceReference sRef : dalFuncRefs) {
                if (sRef.getProperty(Function.SERVICE_UID).equals(uid)) {
                    Function function = (Function) Activator.getBundleContext().getService(sRef);
                    String[] propertyNames = (String[]) function.getServiceProperty(Function.SERVICE_PROPERTY_NAMES);
                    Arrays.stream(propertyNames).forEach(propertyName -> {
                        try {
                            Method method = function.getClass().getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length()), null);
                            FunctionData functionData = (FunctionData) method.invoke(function);
                            result.add(new FunctionPropertyDataWrapper(propertyName, new FunctionDataWrapper(functionData)));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {

                        }
                    });

                }
            }
        }
        return result;
    }
}
