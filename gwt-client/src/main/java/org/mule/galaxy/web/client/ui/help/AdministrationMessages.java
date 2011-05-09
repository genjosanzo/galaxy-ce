package org.mule.galaxy.web.client.ui.help;


public interface AdministrationMessages extends com.google.gwt.i18n.client.Messages {

  @DefaultMessage("You can filter the activity log by date, by the user who made the changes, and by artifacts containing specific text or of certain types. You can also type the name of a specific artifact in the Relating to box to see activity for just that item. Additionally, you can specify the maximum number of results you want to return. After specifying the filter conditions you want, click <b>Search</b>. To clear the filter and see all activity, click <b>Reset</b>.")
  @Key("activityTip")
  String activityTip();

  @DefaultMessage("Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished setting up this lifecycle.")
  @Key("addLifecycleTip")
  String addLifecycleTip();

  @DefaultMessage("Select the script you want to schedule, enter a unique name and description for this scheduled job, and then enter a cron command (hover over the Cron Command field for cron syntax tips) to specify when the script should be run. To allow this script to be run even if the last execution of the script is still running, click <b>Allow Concurrent Execution</b>.")
  @Key("scheduledItemTip")
  String scheduledItemTip();

  @DefaultMessage("Enter a user name that the user will enter when logging in, as well as the user''s full name, email address, and a password of at least five characters. Select at least one role for this user and click the right arrow (>). Click <b>Save</b> to create the user.")
  @Key("addUserTip")
  String addUserTip();

  @DefaultMessage("Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished editing this lifecycle.")
  @Key("editLifecycleTip")
  String editLifecycleTip();

  @DefaultMessage("Modify the user''s name and email address as needed. To enter a new password for this user, click  <b>Reset Password</b> and enter the password. To add a role, select it in the left list and click the right arrow (>); to remove a role, select it in the right list and click the left arrow button (<). Click <b>Save</b> to save the changes.")
  @Key("editUserTip")
  String editUserTip();

  @DefaultMessage("Display related help topic on www.mulesoft.org")
  @Key("helpLink")
  String helpLink();

  @DefaultMessage("Creates a new lifecycle that you can use to apply policies to artifacts.")
  @Key("lifecyclesNew")
  String lifecyclesNew();

  @DefaultMessage("A <i>lifecycle</i> is a series of phases that you can use to control artifacts managed by Galaxy, such as applying one policy during the Developed phase and another policy during the Production phase. To edit the phases in a lifecycle, click its name. To add a new lifecycle, click <b>Add</b>.")
  @Key("lifecyclesTip")
  String lifecyclesTip();

  @DefaultMessage("Galaxy comes with several predefined policies that you can use to control artifacts during different phases in a lifecycle. Select the lifecycle and phase, select one or more policies to apply during that phase, and then click the right arrow (<b>></b>). Repeat for each phase in which you want to apply policies. If you need to remove a policy from a phase, select the phase, select the policy from the list on the right, and then click the left arrow (<b><</b>). Click <b>Save</b> when you have finished adding policies to phases.")
  @Key("policiesTip")
  String policiesTip();

  @DefaultMessage("Properties provide a structured way of tagging artifacts in the repository. Galaxy provides several predefined properties you can use, or you can create your own by clicking <b>Add</b>.")
  @Key("propertiesTip")
  String propertiesTip();

  @DefaultMessage("You use the scheduler to run scripts automatically on a scheduled basis. To create a new schedule, click <b>New</b>.")
  @Key("schedulerTip")
  String schedulerTip();

  @DefaultMessage("Manage security and options, monitor activity, and create and schedule scripts")
  @Key("tabTip")
  String tabTip();

  @DefaultMessage("Use this screen to administer your Galaxy instance, including setting permissions for users and groups, and monitoring activity on the server.")
  @Key("tip")
  String tip();

  @DefaultMessage("Creates a new user who can log into the application")
  @Key("usersNew")
  String usersNew();

  @DefaultMessage("Users are individuals who can log in to the application. Click <b>New</b> to add a user. When you add users, you assign them roles to determine which permissions they have, such as granting certain users the Administrator role to allow them to manage users.")
  @Key("usersTip")
  String usersTip();

  @DefaultMessage("Loading...")
  @Key("loading")
  String loading();

  @DefaultMessage("User Groups")
  @Key("usersGroup")
  String usersGroup();

  @DefaultMessage("Administrators")
  @Key("administrators")
  String administrators();

  @DefaultMessage("Anonymous")
  @Key("anonymous")
  String anonymous();

