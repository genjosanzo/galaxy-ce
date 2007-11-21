package org.mule.galaxy.lifecycle;

import org.mule.galaxy.Artifact;

public interface LifecycleListener {
    void nextLifecyclePhase(Artifact artifact, Phase phase);
}
