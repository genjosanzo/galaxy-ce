package org.mule.galaxy.web.client.ui.help;

public interface AdministrationConstants extends com.google.gwt.i18n.client.Constants {

    @DefaultStringValue("You can filter the activity log by date, by the user who made the changes, and by artifacts containing specific text or of certain types. You can also type the name of a specific artifact in the Relating to box to see activity for just that item. Additionally, you can specify the maximum number of results you want to return. After specifying the filter conditions you want, click <b>Search</b>. To clear the filter and see all activity, click <b>Reset</b>.")
    @Key("activityTip")
    String activityTip();

    @DefaultStringValue("Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished setting up this lifecycle.")
    @Key("addLifecycleTip")
    String addLifecycleTip();

    @DefaultStringValue("Select the script you want to schedule, enter a unique name and description for this scheduled job, and then enter a cron command (hover over the Cron Command field for cron syntax tips) to specify when the script should be run. To allow this script to be run even if the last execution of the script is still running, click <b>Allow Concurrent Execution</b>.")
    @Key("scheduledItemTip")
    String scheduledItemTip();

    @DefaultStringValue("Enter a user name that the user will enter when logging in, as well as the user's full name, email address, and a password of at least five characters. Select at least one role for this user and click the right arrow (>). Click <b>Save</b> to create the user.")
    @Key("addUserTip")
    String addUserTip();

    @DefaultStringValue("Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished editing this lifecycle.")
    @Key("editLifecycleTip")
    String editLifecycleTip();

    @DefaultStringValue("Modify the user's name and email address as needed. To enter a new password for this user, click  <b>Reset Password</b> and enter the password. To add a role, select it in the left list and click the right arrow (>); to remove a role, select it in the right list and click the left arrow button (<). Click <b>Save</b> to save the changes.")
    @Key("editUserTip")
    String editUserTip();

    @DefaultStringValue("Display related help topic on www.mulesoft.org")
    @Key("helpLink")
    String helpLink();

    @DefaultStringValue("Creates a new lifecycle that you can use to apply policies to artifacts.")
    @Key("lifecyclesNew")
    String lifecyclesNew();

    @DefaultStringValue("A <i>lifecycle</i> is a series of phases that you can use to control artifacts managed by Galaxy, such as applying one policy during the Developed phase and another policy during the Production phase. To edit the phases in a lifecycle, click its name. To add a new lifecycle, click <b>Add</b>.")
    @Key("lifecyclesTip")
    String lifecyclesTip();

    @DefaultStringValue("Galaxy comes with several predefined policies that you can use to control artifacts during different phases in a lifecycle. Select the lifecycle and phase, select one or more policies to apply during that phase, and then click the right arrow (<b>></b>). Repeat for each phase in which you want to apply policies. If you need to remove a policy from a phase, select the phase, select the policy from the list on the right, and then click the left arrow (<b><</b>). Click <b>Save</b> when you have finished adding policies to phases.")
    @Key("policiesTip")
    String policiesTip();

    @DefaultStringValue("Properties provide a structured way of tagging artifacts in the repository. Galaxy provides several predefined properties you can use, or you can create your own by clicking <b>Add</b>.")
    @Key("propertiesTip")
    String propertiesTip();

    @DefaultStringValue("You use the scheduler to run scripts automatically on a scheduled basis. To create a new schedule, click <b>New</b>.")
    @Key("schedulerTip")
    String schedulerTip();

    @DefaultStringValue("Manage security and options, monitor activity, and create and schedule scripts")
    @Key("tabTip")
    String tabTip();

    @DefaultStringValue("Use this screen to administer your Galaxy instance, including setting permissions for users and groups, and monitoring activity on the server.")
    @Key("tip")
    String tip();

    @DefaultStringValue("Creates a new user who can log into the application")
    @Key("usersNew")
    String usersNew();

    @DefaultStringValue("Users are individuals who can log in to the application. Click <b>New</b> to add a user. When you add users, you assign them roles to determine which permissions they have, such as granting certain users the Administrator role to allow them to manage users.")
    @Key("usersTip")
    String usersTip();

    @DefaultStringValue("Loading...")
    @Key("loading")
    String loading();

    @DefaultStringValue("User Groups")
    @Key("usersGroup")
    String usersGroup();

    @DefaultStringValue("Administrators")
    @Key("administrators")
    String administrators();

