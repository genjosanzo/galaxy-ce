package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_MOVED;
import org.mule.galaxy.event.ItemMovedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ENTRY_MOVED)
public class EntryMovedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(ItemMovedEvent event) {
        final String message = MessageFormat.format("Item {0} was moved to {1}",
                                                    event.getArtifactOldPath(), event.getArtifactNewPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}