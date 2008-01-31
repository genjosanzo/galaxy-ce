package org.mule.galaxy.api.view;

import org.mule.galaxy.api.Artifact;

/**
 * Information necessary to display a group of similar artifacts.
 */
public interface ArtifactTypeView {
    String[] getColumnNames();

    String getColumnValue(Artifact row, int column);

    ViewLink getLink(Artifact row, int column);
    
    boolean isSummary(int column);
    
    boolean isDetail(int column);
    
    // Collection<Artifact> sort(Collection<Artifact> artifacts, int column)
}
