package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_CREATED;
import org.mule.galaxy.event.EntryCreatedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ENTRY_CREATED)
public class EntryCreatedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(EntryCreatedEvent event) {
        final String message = MessageFormat.format("Item {0} was created", event.getArtifactPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}