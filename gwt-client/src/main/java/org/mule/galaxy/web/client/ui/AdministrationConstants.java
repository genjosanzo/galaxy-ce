package org.mule.galaxy.web.client.ui;

public interface AdministrationConstants extends com.google.gwt.i18n.client.Constants {

    /**
     * Translated "Display related help topic on www.mulesource.org".
     *
     * @return translated "Display related help topic on www.mulesource.org"
     */
    @DefaultStringValue("Display related help topic on www.mulesource.org")
    @Key("admin_HelpLink")
    String admin_HelpLink();

    /**
     * Translated "Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished setting up this lifecycle.".
     *
     * @return translated "Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished setting up this lifecycle."
     */
    @DefaultStringValue("Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished setting up this lifecycle.")
    @Key("admin_AddLifecycle_Tip")
    String admin_AddLifecycle_Tip();

    /**
     * Translated "You can filter the activity log by date, by the user who made the changes, and by artifacts containing specific text or of certain types. You can also type the name of a specific artifact in the Relating to box to see activity for just that item. Additionally, you can specify the maximum number of results you want to return. After specifying the filter conditions you want, click <b>Search</b>. To clear the filter and see all activity, click <b>Reset</b>.".
     *
     * @return translated "You can filter the activity log by date, by the user who made the changes, and by artifacts containing specific text or of certain types. You can also type the name of a specific artifact in the Relating to box to see activity for just that item. Additionally, you can specify the maximum number of results you want to return. After specifying the filter conditions you want, click <b>Search</b>. To clear the filter and see all activity, click <b>Reset</b>."
     */
    @DefaultStringValue("You can filter the activity log by date, by the user who made the changes, and by artifacts containing specific text or of certain types. You can also type the name of a specific artifact in the Relating to box to see activity for just that item. Additionally, you can specify the maximum number of results you want to return. After specifying the filter conditions you want, click <b>Search</b>. To clear the filter and see all activity, click <b>Reset</b>.")
    @Key("admin_Activity_Tip")
    String admin_Activity_Tip();

    /**
     * Translated "Select the script you want to schedule, enter a unique name and description for this scheduled job, and then enter a cron command (hover over the Cron Command field for cron syntax tips) to specify when the script should be run. To allow this script to be run even if the last execution of the script is still running, click <b>Allow Concurrent Execution</b>.".
     *
     * @return translated "Select the script you want to schedule, enter a unique name and description for this scheduled job, and then enter a cron command (hover over the Cron Command field for cron syntax tips) to specify when the script should be run. To allow this script to be run even if the last execution of the script is still running, click <b>Allow Concurrent Execution</b>."
     */
    @DefaultStringValue("Select the script you want to schedule, enter a unique name and description for this scheduled job, and then enter a cron command (hover over the Cron Command field for cron syntax tips) to specify when the script should be run. To allow this script to be run even if the last execution of the script is still running, click <b>Allow Concurrent Execution</b>.")
    @Key("admin_Add_Scheduled_Item_Tip")
    String admin_Add_Scheduled_Item_Tip();

    /**
     * Translated "Enter a user name that the user will enter when logging in, as well as the user's full name, email address, and a password of at least five characters. Select at least one role for this user and click the right arrow (>). Click <b>Save</b> to create the user.".
     *
     * @return translated "Enter a user name that the user will enter when logging in, as well as the user's full name, email address, and a password of at least five characters. Select at least one role for this user and click the right arrow (>). Click <b>Save</b> to create the user."
     */
    @DefaultStringValue("Enter a user name that the user will enter when logging in, as well as the user's full name, email address, and a password of at least five characters. Select at least one role for this user and click the right arrow (>). Click <b>Save</b> to create the user.")
    @Key("admin_Add_User_Tip")
    String admin_Add_User_Tip();

    /**
     * Translated "Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished editing this lifecycle.".
     *
     * @return translated "Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished editing this lifecycle."
     */
    @DefaultStringValue("Enter a unique name for this lifecycle, specify whether you want this to be the default lifecycle applied to all new artifacts, and then click <b>Add</b> to add phases to the lifecycle. Click the phase that you want to come first in the lifecycle and click <b>Initial Phase</b>. After you have added the phases, click the first phase and highlight all the phases that will come after that phases in the list on the right. Repeat this step for each of the phases, highlighting the phases that will come after the phase. To delete a phase, select it and click <b>Delete</b>. Click <b>Save</b> when you have finished editing this lifecycle.")
    @Key("admin_EditLifecycle_Tip")
    String admin_EditLifecycle_Tip();

    /**
     * Translated "Creates a new lifecycle that you can use to apply policies to artifacts.".
     *
     * @return translated "Creates a new lifecycle that you can use to apply policies to artifacts."
     */
    @DefaultStringValue("Creates a new lifecycle that you can use to apply policies to artifacts.")
    @Key("admin_Lifecycles_New")
    String admin_Lifecycles_New();

    /**
     * Translated "A <i>lifecycle</i> is a series of phases that you can use to control artifacts managed by Galaxy, such as applying one policy during the Developed phase and another policy during the Production phase. To edit the phases in a lifecycle, click its name. To add a new lifecycle, click <b>Add</b>.".
     *
     * @return translated "A <i>lifecycle</i> is a series of phases that you can use to control artifacts managed by Galaxy, such as applying one policy during the Developed phase and another policy during the Production phase. To edit the phases in a lifecycle, click its name. To add a new lifecycle, click <b>Add</b>."
     */
    @DefaultStringValue("A <i>lifecycle</i> is a series of phases that you can use to control artifacts managed by Galaxy, such as applying one policy during the Developed phase and another policy during the Production phase. To edit the phases in a lifecycle, click its name. To add a new lifecycle, click <b>Add</b>.")
    @Key("admin_Lifecycles_Tip")
    String admin_Lifecycles_Tip();

