package org.mule.galaxy.events;

import java.util.EventListener;

public interface GalaxyEventListener extends EventListener {

    void onEvent(GalaxyEvent event);

}
