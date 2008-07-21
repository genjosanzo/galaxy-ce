package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ITEM_VERSION_DELETED;
import org.mule.galaxy.event.ItemVersionDeletedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ITEM_VERSION_DELETED)
public class ItemVersionDeletedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(ItemVersionDeletedEvent event) {
        final String message = MessageFormat.format(
                "Version {0} of item {1} was deleted",
                event.getVersionLabel(), event.getArtifactPath());

        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}