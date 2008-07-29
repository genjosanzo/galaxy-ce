package org.mule.galaxy.impl;


import java.util.Collection;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;

public class PropertyTest extends AbstractGalaxyTest {
    
    public void testTypes() throws Exception {
        Type type = typeManager.getDefaultType();
        
        assertNotNull(type);
        
        assertEquals(1, type.getProperties().size());
    }
    
    public void testProperties() throws Exception {
       importHelloWsdl();
       
       PropertyDescriptor pd = new PropertyDescriptor("location", 
                                                      "Geographic Location",
                                                      false);
       
       typeManager.savePropertyDescriptor(pd);
       assertEquals("location", pd.getProperty());
       
       PropertyDescriptor pd2 = typeManager.getPropertyDescriptor(pd.getId());
       assertNotNull(pd2);
       assertEquals(pd.getDescription(), pd2.getDescription());
       
       Collection<PropertyDescriptor> pds = typeManager.getPropertyDescriptors();
       // 12 of these are index related
//       assertEquals(23, pds.size());
       assertNotNull(pds);
       
       PropertyDescriptor pd3 = typeManager.getPropertyDescriptorByName(pd.getProperty());
       assertNotNull(pd3);
       
       pd.setId(null);
       try {
           typeManager.savePropertyDescriptor(pd);
           fail("DuplicateItemException expected");
       } catch (DuplicateItemException e) {
       }
    }

}
