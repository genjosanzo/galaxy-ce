package org.mule.galaxy.web.client;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Generic popup window for "about" style panels
 */
public abstract class AbstractInfoPanel extends LayoutContainer {

    private int width = 500;
    private Dialog simple = new Dialog();

    public AbstractInfoPanel() {
        simple.setHeading(getHeading());
        simple.setButtons(Dialog.OK);
        simple.add(getText());
        simple.setScrollMode(Style.Scroll.AUTO);
        simple.setHideOnButtonClick(true);
        simple.setWidth(getWidth());
        simple.show();

    }


    public abstract String getHeading();

    public abstract Html getText();

    public void setHeading(String s) {
        simple.setHeading(s);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


}
