package org.mule.galaxy.impl;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.impl.jcr.ArtifactTypeDaoImpl;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

import java.util.List;

public class ArtifactTypeDaoTest extends AbstractGalaxyTest {
    protected ArtifactTypeDaoImpl artifactTypeDao;
    
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
    
    public void testRegistration() throws Exception {
        List<ArtifactType> all = artifactTypeDao.listAll();
        System.out.println(all);
        assertTrue(all.size() > 5);
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
