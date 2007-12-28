package org.mule.galaxy.impl.jcr;

import java.util.Comparator;

import org.mule.galaxy.Workspace;

public class WorkspaceComparator implements Comparator<Workspace> {

    public int compare(Workspace w1, Workspace w2) {
        return w1.getName().compareTo(w2.getName());
    }

}
