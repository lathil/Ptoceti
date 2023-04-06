package com.ptoceti.osgi.auth.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import javax.ws.rs.ServiceUnavailableException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class PreferencesWrapper {

    Preferences preferences;
    List<PreferencePropertyEntry> properties;

    public PreferencesWrapper() {

    }

    public PreferencesWrapper(Preferences preferences) throws BackingStoreException {

        this.preferences = preferences;
        List<PreferencePropertyEntry> properties = new ArrayList<PreferencePropertyEntry>();
        for (String key : preferences.keys()) {
            Object value = preferences.get(key, "");
            PreferencePropertyEntry entry = new PreferencePropertyEntry(key, (value instanceof String) ? (String) value : String.valueOf(value));
            properties.add(entry);
        }

        this.setProperties(properties);
    }

    @JsonGetter
    @XmlElementWrapper(name = "properties")
    public List<PreferencePropertyEntry> getProperties() {
        return this.properties;
    }

    public void setProperties(List<PreferencePropertyEntry> properties) {
        this.properties = properties;
    }

    @JsonGetter
    @XmlElementWrapper(name = "name")
    String getName() {
        return preferences.name();
    }

    @JsonGetter
    @XmlElementWrapper(name = "absolutePath")
    String getAbsolutePath() {
        return preferences.absolutePath();
    }

    @JsonGetter
    @XmlElementWrapper(name = "childrenNames")
    @XmlElement(name = "childrenNames")
    public String[] getChildrenNames() {
        try {
            return preferences.childrenNames();
        } catch (BackingStoreException ex) {
            throw new ServiceUnavailableException();
        }
    }

    @JsonGetter
    @XmlElementWrapper(name = "parent")
    @XmlElement(name = "parent")
    public String getParent() {
        return preferences.parent().name();
    }
}
