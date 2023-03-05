package com.ptoceti.osgi.deviceadmin.impl;

import com.ptoceti.osgi.deviceadmin.DeviceFactoryInfo;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DevicefactoryInfoReader {

    public static final String FACTORIES_META_TAG_NAME = "Factories";
    public static final String FACTORY_META_TAG_NAME = "FactoryInfo";
    private KXmlParser parser = new KXmlParser();

    public List<DeviceFactoryInfo> parse(URL url) throws IOException {

        InputStream stream = url.openStream();
        List<DeviceFactoryInfo> factoriesMeta = null;
        try {
            factoriesMeta = parse(stream);
        } catch (IOException ex) {
            Activator.getLogger().error("Error reading device factory file at: " + url.getPath(), ex, null);
        } catch (XmlPullParserException ex) {
            Activator.getLogger().error("Error parsing device factoory info at: " + url.getPath(), ex, null);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return factoriesMeta;
    }

    protected List<DeviceFactoryInfo> parse(InputStream inputStream) throws IOException, XmlPullParserException {

        List<DeviceFactoryInfo> results = new ArrayList<DeviceFactoryInfo>();
        ;

        // We set to null the encoding type. The parser should then dected it from the file stream.
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals(FACTORIES_META_TAG_NAME)) {
                    // We move to the next element inside the Wires element
                    parser.next();
                    results.addAll(parseFactories(parser));
                } else {
                    parser.skipSubTree();
                }
            }
            eventType = parser.next();
        }

        return results;

    }


    private List<DeviceFactoryInfo> parseFactories(KXmlParser parser) throws XmlPullParserException, IOException {
        List<DeviceFactoryInfo> results = new ArrayList<DeviceFactoryInfo>();

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals(FACTORY_META_TAG_NAME)) {
                    // We move to the next element inside the Wires element
                    results.add(parseFactory(parser));
                } else {
                    parser.skipSubTree();
                }
            }
            eventType = parser.next();
        }

        return results;
    }

    private DeviceFactoryInfo parseFactory(KXmlParser parser) throws XmlPullParserException, IOException {

        DeviceFactoryInfo meta = new DeviceFactoryInfo();
        String description = parser.getAttributeValue(null, "description");
        if (description == null) throw missingAttribute("description");
        String pid = parser.getAttributeValue(null, "pid");
        if (pid == null) throw missingAttribute("pid");
        String isFactory = parser.getAttributeValue(null, "isFactory");
        if (isFactory == null) throw missingAttribute("isFactory");
        String type = parser.getAttributeValue(null, "type");
        if (type == null) throw missingAttribute("type");

        meta.setDescription(description);
        meta.setPid(pid);
        meta.setIsFactory(Boolean.valueOf(isFactory).booleanValue());
        meta.setType(DeviceFactoryInfo.Type.getTypeByLabel(type));

        int eventType;
        do {
            eventType = parser.next();
        } while (eventType != XmlPullParser.END_TAG);


        return meta;
    }

    private XmlPullParserException missingAttribute(String attributeName) {
        return new XmlPullParserException("Missing attribute " + attributeName, this.parser, null);
    }
}
