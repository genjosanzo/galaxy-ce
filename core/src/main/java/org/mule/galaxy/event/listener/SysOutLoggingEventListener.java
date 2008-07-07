package org.mule.galaxy.event.listener;

import static org.mule.galaxy.event.DefaultEvents.PROPERTY_UPDATED;
import org.mule.galaxy.event.PropertyUpdatedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

@BindToEvent(PROPERTY_UPDATED)
public class SysOutLoggingEventListener {

    @OnEvent
    public void onEvent(final PropertyUpdatedEvent event) {
        System.out.println(">>>>>>>> PropertyUpdated");
    }

}
