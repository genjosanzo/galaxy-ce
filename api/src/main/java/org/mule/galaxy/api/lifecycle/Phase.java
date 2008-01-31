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

import org.mule.galaxy.api.security.User;

import java.util.Set;
import java.util.HashSet;

/**
 * TODO
 */

public class Phase
{
    private String name;
    private Set<Phase> nextPhases;
    private Set<User> approvedUsers;
    private Lifecycle lifecycle;

    public Phase(Lifecycle lifecycle) {
        super();
        this.lifecycle = lifecycle;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The set of phases which can be transitioned to after this phase.
     * @return
     */
    public Set<Phase> getNextPhases() {
        if (nextPhases == null) {
            nextPhases = new HashSet<Phase>();
        }
        return nextPhases;
    }

    public void setNextPhases(Set<Phase> nextPhases) {
        this.nextPhases = nextPhases;
    }

    /**
     * The users which can manipulate this lifecycle phase.
     * @return
     */
    public Set<User> getApprovedUsers() {
        return approvedUsers;
    }

    public void setApprovedUsers(Set<User> approvedUsers) {
        this.approvedUsers = approvedUsers;
    }

}
