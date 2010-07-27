package org.mule.galaxy.web.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Image;

public class ClickableImage extends Image {

    public ClickableImage(String url, final String token) {
        super(url);
        setStyleName("clickable");
        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                gotoLocation(token);
            }
        });

    }

    protected void gotoLocation(String token) {
        History.newItem(token);
    }

}
