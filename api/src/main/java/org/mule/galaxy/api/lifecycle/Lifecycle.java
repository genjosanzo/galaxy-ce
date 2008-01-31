/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.api.lifecycle;

import java.util.Map;

/**
 * TODO
 */

public class Lifecycle
{
    private String name;
    private Phase initialPhase;
    private Map<String, Phase> phases;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Phase getInitialPhase() {
        return initialPhase;
    }

    public void setInitialPhase(Phase initialPhase) {
        this.initialPhase = initialPhase;
    }

    public Phase getPhase(String phase) {
        return phases.get(phase);
    }

    public Map<String, Phase> getPhases() {
        return phases;
    }

    public void setPhases(Map<String, Phase> phases) {
        this.phases = phases;
    }
}
