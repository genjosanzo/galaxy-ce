package org.mule.galaxy.events.listeners;

import static org.mule.galaxy.events.DefaultEvents.PROPERTY_UPDATED;
import org.mule.galaxy.events.PropertyUpdatedEvent;
import org.mule.galaxy.events.annotations.BindToEvent;
import org.mule.galaxy.events.annotations.OnEvent;

@BindToEvent(PROPERTY_UPDATED)
public class SysOutLoggingEventListener {

    @OnEvent
    public void onEvent(final PropertyUpdatedEvent event) {
        System.out.println(">>>>>>>> PropertyUpdated");
    }

}
