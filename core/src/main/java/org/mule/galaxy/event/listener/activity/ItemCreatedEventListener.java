package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ITEM_CREATED;
import org.mule.galaxy.event.ItemCreatedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ITEM_CREATED)
public class ItemCreatedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(ItemCreatedEvent event) {
        final String message = MessageFormat.format("Entry {0} was created", event.getItemPath());
        getActivityManager().logActivity(message, ActivityManager.EventType.INFO, event.getUser(), event.getItemId());
    }
}