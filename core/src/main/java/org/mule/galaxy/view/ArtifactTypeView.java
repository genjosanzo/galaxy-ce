package org.mule.galaxy.view;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.Artifact;

/**
 * Information necessary to display a group of similar artifacts.
 */
public interface ArtifactTypeView {
    String[] getColumnNames();

    String getColumnValue(Artifact row, int column);

    ViewLink getLink(Artifact row, int column);
    
    boolean isSummaryOnly(int column);
    
    // Collection<Artifact> sort(Collection<Artifact> artifacts, int column)
}
