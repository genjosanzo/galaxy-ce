package org.mule.galaxy.events.listeners;

import org.mule.galaxy.events.annotations.BindToEvent;
import org.mule.galaxy.events.GalaxyEventListener;
import org.mule.galaxy.events.GalaxyEvent;

@BindToEvent("WorkspaceCreated")
public class SysOutLoggingEventListener implements GalaxyEventListener {

    public void onEvent(final GalaxyEvent event) {
        System.out.println(">>>>>>>> " + event);
    }

}
