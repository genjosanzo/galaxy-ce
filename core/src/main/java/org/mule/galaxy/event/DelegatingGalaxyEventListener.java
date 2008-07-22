package org.mule.galaxy.event;

interface DelegatingGalaxyEventListener extends InternalGalaxyEventListener {
    Object getDelegateListener();
}
