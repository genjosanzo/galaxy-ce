package org.mule.galaxy.impl.event;

import java.util.EventListener;

import org.mule.galaxy.event.GalaxyEvent;

/**
 * This is a utility interface used by the {@link DefaultEventManager} and is
 * not meant for generic use.
 */
interface InternalGalaxyEventListener extends EventListener {
    void onEvent(GalaxyEvent event);
}
