package org.mule.galaxy.impl.event;

interface DelegatingGalaxyEventListener extends InternalGalaxyEventListener {
    Object getDelegateListener();
}
