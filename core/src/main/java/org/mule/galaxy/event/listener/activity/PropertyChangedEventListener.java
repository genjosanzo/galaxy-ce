package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.PROPERTY_CHANGED;
import org.mule.galaxy.event.PropertyChangedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(PROPERTY_CHANGED)
public class PropertyChangedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(PropertyChangedEvent event) {
        final String message = MessageFormat.format(
                "* Property {0} of {1} was set to {2}", event.getPropertyName(), event.getItemPath(), event.getNewValue());

        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}