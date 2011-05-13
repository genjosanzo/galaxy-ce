package org.mule.galaxy.web.client.ui.util;

import java.util.Date;
import java.util.Map;

import org.mule.galaxy.web.client.ui.help.PanelMessages;

import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.core.client.GWT;

public class UIUtil {

	private static final PanelMessages panelMessages = (PanelMessages) GWT.create(PanelMessages.class);
	
    public static boolean validatePromptInput(MessageBoxEvent be, final String errorMsg) {
        // handle Cancel button
        if (Dialog.CANCEL.equals(be.getButtonClicked().getItemId())) {
            return false;
        }

        // validate name
        final String value = be.getValue();
        if (value == null || Util.isEmptyString(value.trim())) {
            be.setCancelled(true);
            be.getMessageBox().show();
            be.getMessageBox().getTextBox().forceInvalid(errorMsg);
            return false;
        }
        return true;
    }

    public static String getBulkItemExceptionMessage(String action,
            String generalExceptionPreface, Map<String, String> idAndName,
            Map<String, Exception> m) {
        String html = panelMessages.serversError(action);
        for (Map.Entry<String, Exception> e : m.entrySet()) {
            Exception value = e.getValue();
            html += "<li>";

            String name = idAndName.get(e.getKey());

            html += getExceptionMessage(value, name, generalExceptionPreface);

            html += "</li>";

        }
        html += "</ul>";
        return html;
    }

    public static String getExceptionMessage(Throwable value, String name,
            String generalExceptionPreface) {
        return getExceptionMessage(name, generalExceptionPreface, value.toString());
    }

    public static String getExceptionMessage(String name,
            String generalExceptionPreface, String detail) {
        return format(generalExceptionPreface, name) + ": " + detail;
    }

    private static String format(String string, String... strings) {
        for (int i = 0; i < strings.length; i++) {
            string = string.replaceAll("\\{" + i + "\\}", strings[i]);
        }
        return string;
    }

    public static int getUnixTimeStamp() {
        Date date = new Date();
        return (int) (date.getTime() * .001);
    }
    
}
