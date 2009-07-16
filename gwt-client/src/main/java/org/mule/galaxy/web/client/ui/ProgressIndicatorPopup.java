package org.mule.galaxy.web.client.ui;

import com.extjs.gxt.ui.client.widget.Popup;
import com.google.gwt.user.client.ui.Image;

/**
 * A 'loading'-style popup with an animated image.
 */
public class ProgressIndicatorPopup extends Popup {

    public ProgressIndicatorPopup() {
        Image image = new Image("images/progressbar_indefinite.gif");
        add(image);
        setAnimate(true);
        center();
    }
}
