package org.mule.galaxy.web.client.util;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FormPanel;

import java.util.Map;

/**
 * encapsulate the addItem logic in this class and
 * leave the rendering in the panels where they belong...
 * <p/>
 * helper.setType(AddItemHelper.Type.ARTIFACT);
 * helper.setAddVersion(true); //  this would tell it to submit the artifact version too
 * helper.setName("hello.war");
 * helper.setVersion("1.0");
 * helper.setProperties(mapOfProperties);
 */
public class AddItemHelper extends FormPanel {

    public static enum Type {
        ARTIFACT, ARTIFACT_VERSION, VERSION, VERSION_TYPE
    }

    private String type;
    private boolean addVersion;
    private String name;
    private String version;
    private Map properties;


    public AddItemHelper(String action) {
        this.setAction(action);
        this.setEncoding(FormPanel.ENCODING_MULTIPART);
        this.setMethod(FormPanel.METHOD_POST);
    }

    public String getType() {
        return type;
    }                                   

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAddVersion() {
        return addVersion;
    }

    public void setAddVersion(boolean addVersion) {
        this.addVersion = addVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }


}
