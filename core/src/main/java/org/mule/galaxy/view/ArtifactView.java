package org.mule.galaxy.view;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.Artifact;

public interface ArtifactView {
    String[] getColumnNames();

    String getColumnValue(Artifact row, int column);

    ViewLink getLink(Artifact row, int column);
    
    // Collection<Artifact> sort(Collection<Artifact> artifacts, int column)
}