    /**
     * Translated "Galaxy comes with several predefined policies that you can use to control artifacts during different phases in a lifecycle. Select the lifecycle and phase, select one or more policies to apply during that phase, and then click the right arrow (<b>></b>). Repeat for each phase in which you want to apply policies. If you need to remove a policy from a phase, select the phase, select the policy from the list on the right, and then click the left arrow (<b><</b>). Click <b>Save</b> when you have finished adding policies to phases.".
     *
     * @return translated "Galaxy comes with several predefined policies that you can use to control artifacts during different phases in a lifecycle. Select the lifecycle and phase, select one or more policies to apply during that phase, and then click the right arrow (<b>></b>). Repeat for each phase in which you want to apply policies. If you need to remove a policy from a phase, select the phase, select the policy from the list on the right, and then click the left arrow (<b><</b>). Click <b>Save</b> when you have finished adding policies to phases."
     */
    @DefaultStringValue("Galaxy comes with several predefined policies that you can use to control artifacts during different phases in a lifecycle. Select the lifecycle and phase, select one or more policies to apply during that phase, and then click the right arrow (<b>></b>). Repeat for each phase in which you want to apply policies. If you need to remove a policy from a phase, select the phase, select the policy from the list on the right, and then click the left arrow (<b><</b>). Click <b>Save</b> when you have finished adding policies to phases.")
    @Key("admin_Policies_Tip")
    String admin_Policies_Tip();

    /**
     * Translated "Properties provide a structured way of tagging artifacts in the repository. Galaxy provides several predefined properties you can use, or you can create your own by clicking <b>Add</b>.".
     *
     * @return translated "Properties provide a structured way of tagging artifacts in the repository. Galaxy provides several predefined properties you can use, or you can create your own by clicking <b>Add</b>."
     */
    @DefaultStringValue("Properties provide a structured way of tagging artifacts in the repository. Galaxy provides several predefined properties you can use, or you can create your own by clicking <b>Add</b>.")
    @Key("admin_Properties_Tip")
    String admin_Properties_Tip();

    /**
     * Translated "For each role, select the permissions you want users with that role to have. To change the name of a role, click its name, enter the new name, and click <b>Save</b>. To assign users to a role, click <b>Users</b> in the left navigation pane.".
     *
     * @return translated "For each role, select the permissions you want users with that role to have. To change the name of a role, click its name, enter the new name, and click <b>Save</b>. To assign users to a role, click <b>Users</b> in the left navigation pane."
     */
    @DefaultStringValue("For each role, select the permissions you want users with that role to have. To change the name of a role, click its name, enter the new name, and click <b>Save</b>. To assign users to a role, click <b>Users</b> in the left navigation pane.")
    @Key("admin_Roles_Tip")
    String admin_Roles_Tip();

    /**
     * Translated "You use the scheduler to run scripts automatically on a scheduled basis. To create a new schedule, click <b>New</b>.".
     *
     * @return translated "You use the scheduler to run scripts automatically on a scheduled basis. To create a new schedule, click <b>New</b>."
     */
    @DefaultStringValue("You use the scheduler to run scripts automatically on a scheduled basis. To create a new schedule, click <b>New</b>.")
    @Key("admin_Scheduler_Tip")
    String admin_Scheduler_Tip();

    /**
     * Translated "Manage security and options, monitor activity, and create and schedule scripts".
     *
     * @return translated "Manage security and options, monitor activity, and create and schedule scripts"
     */
    @DefaultStringValue("Manage security and options, monitor activity, and create and schedule scripts")
    @Key("admin_TabTip")
    String admin_TabTip();

    /**
     * Translated "Use this screen to administer your Galaxy instance, including setting permissions for users and groups, and monitoring activity on the server.".
     *
     * @return translated "Use this screen to administer your Galaxy instance, including setting permissions for users and groups, and monitoring activity on the server."
     */
    @DefaultStringValue("Use this screen to administer your Galaxy instance, including setting permissions for users and groups, and monitoring activity on the server.")
    @Key("admin_Tip")
    String admin_Tip();

    /**
     * Translated "Creates a new user who can log into the application".
     *
     * @return translated "Creates a new user who can log into the application"
     */
    @DefaultStringValue("Creates a new user who can log into the application")
    @Key("admin_Users_New")
    String admin_Users_New();

    /**
     * Translated "Users are individuals who can log in to the application. Click <b>New</b> to add a user. When you add users, you assign them roles to determine which permissions they have, such as granting certain users the Administrator role to allow them to manage users.".
     *
     * @return translated "Users are individuals who can log in to the application. Click <b>New</b> to add a user. When you add users, you assign them roles to determine which permissions they have, such as granting certain users the Administrator role to allow them to manage users."
     */
    @DefaultStringValue("Users are individuals who can log in to the application. Click <b>New</b> to add a user. When you add users, you assign them roles to determine which permissions they have, such as granting certain users the Administrator role to allow them to manage users.")
    @Key("admin_Users_Tip")
    String admin_Users_Tip();
}
