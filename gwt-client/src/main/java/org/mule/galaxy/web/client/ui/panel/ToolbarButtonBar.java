package org.mule.galaxy.web.client.ui.panel;

import org.mule.galaxy.web.client.ui.button.ToolbarButton;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import java.util.ArrayList;
import java.util.List;

/* assigns correct style based on position in toolbar */
public class ToolbarButtonBar extends ToolBar {

    public ToolbarButtonBar() {
    }

    public ToolbarButtonBar(ToolbarButton button) {
        this.add(button);
    }

    public ToolbarButtonBar(List<ToolbarButton> buttons) {
        this.add(buttons);
    }


    public void add(ToolbarButton button) {
        super.add(button);
        setStyles();
    }

    public void add(List<ToolbarButton> buttons) {
        for(ToolbarButton b : buttons) {
           this.add(b);
        }
    }


    private List<ToolbarButton> getButtonItems() {
        List<Component> all = getItems();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        for (Component c : all) {
            if (c instanceof ToolbarButton) {
                buttons.add((ToolbarButton) c);
            }
        }
        return buttons;

    }

    private void setStyles() {
        int i = 0;

        List<ToolbarButton> buttons = getButtonItems();
        int size = buttons.size();

        // no toolbar buttons, nothing to do
        if (size == 0) {
            return;
        }

        for (ToolbarButton button : buttons) {
            i++;
            if (size == 1) {
                // just one button
                button.setStyleName("toolbar-btn");
            } else if (i == 1) {
                // first one
                button.setStyleName("toolbar-btn_left");
            } else if (i == size) {
                // last one
                button.setStyleName("toolbar-btn_right");
            } else {
                // middle ones
                button.setStyleName("toolbar-btn_center");
            }
        }
    }

}
