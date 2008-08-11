package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_VERSION_CREATED;
import org.mule.galaxy.event.EntryVersionCreatedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ENTRY_VERSION_CREATED)
public class EntryVersionCreatedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(EntryVersionCreatedEvent event) {
        final String message = MessageFormat.format("Version {0} was created for entry {1}",
                                                    event.getVersionLabel(), event.getItemPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}