package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class NavigationUtil {

    public static ClickListener createNavigatingClickListener(final String token) {
        return new ClickListener() {
            public void onClick(Widget w) {
                History.newItem(token);
            }
        };
    }

}
