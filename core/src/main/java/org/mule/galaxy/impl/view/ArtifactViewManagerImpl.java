package org.mule.galaxy.impl.view;

import java.util.List;

import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.security.User;
import org.mule.galaxy.view.ArtifactView;
import org.mule.galaxy.view.ArtifactViewManager;

public class ArtifactViewManagerImpl 
    extends AbstractReflectionDao<ArtifactView>
    implements ArtifactViewManager {

    public ArtifactViewManagerImpl() throws Exception {
        super(ArtifactView.class, "artifactViews", true);
    }

    public List<ArtifactView> getArtifactViews(User user) {
        return find("user", user.getId());
    }
}
