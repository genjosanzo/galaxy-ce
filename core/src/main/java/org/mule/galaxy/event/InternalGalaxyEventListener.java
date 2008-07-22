package org.mule.galaxy.event;

import java.util.EventListener;

/**
 * This is a utility interface used by the {@link DefaultEventManager} and is
 * not meant for generic use.
 */
interface InternalGalaxyEventListener extends EventListener {
    void onEvent(GalaxyEvent event);
}
