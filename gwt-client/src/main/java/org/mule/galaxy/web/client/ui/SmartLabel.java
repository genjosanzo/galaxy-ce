package org.mule.galaxy.web.client.ui;

import com.google.gwt.user.client.ui.Label;

// auto convert 
public class SmartLabel extends Label {

    public SmartLabel() {
    }

    public SmartLabel(String text) {
        super(text);
    }

    public SmartLabel(Long l) {
        super(Long.toString(l));
    }

    public SmartLabel(Double d) {
        super(Double.toString(d));
    }

    public SmartLabel(Float f) {
        super(Float.toString(f));
    }

    public SmartLabel(Integer i) {
        super(Integer.toString(i));
    }


    public void setText(Long l) {
        super.setText(Long.toString(l));
    }

    public void setText(Integer i) {
        super.setText(Integer.toString(i));
    }

    public void setText(Double d) {
        super.setText(Double.toString(d));
    }

    public void setText(Float f) {
        super.setText(Float.toString(f));
    }

}
