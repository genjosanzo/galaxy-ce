package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.WORKSPACE_DELETED;
import org.mule.galaxy.event.WorkspaceDeletedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(WORKSPACE_DELETED)
public class WorkspaceDeletedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(WorkspaceDeletedEvent event) {
        final String message = MessageFormat.format("Workspace {0} was deleted", event.getWorkspacePath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}
