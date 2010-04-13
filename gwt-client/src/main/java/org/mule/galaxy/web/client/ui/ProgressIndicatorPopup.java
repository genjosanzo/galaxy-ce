package org.mule.galaxy.web.client.ui;

import com.extjs.gxt.ui.client.widget.Popup;
import com.google.gwt.user.client.ui.Image;

import org.mule.galaxy.web.client.ui.panel.WidgetHelper;

/**
 * A 'loading'-style popup with an animated image.
 */
public class ProgressIndicatorPopup extends Popup {

    public ProgressIndicatorPopup() {
        this(null);
    }

    public ProgressIndicatorPopup(String message) {
        if (message != null) {
            add(WidgetHelper.newLabel(message, "progress-indicator-message"));
        }
        Image image = new Image("images/progressbar_indefinite.gif");
        add(image);
        setAnimate(true);
        center();
    }
}
