package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ITEM_MOVED;
import org.mule.galaxy.event.ItemMovedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ITEM_MOVED)
public class ItemMovedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(ItemMovedEvent event) {
        final String message = MessageFormat.format("Item {0} was moved to {1}",
                                                    event.getArtifactOldPath(), event.getArtifactNewPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}