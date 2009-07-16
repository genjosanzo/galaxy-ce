package org.mule.galaxy.web.client;

import com.extjs.gxt.ui.client.data.BaseModel;

// wrapper class for left side nav items
public class NavMenuItem extends BaseModel {

    private String title;
    private String tokenBase;
    private AbstractComposite listPanel;
    private AbstractComposite formPanel;
    public static final String NEW = "/new";


    public NavMenuItem(String title, String tokenBase, AbstractComposite listPanel,
                       AbstractComposite formPanel) {
        set("title", title);
        set("tokenBase", tokenBase);
        set("listPanel", listPanel);
        set("formPanel", formPanel);
    }

    public String getTitle() {
        return (String) get("title");
    }

    public void setTitle(String title) {
        set("title", title);
    }

    public String getTokenBase() {
        return (String) get("tokenBase");
    }

    public void setTokenBase(String tokenBase) {
        set("tokenBase", tokenBase);
    }

    public AbstractComposite getListPanel() {
        return (AbstractComposite) get("listPanel");
    }

    public void setListPanel(AbstractComposite listPanel) {
        set("listPanel", listPanel);
    }

    public AbstractComposite getFormPanel() {
        return (AbstractComposite) get("formPanel");
    }

    public void setFormPanel(AbstractComposite formPanel) {
        set("formPanel", formPanel);
    }

}
