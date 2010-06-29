package org.mule.galaxy.web.client.ui.dialog;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class KillableMessageBox extends MessageBox {

    public static MessageBox wait(String title, String msg, String progressText,
                                  Listener<MessageBoxEvent> callback) {
        MessageBox box = new MessageBox();
        box.setTitle(title);
        box.setMessage(msg);
        box.setType(MessageBoxType.WAIT);
        box.setProgressText(progressText);
        box.setButtons("");
        box.setClosable(true);
        if (callback != null) {
            box.addCallback(callback);
        }
        box.show();
        return box;
    }

    public static MessageBox wait(String title, String msg, String progressText) {
        return wait(title, msg, progressText, null);
    }

    public static MessageBox progress(String title, String msg, String progressText) {
        return progress(title, msg, progressText, null);
    }

    public static MessageBox progress(String title, String msg, String progressText,
                                      Listener<MessageBoxEvent> callback) {
        MessageBox box = new MessageBox();
        box.setTitle(title);
        box.setMessage(msg);
        box.setType(MessageBoxType.PROGRESSS);
        box.setProgressText(progressText);
        box.setButtons("");
        box.setClosable(true);
        if (callback != null) {
            box.addCallback(callback);
        }
        box.show();
        return box;
    }


}
