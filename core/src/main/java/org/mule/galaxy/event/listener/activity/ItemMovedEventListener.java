package org.mule.galaxy.event.listener.activity;

import static org.mule.galaxy.event.DefaultEvents.ITEM_MOVED;

import java.text.MessageFormat;

import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.ItemMovedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

@BindToEvent(ITEM_MOVED)
public class ItemMovedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(ItemMovedEvent event) {
        final String message = MessageFormat.format("Entry {0} was moved to {1}",
                                                    event.getOldPath(), event.getItemPath());
        getActivityManager().logActivity(message, ActivityManager.EventType.INFO, event.getUser(), event.getItemId());
    }
}