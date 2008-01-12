package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Label;

public interface ErrorPanel {
    void setMessage(Label label);
    void setMessage(String string);
}
