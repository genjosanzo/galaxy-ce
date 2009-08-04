package org.mule.galaxy.web.client.ui;

/**
 * Interface to represent the constants contained in resource bundle:
 * 	'/Users/mark/Foo/src/org/mule/galaxy/ee/web/client/ui/AdministrationConstants.properties'.
 */
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
   * Translated "Manage security and options, monitor activity, and create and schedule scripts".
   *
   * @return translated "Manage security and options, monitor activity, and create and schedule scripts"
   */
  @DefaultStringValue("Manage security and options, monitor activity, and create and schedule scripts")
  @Key("admin_TabTip")
  String admin_TabTip();

  /**
   * Translated "Select the permissions you want users to have. ".
   *
   * @return translated "Select the permissions you want users to have. "
   */
  @DefaultStringValue("Select the permissions you want users to have. ")
  @Key("admin_User_Groups_Tip")
  String admin_User_Groups_Tip();
}
