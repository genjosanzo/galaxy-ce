package org.mule.galaxy.view;

import java.util.List;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.security.User;

public interface ArtifactViewManager {
    List<ArtifactView> getArtifactViews(User user);
    
    void save(ArtifactView view) throws DuplicateItemException, NotFoundException;
    
    void delete(String id);

    ArtifactView getArtifactView(String id);
}
