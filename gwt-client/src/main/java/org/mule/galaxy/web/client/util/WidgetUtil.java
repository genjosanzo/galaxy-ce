package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.ui.Label;

public class WidgetUtil {

    public static Label newSpacer() {
        Label spacer = new Label(" ");
        spacer.setStyleName("spacer");
        return spacer;
    }

    public static Label newSpacerPipe() {
        Label pipe = new Label(" | ");
        pipe.setStyleName("pipe-with-space");
        return pipe;
    }

}

