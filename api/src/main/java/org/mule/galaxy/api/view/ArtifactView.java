package org.mule.galaxy.api.view;

import org.mule.galaxy.api.ArtifactVersion;

/**
 * A group of information about a version of an artifact.
 */
public interface ArtifactView<T> {

    String getTitle();
    
    String[] getColumnNames();

    Iterable<T> getRows(ArtifactVersion a);
    
    String getColumnValue(T row, int column);

    ViewLink getLink(T row, int column);
}
