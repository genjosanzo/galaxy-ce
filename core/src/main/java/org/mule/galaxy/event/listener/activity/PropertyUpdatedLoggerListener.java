package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.PROPERTY_UPDATED;
import org.mule.galaxy.event.GalaxyEvent;
import org.mule.galaxy.event.PropertyUpdatedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;

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
