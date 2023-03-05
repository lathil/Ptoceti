package com.ptoceti.osgi.deviceadmin;

public class DeviceFactoryInfo {

    protected String description;
    protected String pid;
    protected boolean isFactory;
    private Type type;


    public enum Type {
        DAL(1, "Dal"),
        DEVICE(2, "Device"),
        DRIVER(3, "Driver"),
        FUNCTION(4, "Function");

        int code;
        String label;
        private int type;

        private Type(int code, String label) {
            this.code = code;
            this.label = label;
        }

        public static Type getTypeByCode(int code) {
            for (Type e : Type.values()) {
                if (code == e.code) return e;
            }
            return null;
        }

        public static Type getTypeByLabel(String label) {
            for (Type e : Type.values()) {
                if (label.equals(e.label)) return e;
            }
            return null;
        }

        public int getCode() {
            return this.code;
        }

        public String getLabel() {
            return this.label;
        }

    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isFactory() {
        return isFactory;
    }

    public void setIsFactory(boolean factory) {
        isFactory = factory;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
