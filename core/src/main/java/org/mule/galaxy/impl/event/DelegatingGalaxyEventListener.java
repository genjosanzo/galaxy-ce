package org.mule.galaxy.impl.event;

/**
 * Additionally exposes a delegate listener implementation which is being
 * wrapped by this class.
 */
interface DelegatingGalaxyEventListener extends InternalGalaxyEventListener {
    Object getDelegateListener();
}
