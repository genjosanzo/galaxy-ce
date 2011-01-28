package org.mule.galaxy.event.listener.activity;

import static org.mule.galaxy.event.DefaultEvents.ITEM_DELETED;

import java.text.MessageFormat;

import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.ItemDeletedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

@BindToEvent(ITEM_DELETED)
public class ItemDeletedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(ItemDeletedEvent event) {
        final String message = MessageFormat.format("Entry {0} was deleted", event.getItemPath());
        getActivityManager().logActivity(message, ActivityManager.EventType.INFO, event.getUser(), event.getItemId());
    }
}