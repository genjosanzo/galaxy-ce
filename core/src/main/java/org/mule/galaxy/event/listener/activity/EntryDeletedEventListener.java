package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_DELETED;
import org.mule.galaxy.event.EntryDeletedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ENTRY_DELETED)
public class EntryDeletedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(EntryDeletedEvent event) {
        final String message = MessageFormat.format("Item {0} was deleted", event.getArtifactPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}