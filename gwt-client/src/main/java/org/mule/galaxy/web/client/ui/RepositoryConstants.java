package org.mule.galaxy.web.client.ui;

/**
 * Interface to represent the constants contained in resource bundle:
 */
public interface RepositoryConstants extends com.google.gwt.i18n.client.Constants {

  /**
   * Translated "Specify the type of item you are adding, the workspace (parent) where you want it stored, and a name for it. Additional fields appear depending on the type of item you select (see the <a href=\"http://www.mulesoft.org/display/TCAT/Managing+the+Repository\" target=\"_blank\">online help</a> for details). Click <b>Add</b> to create the item.".
   *
   * @return translated "Specify the type of item you are adding, the workspace (parent) where you want it stored, and a name for it. Additional fields appear depending on the type of item you select (see the <a href=\"http://www.mulesoft.org/display/TCAT/Managing+the+Repository\" target=\"_blank\">online help</a> for details). Click <b>Add</b> to create the item."
   */
  @DefaultStringValue("Specify the type of item you are adding, the workspace (parent) where you want it stored, and a name for it. Additional fields appear depending on the type of item you select (see the <a href=\"http://www.mulesoft.org/display/TCAT/Managing+the+Repository\" target=\"_blank\">online help</a> for details). Click <b>Add</b> to create the item.")
  @Key("repo_Add_Item_Tip")
  String repo_Add_Item_Tip();

  /**
   * Translated "Display related help topic on www.mulesoft.org".
   *
   * @return translated "Display related help topic on www.mulesoft.org"
   */
  @DefaultStringValue("Display related help topic on www.mulesoft.org")
  @Key("repo_HelpLink")
  String repo_HelpLink();

  /**
   * Translated "View and edit the name, metadata, and comments for the current workspace or item".
   *
   * @return translated "View and edit the name, metadata, and comments for the current workspace or item"
   */
  @DefaultStringValue("View and edit the name, metadata, and comments for the current workspace or item")
  @Key("repo_Info_TabTip")
  String repo_Info_TabTip();

  /**
   * Translated "Adds a new item to the repository".
   *
   * @return translated "Adds a new item to the repository"
   */
  @DefaultStringValue("Adds a new item to the repository")
  @Key("repo_Items_New")
  String repo_Items_New();

  /**
   * Translated "Add, view, and delete items in the repository".
   *
   * @return translated "Add, view, and delete items in the repository"
   */
  @DefaultStringValue("Add, view, and delete items in the repository")
  @Key("repo_Items_TabTip")
  String repo_Items_TabTip();

  /**
   * Translated "Apply policies to the different lifecycle phases for the current workspace or item".
   *
   * @return translated "Apply policies to the different lifecycle phases for the current workspace or item"
   */
  @DefaultStringValue("Apply policies to the different lifecycle phases for the current workspace or item")
  @Key("repo_Policies_TabTip")
  String repo_Policies_TabTip();

  /**
   * Translated "Specify the type of access each role has to the current workspace or item".
   *
   * @return translated "Specify the type of access each role has to the current workspace or item"
   */
  @DefaultStringValue("Specify the type of access each role has to the current workspace or item")
  @Key("repo_Security_TabTip")
  String repo_Security_TabTip();

  /**
   * Translated "For each role, specify how users with that role can access this artifact or workspace. In the row for the role whose permissions you want to change, select whether you want to grant or revoke the right to read, modify, or delete the item, or to manage policies for the item.".
   *
   * @return translated "For each role, specify how users with that role can access this artifact or workspace. In the row for the role whose permissions you want to change, select whether you want to grant or revoke the right to read, modify, or delete the item, or to manage policies for the item."
   */
  @DefaultStringValue("For each role, specify how users with that role can access this artifact or workspace. In the row for the role whose permissions you want to change, select whether you want to grant or revoke the right to read, modify, or delete the item, or to manage policies for the item.")
  @Key("repo_Security_Tip")
  String repo_Security_Tip();

  /**
   * Translated "Manage your repository of applications, servers, and server groups".
   *
   * @return translated "Manage your repository of applications, servers, and server groups"
   */
  @DefaultStringValue("Manage your repository of applications, servers, and server groups")
  @Key("repo_TabTip")
  String repo_TabTip();

  /**
   * Translated "The repository stores applications and other objects as <i>artifacts</i>. Artifacts are organized into <i>workspaces</i>, such as the Applications workspace where your applications are stored by default. You can search or browse the repository, view the details of your artifacts, and add metadata and comments about them.".
   *
   * @return translated "The repository stores applications and other objects as <i>artifacts</i>. Artifacts are organized into <i>workspaces</i>, such as the Applications workspace where your applications are stored by default. You can search or browse the repository, view the details of your artifacts, and add metadata and comments about them."
   */
  @DefaultStringValue("The repository stores applications and other objects as <i>artifacts</i>. Artifacts are organized into <i>workspaces</i>, such as the Applications workspace where your applications are stored by default. You can search or browse the repository, view the details of your artifacts, and add metadata and comments about them.")
  @Key("repo_Tip")
  String repo_Tip();
}