    @DefaultStringValue("Anonymous")
    @Key("anonymous")
    String anonymous();

    @DefaultStringValue("Save")
    @Key("save")
    String save();

    @DefaultStringValue("Cancel")
    @Key("cancel")
    String cancel();

    @DefaultStringValue("New")
    @Key("newAdmin")
    String newAdmin();

    @DefaultStringValue("Permissions Saved")
    @Key("permissionsSaved")
    String permissionsSaved();

    @DefaultStringValue("Users")
    @Key("users")
    String users();

    @DefaultStringValue("Admin Shell")
    @Key("adminShell")
    String adminShell();

    @DefaultStringValue("Scheduler")
    @Key("scheduler")
    String scheduler();

    @DefaultStringValue("Utility")
    @Key("utility")
    String utility();

    @DefaultStringValue("A group with that name already exists")
    @Key("groupExists")
    String groupExists();

    @DefaultStringValue("Group was saved.")
    @Key("groupSaved")
    String groupSaved();

    @DefaultStringValue("Group was deleted")
    @Key("groupDeleted")
    String groupDeleted();

    @DefaultStringValue("Add Role")
    @Key("addRole")
    String addRole();

    @DefaultStringValue("Name:")
    @Key("name")
    String name();

    @DefaultStringValue("Edit Role: ")
    @Key("editRole")
    String editRole();

    @DefaultStringValue("Confirm")
    @Key("confirm")
    String confirm();

    @DefaultStringValue("Are you sure you want to delete group  ")
    @Key("deleteGroup")
    String deleteGroup();

    @DefaultStringValue("A Scheduled item with that name already exists.")
    @Key("scheduledExists")
    String scheduledExists();

    @DefaultStringValue("Scheduled item was saved.")
    @Key("scheduledSaved")
    String scheduledSaved();

    @DefaultStringValue("Scheduled item was deleted.")
    @Key("scheduledDeleted")
    String scheduledDeleted();

    @DefaultStringValue("Manage")
    @Key("manage")
    String manage();

    @DefaultStringValue("Username")
    @Key("username")
    String username();

    @DefaultStringValue("Name")
    @Key("userName")
    String userName();

    @DefaultStringValue("Email Address")
    @Key("emailAddress")
    String emailAddress();

    @DefaultStringValue("New")
    @Key("newUser")
    String newUser();

    @DefaultStringValue("Search")
    @Key("search")
    String search();

    @DefaultStringValue("Username:")
    @Key("usernameForm")
    String usernameForm();

    @DefaultStringValue("Email:")
    @Key("email")
    String email();

    @DefaultStringValue("Password:")
    @Key("password")
    String password();

    @DefaultStringValue("Confirm Password:")
    @Key("confirmPassword")
    String confirmPassword();

    @DefaultStringValue("Groups:")
    @Key("groups")
    String groups();

    @DefaultStringValue("User was saved.")
    @Key("userSaved")
    String userSaved();

    @DefaultStringValue("User was deleted.")
    @Key("userDeleted")
    String userDeleted();

    @DefaultStringValue("A user with that username already exists.")
    @Key("userExists")
    String userExists();

    @DefaultStringValue("Cannot contain '/'")
    @Key("cannotContain")
    String cannotContain();

    @DefaultStringValue("This field is required")
    @Key("fieldRequired")
    String fieldRequired();

    @DefaultStringValue(" Reset Password ")
    @Key("resetPassword")
    String resetPassword();

    @DefaultStringValue("Loading Groups...")
    @Key("loadingGroups")
    String loadingGroups();

    @DefaultStringValue("Add User: ")
    @Key("addUser")
    String addUser();

    @DefaultStringValue("Edit User ")
    @Key("editUser")
    String editUser();

    @DefaultStringValue("Available Groups")
    @Key("availableGroups")
    String availableGroups();

    @DefaultStringValue("Joined Groups")
    @Key("joinedGroups")
    String joinedGroups();

    @DefaultStringValue("Passwords must match")
    @Key("passwordsMatch")
    String passwordsMatch();

    @DefaultStringValue("User must be a member of at least one group")
    @Key("userWarning")
    String userWarning();

    @DefaultStringValue("New Password")
    @Key("newPassword")
    String newPassword();

    @DefaultStringValue("Confirm Password")
    @Key("confirmPasswordField")
    String confirmPasswordField();

