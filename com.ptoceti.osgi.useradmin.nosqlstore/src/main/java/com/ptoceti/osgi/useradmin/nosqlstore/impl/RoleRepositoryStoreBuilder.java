package com.ptoceti.osgi.useradmin.nosqlstore.impl;

import org.dizitart.no2.Nitrite;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Hashtable;

public class RoleRepositoryStoreBuilder implements ManagedService {

    public static final String DATABASE_STORE_FILEPATH = "com.ptoceti.osgi.useradmin.nosqlstore.nitrite.db.filepath";


    private ServiceRegistration sReg;
    private String nitriteRoleRepositoryDbPath;
    private Nitrite nitriteRoleRepositoryDb;
    private NitriteRoleRepositoryStore nitriteRoleRepositoryStore;


    public RoleRepositoryStoreBuilder() {
        String[] clazzes = new String[]{
                ManagedService.class.getName()
        };
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_PID, this.getClass().getName());
        sReg = Activator.bc.registerService(clazzes, this, properties);

        Activator.getLogger().info("Registered " + this.getClass().getName() + ", Pid = " + (String) properties.get(Constants.SERVICE_PID));
    }


    @Override
    public void updated(Dictionary<String, ?> dictionary) throws ConfigurationException {

        String nitriteDbFilePath = (String) dictionary.get(RoleRepositoryStoreBuilder.DATABASE_STORE_FILEPATH);
        if (nitriteRoleRepositoryDbPath == null || !nitriteRoleRepositoryDbPath.equals(nitriteDbFilePath) || nitriteRoleRepositoryDb == null) {
            if (nitriteRoleRepositoryDb != null) {
                nitriteRoleRepositoryStore.stop();
                nitriteRoleRepositoryDb.close();
            }
            nitriteRoleRepositoryDbPath = nitriteDbFilePath;
            nitriteRoleRepositoryDb = Nitrite.builder().compressed().filePath(nitriteDbFilePath).openOrCreate();
            nitriteRoleRepositoryStore = new NitriteRoleRepositoryStore(nitriteRoleRepositoryDb);
        }

    }

    public void stop() {
        nitriteRoleRepositoryStore.stop();
        nitriteRoleRepositoryDb.close();
    }
}
