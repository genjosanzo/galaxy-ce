package org.mule.galaxy.impl;


import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.Dao;
import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class PropertyTest extends AbstractGalaxyTest {
    protected Dao<PropertyDescriptor> propertyDescriptorDao;
    
    public void testProperties() throws Exception {
       importHelloWsdl();
       
       PropertyDescriptor pd = new PropertyDescriptor("location", 
                                                      "Geographic Location",
                                                      false);
       
       registry.savePropertyDescriptor(pd);
       assertEquals("location", pd.getProperty());
       
       PropertyDescriptor pd2 = registry.getPropertyDescriptor(pd.getProperty());
       assertNotNull(pd2);
       assertEquals(pd.getDescription(), pd2.getDescription());
       
       Collection<PropertyDescriptor> pds = registry.getPropertyDescriptors();
       assertEquals(1, pds.size());
       
       Object pd3 = registry.getPropertyDescriptorOrIndex(pd.getProperty());
       assertTrue(pd3 instanceof PropertyDescriptor);
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
