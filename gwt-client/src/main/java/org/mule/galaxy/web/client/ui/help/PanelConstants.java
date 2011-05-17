package org.mule.galaxy.web.client.ui.help;

public interface PanelConstants extends com.google.gwt.i18n.client.Constants {

    @Key("less")
    @DefaultStringValue(" [less]")
    String less();

    @Key("more")
    @DefaultStringValue(" [more]")
    String more();

    @Key("collapse")
    @DefaultStringValue("Collapse")
    String collapse();

    @Key("expand")
    @DefaultStringValue("Expand")
    String expand();

    @Key("actionCanceled")
    @DefaultStringValue("Action Canceled")
    String actionCanceled();

    @Key("save")
    @DefaultStringValue("Save")
    String save();

    @Key("delete")
    @DefaultStringValue("Delete")
    String delete();

    @Key("cancel")
    @DefaultStringValue("Cancel")
    String cancel();

    @Key("deleting")
    @DefaultStringValue("Deleting...")
    String deleting();

    @Key("saving")
    @DefaultStringValue("Saving...")
    String saving();

    @Key("canceling")
    @DefaultStringValue("Canceling...")
    String canceling();

    @Key("editServerName")
    @DefaultStringValue("Edit Server Name")
    String editServerName();

    @Key("add")
    @DefaultStringValue("Add")
    String add();

    @Key("none")
    @DefaultStringValue("None")
    String none();

    @Key("restore")
    @DefaultStringValue("Restore")
    String restore();

    @Key("removeThisItem")
    @DefaultStringValue("Remove This Item")
    String removeThisItem();

    @Key("serverTakingLonger")
    @DefaultStringValue("Server is taking longer to respond than normal.")
    String serverTakingLonger();

    @Key("currentSession")
    @DefaultStringValue("Current session has been killed, please re-login.")
    String currentSession();

    @Key("errorCommunicatingServer")
    @DefaultStringValue("Error communicating with server: ")
    String errorCommunicatingServer();

    @Key("errorCommunicatingExeption")
    @DefaultStringValue("There was an error communicating with the server. Please try again. <br />Exception: ")
    String errorCommunicatingExeption();

    @Key("digitsOnly")
    @DefaultStringValue("Must be digits only")
    String digitsOnly();

    @Key("validEmailAddress")
    @DefaultStringValue("Is not a valid Email Address")
    String validEmailAddress();

    @Key("fieldRequired")
    @DefaultStringValue("This field is required")
    String fieldRequired();

    @Key("selectValue")
    @DefaultStringValue("At least one value must be selected.")
    String selectValue();

    @Key("urlMalformed")
    @DefaultStringValue("The Url is malformed")
    String urlMalformed();

    @Key("fieldNotEmpty")
    @DefaultStringValue("The field can not be empty")
    String fieldNotEmpty();

    @Key("notMatchRegex")
    @DefaultStringValue("Does not match regex: ")
    String notMatchRegex();

}
