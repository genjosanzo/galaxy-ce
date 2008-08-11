package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.WORKSPACE_CREATED;
import org.mule.galaxy.event.WorkspaceCreatedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(WORKSPACE_CREATED)
public class WorkspaceCreatedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(WorkspaceCreatedEvent event) {
        final String message = MessageFormat.format("Workspace {0} was created", event.getItemPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}