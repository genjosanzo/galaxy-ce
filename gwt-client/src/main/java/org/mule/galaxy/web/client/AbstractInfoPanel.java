package org.mule.galaxy.web.client;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.Style;

/**
 * Generic popup window for "about" style panels
 */
public abstract class AbstractInfoPanel  extends LayoutContainer  {

    private int width = 500;

    public AbstractInfoPanel()  {
        final Dialog simple = new Dialog();
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


}
