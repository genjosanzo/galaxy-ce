package org.mule.galaxy.web.client.ui.help;

import com.google.gwt.i18n.client.Messages;

public interface PanelMessages extends Messages {

    @Key("maxPermSize")
    @DefaultMessage("Entry too long. Max {0} chars")
    String maxPermSize(int maxLength);

    @Key("minPermSize")
    @DefaultMessage("Entry too short. Min {0} chars")
    String minPermSize(int minLength);
    
    @Key("serversError")
    @DefaultMessage("<div>There were errors {0} the selected servers:</div><ul>")
    String serversError(String action);
}
