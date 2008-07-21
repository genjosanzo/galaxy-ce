package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ITEM_VERSION_CREATED;
import org.mule.galaxy.event.ItemVersionCreatedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ITEM_VERSION_CREATED)
public class ItemVersionCreatedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(ItemVersionCreatedEvent event) {
        final String message = MessageFormat.format("Version {0} was created for item {1}",
                                                    event.getVersionLabel(), event.getArtifactPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}