  @DefaultMessage("Save")
  @Key("save")
  String save();

  @DefaultMessage("Cancel")
  @Key("cancel")
  String cancel();

  @DefaultMessage("New")
  @Key("newAdmin")
  String newAdmin();

  @DefaultMessage("Permissions Saved") 
  @Key("permissionsSaved")
  String permissionsSaved();

  @DefaultMessage("Users") 
  @Key("users")
  String users();

  @DefaultMessage("Admin Shell") 
  @Key("adminShell")
  String adminShell();

  @DefaultMessage("Scheduler") 
  @Key("scheduler")
  String scheduler();

  @DefaultMessage("Utility") 
  @Key("utility")
  String utility();

  @DefaultMessage("A group with that name already exists") 
  @Key("groupExists")
  String groupExists();
	 
  @DefaultMessage("Group was saved.") 
  @Key("groupSaved")
  String groupSaved();
	
  @DefaultMessage("Group was deleted") 
  @Key("groupDeleted")
  String groupDeleted();

  @DefaultMessage("Add Role") 
  @Key("addRole")
  String addRole();

  @DefaultMessage("Name:") 
  @Key("name")
  String name();

  @DefaultMessage("Edit Role: ") 
  @Key("editRole")
  String editRole();

  @DefaultMessage("Confirm") 
  @Key("confirm")
  String confirm();

  @DefaultMessage("Are you sure you want to delete group  ") 
  @Key("deleteGroup")
  String deleteGroup();

  @DefaultMessage("A Scheduled item with that name already exists.") 
  @Key("scheduledExists")
  String scheduledExists();

  @DefaultMessage("Scheduled item was saved.") 
  @Key("scheduledSaved")
  String scheduledSaved();

  @DefaultMessage("Scheduled item was deleted.") 
  @Key("scheduledDeleted")
  String scheduledDeleted();

  @DefaultMessage("Manage") 
  @Key("manage")
  String manage();

  @DefaultMessage("Username") 
  @Key("username")
  String username();

  @DefaultMessage("Name") 
  @Key("userName")
  String userName();

  @DefaultMessage("Email Address") 
  @Key("emailAddress")
  String emailAddress();

  @DefaultMessage("New") 
  @Key("newUser")
  String newUser();

  @DefaultMessage("Search") 
  @Key("search")
  String search();

  @DefaultMessage("Username:") 
  @Key("usernameForm")
  String usernameForm();

  @DefaultMessage("Email:") 
  @Key("email")
  String email();

  @DefaultMessage("Password:") 
  @Key("password")	
  String password();

  @DefaultMessage("Confirm Password:") 
  @Key("confirmPassword")
  String confirmPassword();

  @DefaultMessage("Groups:") 
  @Key("groups")
  String groups();

  @DefaultMessage("User was saved.")
  @Key("userSaved")
  String userSaved();

  @DefaultMessage("User was deleted.")
  @Key("userDeleted")
  String userDeleted();

  @DefaultMessage("A user with that username already exists.")
  @Key("userExists")
  String userExists();

  @DefaultMessage("Cannot contain ''/''")
  @Key("cannotContain")
  String cannotContain();

  @DefaultMessage("This field is required")
  @Key("fieldRequired")
  String fieldRequired();

  @DefaultMessage(" Reset Password ")
  @Key("resetPassword")
  String resetPassword();

  @DefaultMessage("Loading Groups...")
  @Key("loadingGroups")
  String loadingGroups();
  
  @DefaultMessage("Must be at least {0} characters in length")
  @Key("charactersLength")
  String charactersLength(int pass);

  @DefaultMessage("Add User: ")
  @Key("addUser")
  String addUser();

  @DefaultMessage("Edit User ")
  @Key("editUser")
  String editUser();

  @DefaultMessage("Available Groups")
  @Key("availableGroups")
  String availableGroups();

  @DefaultMessage("Joined Groups")
  @Key("joinedGroups")
  String joinedGroups();

  @DefaultMessage("Passwords must match")
  @Key("passwordsMatch")
  String passwordsMatch();

  @DefaultMessage("User must be a member of at least one group")
  @Key("userWarning")
  String userWarning();

  @DefaultMessage(" user must be a member of the {0} group")
  @Key("userMemberOf")
  String userMemberOf(String group);

  @DefaultMessage("New Password")
  @Key("newPassword")
  String newPassword();

  @DefaultMessage("Confirm Password")
  @Key("confirmPasswordField")
  String confirmPasswordField();

