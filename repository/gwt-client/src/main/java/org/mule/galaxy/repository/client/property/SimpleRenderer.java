package org.mule.galaxy.repository.client.property;

import org.mule.galaxy.web.client.ui.ExternalHyperlink;
import org.mule.galaxy.web.client.ui.panel.InlineFlowPanel;

import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SimpleRenderer extends AbstractPropertyRenderer {

    private TextField<String> valueTB;

    public Widget createEditForm() {
        valueTB = new TextField<String>();
        valueTB.setValue((String) value);
        return valueTB;
    }

    public Object getValueToSave() {
        return valueTB.getValue();
    }

    public Widget createViewWidget() {
        String txt = value.toString();

        return createWidget(txt);
    }

    public static Widget createWidget(String txt) {
        if ("".equals(txt) || txt == null) {
            return new Label("-----");
        }

        String[] split = txt.split(" ");
        boolean foundLink = false;
        boolean first = true;
        InlineFlowPanel panel = new InlineFlowPanel();

        for (String s : split) {
            if (!first) {
                panel.add(new Label(" "));
            } else {
                first = false;
            }

            if (s.matches("\\b(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
                foundLink = true;
                panel.add(new ExternalHyperlink(s, s));
            } else {
                panel.add(new Label(s));
            }
        }

        if (foundLink) {
            return panel;
        } else {
            return new Label(txt);
        }
    }

    @Override
    public boolean validate() {
        return valueTB.validate();
    }

}
