package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_VERSION_CREATED;
import org.mule.galaxy.event.ItemVersionCreatedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ENTRY_VERSION_CREATED)
public class EntryVersionCreatedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(ItemVersionCreatedEvent event) {
        final String message = MessageFormat.format("Version {0} was created for item {1}",
                                                    event.getVersionLabel(), event.getArtifactPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}