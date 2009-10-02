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
    private Dialog simpleDialog = new Dialog();

    public AbstractInfoPanel(int height) {
        simpleDialog.setHeading(getHeading());
        if(height != -1) {
            simpleDialog.setMinHeight(height);
        }
        simpleDialog.setButtons(Dialog.OK);
        simpleDialog.add(getText());
        simpleDialog.setScrollMode(Style.Scroll.AUTO);
        simpleDialog.setHideOnButtonClick(true);
        simpleDialog.setWidth(getWidth());
        simpleDialog.show();
    }

    public AbstractInfoPanel() {
        this(-1);
    }


    public abstract String getHeading();

    public abstract Html getText();

    public void setHeading(String s) {
        simpleDialog.setHeading(s);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public Dialog getSimpleDialog() {
        return simpleDialog;
    }

    public void setSimpleDialog(Dialog simpleDialog) {
        this.simpleDialog = simpleDialog;
    }


}
