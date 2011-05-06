package org.mule.galaxy.web.client.ui.help;

public interface GalaxyMessages extends com.google.gwt.i18n.client.Messages {

    @Key("welcome")
    @DefaultMessage("Welcome, ")
    String welcome();

    @Key("about")
    @DefaultMessage("About ")
    String about();

    @Key("aboutSpace")
    @DefaultMessage("About...")
    String aboutSpace();

    @Key("home")
    @DefaultMessage("Home")
    String home();

    @Key("logOut")
    @DefaultMessage("Log Out")
    String logOut();

    @Key("rights")
    @DefaultMessage("&copy; MuleSoft, Inc. All rights reserved")
    String rights();

    @Key("administration")
    @DefaultMessage("Administration")
    String Administration();

    @Key("info")
    @DefaultMessage("Info:")
    String info();

}
