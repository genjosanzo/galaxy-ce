package org.mule.galaxy.repository.client;

/**
 * Interface to represent the constants contained in resource bundle:
 */
public interface RepositoryConstants extends com.google.gwt.i18n.client.Constants {

    /**
     * Translated
     * "Specify the type of item you are adding, the workspace (parent) where you want it stored, and a name for it. Additional fields appear depending on the type of item you select (see the <a href=\"http://www.mulesoft.org/display/TCAT/Managing+the+Repository\" target=\"_blank\">online help</a> for details). Click <b>Add</b> to create the item."
     * .
     * 
     * @return translated
     *         "Specify the type of item you are adding, the workspace (parent) where you want it stored, and a name for it. Additional fields appear depending on the type of item you select (see the <a href=\"http://www.mulesoft.org/display/TCAT/Managing+the+Repository\" target=\"_blank\">online help</a> for details). Click <b>Add</b> to create the item."
     */

    String repo_Add_Item_Tip();

    /**
     * Translated "Deletes the currently selected workspace(s) or artifact(s)".
     * 
     * @return translated
     *         "Deletes the currently selected workspace(s) or artifact(s)"
     */

    String repo_Delete();

    /**
     * Translated "Display related help topic on www.mulesoft.org".
     * 
     * @return translated "Display related help topic on www.mulesoft.org"
     */

    String repo_HelpLink();

    /**
     * Translated
     * "View and edit the name, metadata, and comments for the current workspace or item"
     * .
     * 
     * @return translated
     *         "View and edit the name, metadata, and comments for the current workspace or item"
     */

    String repo_Info_TabTip();

    /**
     * Translated "Adds a new item to the repository".
     * 
     * @return translated "Adds a new item to the repository"
     */

    String repo_Items_New();

    /**
     * Translated "Add, view, and delete items in the repository".
     * 
     * @return translated "Add, view, and delete items in the repository"
     */

    String repo_Items_TabTip();

    /**
     * Translated "Adds a new artifact (file) to the current workspace".
     * 
     * @return translated "Adds a new artifact (file) to the current workspace"
     */

    String repo_NewArtifact();

    /**
     * Translated "Adds a new workspace under the current workspace".
     * 
     * @return translated "Adds a new workspace under the current workspace"
     */

    String repo_NewWorkspace();

    /**
     * Translated
     * "Apply policies to the different lifecycle phases for the current workspace or item"
     * .
     * 
     * @return translated
     *         "Apply policies to the different lifecycle phases for the current workspace or item"
     */

    String repo_Policies_TabTip();

    /**
     * Translated
     * "Specify the type of access each role has to the current workspace or item"
     * .
     * 
     * @return translated
     *         "Specify the type of access each role has to the current workspace or item"
     */

    String repo_Security_TabTip();

    /**
     * Translated
     * "For each role, specify how users with that role can access this artifact or workspace. In the row for the role whose permissions you want to change, select whether you want to grant or revoke the right to read, modify, or delete the item, or to manage policies for the item."
     * .
     * 
     * @return translated
     *         "For each role, specify how users with that role can access this artifact or workspace. In the row for the role whose permissions you want to change, select whether you want to grant or revoke the right to read, modify, or delete the item, or to manage policies for the item."
     */

    String repo_Security_Tip();

    /**
     * Translated
     * "Manage your repository of applications, servers, and server groups".
     * 
     * @return translated
     *         "Manage your repository of applications, servers, and server groups"
     */

    String repo_TabTip();

    /**
     * Translated
     * "The repository stores applications and other objects as <i>artifacts</i>. Artifacts are organized into <i>workspaces</i>, such as the Applications workspace where your applications are stored by default. You can search or browse the repository, view the details of your artifacts, and add metadata and comments about them."
     * .
     * 
     * @return translated
     *         "The repository stores applications and other objects as <i>artifacts</i>. Artifacts are organized into <i>workspaces</i>, such as the Applications workspace where your applications are stored by default. You can search or browse the repository, view the details of your artifacts, and add metadata and comments about them."
     */

    String repo_Tip();
}
