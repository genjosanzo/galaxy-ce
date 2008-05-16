package org.mule.galaxy.impl.view;

import java.io.IOException;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.security.User;
import org.mule.galaxy.view.ArtifactView;
import org.mule.galaxy.view.ArtifactViewManager;
import org.springmodules.jcr.JcrCallback;

public class ArtifactViewManagerImpl 
    extends AbstractReflectionDao<ArtifactView>
    implements ArtifactViewManager {

    public ArtifactViewManagerImpl() throws Exception {
        super(ArtifactView.class, "artifactViews", true);
    }

    @SuppressWarnings("unchecked")
    public List<ArtifactView> getArtifactViews(final User user) {
        return (List<ArtifactView>) execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return query("//element(*, galaxy:artifactView)[not(@user) or user = '" + user.getId() + "']", session);
            }
            
        });
    }

    public ArtifactView getArtifactView(String id) {
        return get(id);
    }

    @Override
    protected String getNodeType() {
        return "galaxy:artifactView";
    }
    
}