    @DefaultStringValue("Are you sure you want to delete user ")
    @Key("deleteUser")
    String deleteUser();

    @DefaultStringValue("Reset&nbsp;Password")
    @Key("resetPasswordButton")
    String resetPasswordButton();

    @DefaultStringValue("Nothing Selected")
    @Key("nothingSelected")
    String nothingSelected();

    @DefaultStringValue("Please select a script to delete")
    @Key("selectTip")
    String selectTip();

    @DefaultStringValue("Are you sure you want to delete the script \"")
    @Key("deleteScript")
    String deleteScript();

    @DefaultStringValue("Script execution failure: ")
    @Key("scriptFailure")
    String scriptFailure();

    @DefaultStringValue("The script did not return a value")
    @Key("scriptValue")
    String scriptValue();

    @DefaultStringValue("Delete")
    @Key("delete")
    String delete();

    @DefaultStringValue("Reset")
    @Key("reset")
    String reset();

    @DefaultStringValue("Evaluate")
    @Key("evaluate")
    String evaluate();

    @DefaultStringValue(" Save As... ")
    @Key("saveAs")
    String saveAs();

    @DefaultStringValue(" Run on startup ")
    @Key("runOnStartup")
    String runOnStartup();

    @DefaultStringValue("Type or paste a Groovy script to be executed on the server. A return value will be displayed below the area. ")
    @Key("groovyScriptTip")
    String groovyScriptTip();

    @DefaultStringValue("Tips:<br>&nbsp;&nbsp;Spring's context is available as an 'applicationContext' variable <br>&nbsp;&nbsp;Logger (commons-logging) is available as a 'log' variable <br>&nbsp;&nbsp;Only String return values are supported (or null).")
    @Key("scriptTip")
    String scriptTip();

    @DefaultStringValue("Saved Scripts")
    @Key("saveScripts")
    String saveScripts();

    @DefaultStringValue("Check 'Save As' if saving for the first time.")
    @Key("checkSaveAs")
    String checkSaveAs();

    @DefaultStringValue("Script:")
    @Key("script")
    String script();

    @DefaultStringValue("Description:")
    @Key("description")
    String description();

    @DefaultStringValue("Cron Command:")
    @Key("cronCommand")
    String cronCommand();

    @DefaultStringValue("Allow Concurrent Execution:")
    @Key("allowConcurrentExecution")
    String allowConcurrentExecution();

    @DefaultStringValue("Cron Help")
    @Key("cronHelp")
    String cronHelp();

    @DefaultStringValue("Add")
    @Key("add")
    String add();

    @DefaultStringValue(" Scheduled Item")
    @Key("scheduledItem")
    String scheduledItem();

    @DefaultStringValue("Edit")
    @Key("edit")
    String edit();

    @DefaultStringValue("Are you sure you want to delete schedule ")
    @Key("deleteSchedule")
    String deleteSchedule();

    @DefaultStringValue("Scheduled Jobs")
    @Key("scheduledJobs")
    String scheduledJobs();

    @DefaultStringValue("Name")
    @Key("scheduleName")
    String scheduleName();

    @DefaultStringValue("Cron Expression")
    @Key("cronExpression")
    String cronExpression();

    @DefaultStringValue("Field Name")
    @Key("fieldName")
    String fieldName();

    @DefaultStringValue("Mandatory")
    @Key("mandatory")
    String mandatory();

    @DefaultStringValue("Allowed Values")
    @Key("allowedValues")
    String allowedValues();

    @DefaultStringValue("Allowed Special Characters")
    @Key("allowedSpecialCharacters")
    String allowedSpecialCharacters();

    @DefaultStringValue("Seconds")
    @Key("seconds")
    String seconds();

    @DefaultStringValue("YES")
    @Key("yes")
    String yes();

    @DefaultStringValue("Minutes")
    @Key("minutes")
    String minutes();

    @DefaultStringValue("Hours")
    @Key("hours")
    String hours();

    @DefaultStringValue("Day of Month")
    @Key("dayOfMonth")
    String dayOfMonth();

    @DefaultStringValue("Month")
    @Key("month")
    String month();

    @DefaultStringValue("Day of Week")
    @Key("dayOfWeek")
    String dayOfWeek();

    @DefaultStringValue("Year")
    @Key("year")
    String year();

}
