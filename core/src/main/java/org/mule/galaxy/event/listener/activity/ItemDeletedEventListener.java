package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ITEM_DELETED;
import org.mule.galaxy.event.ItemDeletedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ITEM_DELETED)
public class ItemDeletedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(ItemDeletedEvent event) {
        final String message = MessageFormat.format("Entry {0} was deleted", event.getItemPath());
        getActivityManager().logActivity(message, ActivityManager.EventType.INFO, event.getUser(), event.getItemId());
    }
}