package org.mule.galaxy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Index;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

public class QueryTest extends AbstractGalaxyTest {
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
        
    }
    
    public void testQueries() throws Exception {
        // Import a document which should now be indexed
        Artifact wsdlArtifact = importHelloWsdl();
        
        // Import a document which should now be indexed
        Artifact muleArtifact = importHelloMule();
        
        Workspace workspace = muleArtifact.getWorkspace();
        
        // Try out search!
        Set results = registry.search(new Query(Artifact.class).workspace(workspace.getId()));
        
        assertEquals(2, results.size());
        
        results = registry.search(new Query(Artifact.class)
            .workspace(workspace.getId())
                 .add(Restriction.eq("mule.service", "GreeterUMO"))
                 .add(Restriction.eq("documentType", Constants.MULE_QNAME)));
        assertEquals(1, results.size());
        
        results = registry.search(new Query(Artifact.class)
            .workspace(workspace.getId())
                 .add(Restriction.not(Restriction.eq("documentType", Constants.MULE_QNAME))));
        assertEquals(1, results.size());
        
        results = registry.search(new Query(Artifact.class)
            .workspace(workspace.getId())
                 .add(Restriction.like("mule.service", "Greeter")));
        assertEquals(1, results.size());
        
    }

}
