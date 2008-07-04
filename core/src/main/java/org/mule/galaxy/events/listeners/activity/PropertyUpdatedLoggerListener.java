package org.mule.galaxy.events.listeners.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.events.DefaultEvents.PROPERTY_UPDATED;
import org.mule.galaxy.events.GalaxyEvent;
import org.mule.galaxy.events.PropertyUpdatedEvent;
import org.mule.galaxy.events.annotations.BindToEvent;

@BindToEvent(PROPERTY_UPDATED)
public class PropertyUpdatedLoggerListener extends ActivityLoggerListener {

    @Override
    public void onEvent(final GalaxyEvent event) {
        PropertyUpdatedEvent e = (PropertyUpdatedEvent) event;
        getActivityManager().logActivity(e.getUser(), e.getMessage(), ActivityManager.EventType.INFO);
    }

    public void onEvent(final PropertyUpdatedEvent event) {
        
    }
}
