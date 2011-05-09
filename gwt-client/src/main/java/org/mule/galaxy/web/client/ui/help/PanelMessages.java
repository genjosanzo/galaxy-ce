package org.mule.galaxy.web.client.ui.help;

public interface PanelMessages extends com.google.gwt.i18n.client.Messages {

	@Key("less")
	@DefaultMessage(" [less]")
	String less();
	
	@Key("more")
	@DefaultMessage(" [more]")
	String more();
	
	@Key("collapse")
	@DefaultMessage("Collapse")
	String collapse();
	
	@Key("expand")
	@DefaultMessage("Expand")
	String expand();

	@Key("actionCanceled")
	@DefaultMessage("Action Canceled")
	String actionCanceled();

	@Key("save")
	@DefaultMessage("Save")
	String save();

	@Key("delete")
	@DefaultMessage("Delete")
	String delete();

	@Key("cancel")
	@DefaultMessage("Cancel")
	String cancel();

	@Key("deleting")
	@DefaultMessage("Deleting...")
	String deleting();

	@Key("saving")
	@DefaultMessage("Saving...")
	String saving();

	@Key("canceling")
	@DefaultMessage("Canceling...")
	String canceling();

	@Key("editServerName")
	@DefaultMessage("Edit Server Name")
	String editServerName();

	@Key("add")
	@DefaultMessage("Add")
	String add();

	@Key("none")
	@DefaultMessage("None")
	String none();

	@Key("restore")
	@DefaultMessage("Restore")
	String restore();

	@Key("removeThisItem")
	@DefaultMessage("Remove This Item")
	String removeThisItem();

	@Key("serverTakingLonger")
	@DefaultMessage("Server is taking longer to respond than normal.")
	String serverTakingLonger();

	@Key("currentSession")
	@DefaultMessage("Current session has been killed, please re-login.")
	String currentSession();

	@Key("errorCommunicatingServer")
	@DefaultMessage("Error communicating with server: ")
	String errorCommunicatingServer();

	@Key("errorCommunicatingExeption")
	@DefaultMessage("There was an error communicating with the server. Please try again. <br />Exception: ")
	String errorCommunicatingExeption();

	@Key("digitsOnly")
	@DefaultMessage("Must be digits only")
	String digitsOnly();

	@Key("validEmailAddress")
	@DefaultMessage("Is not a valid Email Address")
	String validEmailAddress();

	@Key("fieldRequired")
	@DefaultMessage("This field is required")
	String fieldRequired();

	@Key("selectValue")
	@DefaultMessage("At least one value must be selected.")
	String selectValue();

	@Key("maxPermSize")
	@DefaultMessage("Entry too long. Max {0} chars")
	String maxPermSize(int maxLength);

	@Key("minPermSize")
	@DefaultMessage("Entry too short. Min {0} chars")
	String minPermSize(int minLength);

	@Key("urlMalformed")
	@DefaultMessage("The Url is malformed")
	String urlMalformed();

	@Key("fieldNotEmpty")
	@DefaultMessage("The field can not be empty")
	String fieldNotEmpty();

	@Key("notMatchRegex")
	@DefaultMessage("Does not match regex: ")
	String notMatchRegex();
	
	@Key("serversError")
	@DefaultMessage("<div>There were errors {0} the selected servers:</div><ul>")
	String serversError(String action);
	
}
