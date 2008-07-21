package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ITEM_DELETED;
import org.mule.galaxy.event.ItemDeletedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ITEM_DELETED)
public class ItemDeletedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(ItemDeletedEvent event) {
        final String message = MessageFormat.format("Item {0} was deleted", event.getArtifactPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}