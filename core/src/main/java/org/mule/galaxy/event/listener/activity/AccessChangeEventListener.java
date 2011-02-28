package org.mule.galaxy.event.listener.activity;

import java.text.MessageFormat;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;
import org.mule.galaxy.security.AccessChangeEvent;

@BindToEvent("org.mule.galaxy.security.AccessChangeEvent")
public class AccessChangeEventListener extends AbstractActivityLoggingListener {

    protected final Log logger = LogFactory.getLog(getClass());

    @OnEvent
    @Async
    public void onEvent(AccessChangeEvent event) {
        String message = null;
        switch (event.getType()) {
        case CREATED:
            message = MessageFormat.format("Group {0} was created.", event.getGroup().getName());
            break;
        case DELETED:
            message = MessageFormat.format("Group {0} was deleted.", event.getGroup().getName());
            break;
        case GRANT:
            if (event.getItem() != null) {
                message = MessageFormat.format("Group {0} was granted permissions {1} for item {2}.", event.getGroup().getName(), Arrays.toString(event.getPermissions().toArray()), event.getItem().toString());
            } else {
                message = MessageFormat.format("Group {0} was granted permissions {1}.", event.getGroup().getName(), Arrays.toString(event.getPermissions().toArray()));
            }
            break;
        case REVOKE:
            if (event.getItem() != null) {
                message = MessageFormat.format("Group {0} was revoked permissions {1} for item {2}.", event.getGroup().getName(), Arrays.toString(event.getPermissions().toArray()), event.getItem().toString());
            } else {
                message = MessageFormat.format("Group {0} was revoked permissions {1}.", event.getGroup().getName(), Arrays.toString(event.getPermissions().toArray()));
            }
            break;
        default: 
            throw new IllegalStateException();
        }

        getActivityManager().logActivity(message, ActivityManager.EventType.INFO, event.getUser(), null);
    }

}
