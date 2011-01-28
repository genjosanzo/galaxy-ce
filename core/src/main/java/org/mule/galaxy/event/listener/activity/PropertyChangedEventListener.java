package org.mule.galaxy.event.listener.activity;

import static org.mule.galaxy.event.DefaultEvents.PROPERTY_CHANGED;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.PropertyChangedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

@BindToEvent(PROPERTY_CHANGED)
public class PropertyChangedEventListener extends AbstractActivityLoggingListener {

    private List<String> ignoreProperties = new ArrayList<String>();
    
    @OnEvent
    @Async
    public void onEvent(PropertyChangedEvent event) {
        if (ignoreProperties.contains(event.getPropertyName())) {
            return;
        }
        
        final String message = MessageFormat.format(
                "* Property {0} of {1} was set to {2}", event.getPropertyName(), event.getItemPath(), event.getNewValue());

        getActivityManager().logActivity(message, ActivityManager.EventType.INFO, event.getUser(), event.getItemId());
    }

    public List<String> getIgnoreProperties() {
        return ignoreProperties;
    }
    
}