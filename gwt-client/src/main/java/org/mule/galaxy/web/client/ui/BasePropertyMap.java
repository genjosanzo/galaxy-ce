package org.mule.galaxy.web.client.ui;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class BasePropertyMap implements Serializable, BeanModelTag {

    private String name;
    private String value;

    // optional but useful for grouping grids  -- ie,  mule, system, etc
    private String type;
    
    public BasePropertyMap() {
    }

    public BasePropertyMap(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public BasePropertyMap(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
