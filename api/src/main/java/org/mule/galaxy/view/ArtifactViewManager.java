package org.mule.galaxy.view;

import java.util.List;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.security.User;

public interface ArtifactViewManager {
    List<View> getArtifactViews(User user);
    
    void save(View view) throws DuplicateItemException, NotFoundException;
    
    void delete(String id);

    View getArtifactView(String id) throws NotFoundException;
}
