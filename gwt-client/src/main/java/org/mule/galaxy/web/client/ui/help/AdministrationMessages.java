package org.mule.galaxy.web.client.ui.help;

import com.google.gwt.i18n.client.Messages;

public interface AdministrationMessages extends Messages {
    @DefaultMessage("Must be at least {0} characters in length")
    @Key("charactersLength")
    String charactersLength(int pass);
    
    @DefaultMessage(" user must be a member of the {0} group")
    @Key("userMemberOf")
    String userMemberOf(String group);
    
    
    @DefaultMessage("Script {0} has been saved")
    @Key("scriptSaved")
    String scriptSaved(String name);
    
    
    @DefaultMessage("Script {0} has been deleted")
    @Key("scriptDeleted")
    String scriptDeleted(String name);
}
