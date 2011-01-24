package org.mule.galaxy.impl;

import java.util.List;

import org.mule.galaxy.artifact.ArtifactType;
import org.mule.galaxy.artifact.ArtifactTypeDao;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

public class ArtifactTypeDaoTest extends AbstractGalaxyTest {
    protected ArtifactTypeDao artifactTypeDao;
    
    public void testDao() throws Exception {
        ArtifactType a = new ArtifactType();
        a.setContentType("application/wsdl+xml");
        a.setDescription("WSDL");
        a.addDocumentType(Constants.WSDL_DEFINITION_QNAME);
        
        artifactTypeDao.save(a);
        
        assertNotNull(a.getId());
        
        ArtifactType a2 = artifactTypeDao.get(a.getId());
        assertEquals(a.getId(), a2.getId());
        assertEquals(a.getDescription(), a2.getDescription());
        assertEquals(a.getContentType(), a2.getContentType());

        ArtifactType at = artifactTypeDao.getArtifactType("application/wsdl+xml",
                                                          Constants.WSDL_DEFINITION_QNAME);

        assertNotNull(at);
        
        at = artifactTypeDao.getArtifactType("foobar", null);
        
        assertNotNull(at);
        assertEquals("Other Artifacts", at.getDescription());
    }
}
