package org.mule.galaxy.impl.view;

import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.security.User;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.view.ArtifactView;

public class ArtifactViewTest extends AbstractGalaxyTest {
    public void testView() throws Exception {
        ArtifactView view = new ArtifactView();
        view.setQuery("select artifact from '/Default Workspace'");
        view.setName("Default Workspace Selection");
        
        artifactViewManager.save(view);
        
        assertNotNull(view.getId());
        
        User admin = getAdmin();
        view.setUser(admin);
        artifactViewManager.save(view);
        
        List<ArtifactView> views = artifactViewManager.getArtifactViews(admin);
        assertEquals(1, views.size());
        
        Query query = new Query(Artifact.class);
        query.add(OpRestriction.like("wsdl.service", "Hello"));
        
        view.setQuery(query.toString());
        
        importHelloWsdl();
        
        SearchResults searchResults = registry.search(view.getQuery(), 0, -1);
        
        assertEquals(1, searchResults.getTotal());
    }
}
