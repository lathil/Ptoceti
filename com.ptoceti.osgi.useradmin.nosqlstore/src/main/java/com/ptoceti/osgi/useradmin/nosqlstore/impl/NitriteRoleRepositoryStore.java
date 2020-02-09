package com.ptoceti.osgi.useradmin.nosqlstore.impl;

import org.apache.felix.useradmin.RoleFactory;
import org.apache.felix.useradmin.RoleRepositoryStore;
import org.dizitart.no2.*;
import org.dizitart.no2.filters.Filters;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.useradmin.*;

import java.util.*;

public class NitriteRoleRepositoryStore implements RoleRepositoryStore, UserAdminListener {

    static final String TYPE = "type";
    static final String NAME = "name";

    static final String PROPERTIES = "properties";
    static final String CREDENTIALS = "credentials";
    static final String MEMBERS = "members";
    static final String REQUIRED_MEMBERS = "requiredMembers";


    private ServiceRegistration sReg;
    private Nitrite roleRepositoryDb;
    private NitriteCollection rolesCollection;

    public NitriteRoleRepositoryStore(Nitrite db) {

        roleRepositoryDb = db;

        String[] clazzes = new String[]{
                UserAdminListener.class.getName(),
                RoleRepositoryStore.class.getName()

        };
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_PID, this.getClass().getName());
        sReg = Activator.bc.registerService(clazzes, this, properties);

        Activator.getLogger().info("Registered " + this.getClass().getName() + ", Pid = " + (String) properties.get(Constants.SERVICE_PID));

        if (!db.hasCollection("roles")) {
            rolesCollection = db.getCollection("roles");
            rolesCollection.createIndex(NAME, IndexOptions.indexOptions(IndexType.NonUnique, true));
        } else {
            rolesCollection = db.getCollection("roles");
        }


    }


    public void stop() {
        sReg.unregister();
    }

    @Override
    public Role addRole(String roleName, int type) throws Exception {

        Role role = getRoleByName(roleName);
        if (role != null) {
            return null;
        }

        Document data = serialize(roleName, type);
        WriteResult result = rolesCollection.insert(data);
        if (result.getAffectedCount() > 0) {
            roleRepositoryDb.commit();
            return getRoleByName(roleName);
        }

        return null;
    }

    @Override
    public Role[] getRoles(String filterValue) throws Exception {

        List<Role> roles = new ArrayList<Role>();

        Filter filter = null;
        if (filterValue != null) {
            filter = FrameworkUtil.createFilter(filterValue);
        }

        Cursor cursor = rolesCollection.find();

        for (Document document : cursor) {
            Role role = deserialize(document);
            if ((filter == null) || filter.match(role.getProperties())) {
                roles.add(role);
            }
        }

        return roles.toArray(new Role[roles.size()]);
    }

    @Override
    public Role getRoleByName(String roleName) throws Exception {
        Cursor cursor = rolesCollection.find(Filters.eq(NAME, roleName));
        if (cursor.size() > 0) {
            return deserialize(cursor.firstOrDefault());
        }
        return null;
    }

    @Override
    public Role removeRole(String roleName) throws Exception {
        Role role = getRoleByName(roleName);
        if (role == null) {
            return null;
        }

        WriteResult result = rolesCollection.remove(Filters.eq(NAME, roleName));
        if (result.getAffectedCount() > 0) {
            roleRepositoryDb.commit();
        }

        return role;
    }

    @Override
    public void roleChanged(UserAdminEvent userAdminEvent) {
        if (UserAdminEvent.ROLE_CHANGED == userAdminEvent.getType()) {
            Role changedRole = userAdminEvent.getRole();
            Document update = serialize(changedRole);
            UpdateOptions options = UpdateOptions.updateOptions(false, true);
            WriteResult result = rolesCollection.update(Filters.and(Filters.eq(NAME, changedRole.getName()), Filters.eq(TYPE, changedRole.getType())), update, options);
            if (result.getAffectedCount() > 0) {
                roleRepositoryDb.commit();
            }
        }
    }


    public Role deserialize(Document document) throws Exception {
        int type = ((Integer) document.get(TYPE)).intValue();
        String name = (String) document.get(NAME);

        Role result = RoleFactory.createRole(type, name);
        // Read the generic properties of the role...
        deserializeDictionary(result.getProperties(), (Document) document.get(PROPERTIES));

        if ((Role.GROUP == type) || (Role.USER == type)) {
            // This is safe, as Group extends from User...
            deserializeDictionary(((User) result).getCredentials(), (Document) document.get(CREDENTIALS));

            if (Role.GROUP == type) {
                for (Role member : getRoles((List<String>) document.get(MEMBERS))) {
                    ((Group) result).addMember(member);
                }

                for (Role member : getRoles((List<String>) document.get(REQUIRED_MEMBERS))) {
                    ((Group) result).addRequiredMember(member);
                }
            }
        }

        return result;
    }

    public Document serialize(Role role) {
        Document data = new Document();

        int type = role.getType();

        data.put(TYPE, type);
        data.put(NAME, role.getName());

        data.put(PROPERTIES, serializeDictionary(role.getProperties()));
        if ((Role.GROUP == type) || (Role.USER == type)) {
            data.put(CREDENTIALS, serializeDictionary(((User) role).getCredentials()));

            if (Role.GROUP == type) {
                data.put(MEMBERS, getRoleNames(((Group) role).getMembers()));
                data.put(REQUIRED_MEMBERS, getRoleNames(((Group) role).getRequiredMembers()));
            }
        }

        return data;
    }


    protected Document serialize(String roleName, int type) {
        Document data = new Document();

        data.put(TYPE, type);
        data.put(NAME, roleName);

        return data;
    }

    private void deserializeDictionary(Dictionary dictionary, Document object) {
        // FELIX-4399: MongoDB does return null for empty properties...
        if (object != null) {
            for (String key : object.keySet()) {
                dictionary.put(key, object.get(key));
            }
        }
    }

    private Document serializeDictionary(Dictionary properties) {
        Document result = new Document();

        Enumeration<String> keysEnum = properties.keys();
        while (keysEnum.hasMoreElements()) {
            String key = keysEnum.nextElement();
            Object value = properties.get(key);

            result.put(key, value);
        }

        return result;
    }

    private List<Role> getRoles(List<String> list) throws Exception {
        List<Role> result = new ArrayList<Role>();
        int size = (list == null) ? 0 : list.size();
        for (int i = 0; i < size; i++) {
            final String memberName = (String) list.get(i);
            Role role = findExistingMember(memberName);
            if (role != null) {
                result.add(role);
            }
        }
        return result;
    }

    private List<String> getRoleNames(Role[] members) {
        List<String> result = new ArrayList<String>();
        if (members != null) {
            for (Role member : members) {
                result.add(member.getName());
            }
        }
        return result;
    }

    private Role findExistingMember(String name) throws Exception {
        Role result = getRoleByName(name);

        return result;
    }
}
