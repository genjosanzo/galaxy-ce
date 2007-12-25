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
    
    public void testAddWsdl() throws Exception {
       Artifact a = importHelloWsdl();
       
       PropertyDescriptor pd = new PropertyDescriptor("location", 
                                                      "Geographic Location",
                                                      false);
       
       registry.savePropertyDescriptor(pd);
       
       PropertyDescriptor pd2 = registry.getPropertyDescriptor(pd.getName());
       assertNotNull(pd2);
       assertEquals(pd.getName(), pd2.getName());
       
       Collection<PropertyDescriptor> pds = registry.getPropertyDescriptors();
       assertEquals(1, pds.size());
       
       Object pd3 = registry.getPropertyDescriptorOrIndex(pd.getName());
       assertTrue(pd3 instanceof PropertyDescriptor);
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
