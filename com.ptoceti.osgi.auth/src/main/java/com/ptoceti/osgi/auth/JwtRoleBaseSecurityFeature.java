package com.ptoceti.osgi.auth;



import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;

@Provider
public class JwtRoleBaseSecurityFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {

        Class resourceClass = resourceInfo.getResourceClass();
        Method resourceMethod = resourceInfo.getResourceMethod();

        RolesAllowed classAllowed = (RolesAllowed) resourceClass.getAnnotation(RolesAllowed.class);
        RolesAllowed methodAllowed = resourceMethod.getAnnotation(RolesAllowed.class);

        String[] rolesAllowed = null;
        if (methodAllowed != null) {
            rolesAllowed = methodAllowed.value();
        } else {
            if (classAllowed != null) {
                rolesAllowed = classAllowed.value();
            }
        }

        boolean denyAll = (resourceClass.isAnnotationPresent(DenyAll.class)
                && resourceMethod.isAnnotationPresent(RolesAllowed.class) == false
                && resourceMethod.isAnnotationPresent(PermitAll.class) == false) || resourceMethod.isAnnotationPresent(DenyAll.class);

        boolean permitAll = (resourceClass.isAnnotationPresent(PermitAll.class) == true
                && resourceMethod.isAnnotationPresent(RolesAllowed.class) == false
                && resourceMethod.isAnnotationPresent(DenyAll.class) == false) || resourceMethod.isAnnotationPresent(PermitAll.class);

        if (rolesAllowed != null || denyAll || permitAll) {
            JwtRoleBaseSecurityFilter filter = new JwtRoleBaseSecurityFilter(rolesAllowed, denyAll, permitAll);
            context.register(filter);
        }
    }
}
