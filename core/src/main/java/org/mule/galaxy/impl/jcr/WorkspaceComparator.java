package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.api.Workspace;

import java.util.Comparator;

public class WorkspaceComparator implements Comparator<Workspace> {

    public int compare(Workspace w1, Workspace w2) {
        return w1.getName().compareTo(w2.getName());
    }

}