  @DefaultMessage("Are you sure you want to delete user ")
  @Key("deleteUser")
  String deleteUser();

  @DefaultMessage("Reset&nbsp;Password")
  @Key("resetPasswordButton")
  String resetPasswordButton();

  @DefaultMessage("Nothing Selected")
  @Key("nothingSelected")
  String nothingSelected();

  @DefaultMessage("Please select a script to delete")
  @Key("selectTip")
  String selectTip();

  @DefaultMessage("Are you sure you want to delete the script \"")
  @Key("deleteScript")
  String deleteScript();

  @DefaultMessage("Script execution failure: ")
  @Key("scriptFailure")
  String scriptFailure();
  
  @DefaultMessage("The script did not return a value")
  @Key("scriptValue")
  String scriptValue();

  @DefaultMessage("Delete")
  @Key("delete")
  String delete();

  @DefaultMessage("Reset")
  @Key("reset")
  String reset();

  @DefaultMessage("Evaluate")
  @Key("evaluate")
  String evaluate();

  @DefaultMessage(" Save As... ")
  @Key("saveAs")
  String saveAs();

  @DefaultMessage(" Run on startup ")
  @Key("runOnStartup")
  String runOnStartup();
  
  @DefaultMessage("Type or paste a Groovy script to be executed on the server. A return value will be displayed below the area. ")
  @Key("groovyScriptTip")
  String groovyScriptTip();

  @DefaultMessage("Tips:<br>&nbsp;&nbsp;Spring''s context is available as an ''applicationContext'' variable <br>&nbsp;&nbsp;Logger (commons-logging) is available as a ''log'' variable <br>&nbsp;&nbsp;Only String return values are supported (or null).")
  @Key("scriptTip")
  String scriptTip();

  @DefaultMessage("Saved Scripts")
  @Key("saveScripts")
  String saveScripts();
  
  @DefaultMessage("Check ''Save As'' if saving for the first time.")
  @Key("checkSaveAs")
  String checkSaveAs();
  
  @DefaultMessage("Script {0} has been saved")
  @Key("scriptSaved")
  String scriptSaved(String name);
  
  @DefaultMessage("Script {0} has been deleted")
  @Key("scriptDeleted")
  String scriptDeleted(String name);

  @DefaultMessage("Script:")
  @Key("script")
  String script();

  @DefaultMessage("Description:")
  @Key("description")
  String description();

  @DefaultMessage("Cron Command:")
  @Key("cronCommand")
  String cronCommand();

  @DefaultMessage("Allow Concurrent Execution:")
  @Key("allowConcurrentExecution")
  String allowConcurrentExecution();

  @DefaultMessage("Cron Help")
  @Key("cronHelp")
  String cronHelp();

  @DefaultMessage("Add")
  @Key("add")
  String add();

  @DefaultMessage(" Scheduled Item")
  @Key("scheduledItem")
  String scheduledItem();

  @DefaultMessage("Edit")
  @Key("edit")
  String edit();

  @DefaultMessage("Are you sure you want to delete schedule ")
  @Key("deleteSchedule")
  String deleteSchedule();

  @DefaultMessage("Scheduled Jobs")
  @Key("scheduledJobs")
  String scheduledJobs();

  @DefaultMessage("Name")
  @Key("scheduleName")
  String scheduleName();

  @DefaultMessage("Cron Expression")
  @Key("cronExpression")
  String cronExpression();

  @DefaultMessage("Field Name")
  @Key("fieldName")
  String fieldName();

  @DefaultMessage("Mandatory")
  @Key("mandatory")
  String mandatory();

  @DefaultMessage("Allowed Values")
  @Key("allowedValues")
  String allowedValues();

  @DefaultMessage("Allowed Special Characters")
  @Key("allowedSpecialCharacters")
  String allowedSpecialCharacters();

  @DefaultMessage("Seconds")
  @Key("seconds")
  String seconds();

  @DefaultMessage("YES")
  @Key("yes")
  String yes();
  
  @DefaultMessage("Minutes")
  @Key("minutes")
  String minutes();

  @DefaultMessage("Hours")
  @Key("hours")
  String hours();

  @DefaultMessage("Day of Month")
  @Key("dayOfMonth")
  String dayOfMonth();

  @DefaultMessage("Month")
  @Key("month")
  String month();
  
  @DefaultMessage("Day of Week")
  @Key("dayOfWeek")
  String dayOfWeek();

  @DefaultMessage("Year")
  @Key("year")
  String year();
  
 
}
