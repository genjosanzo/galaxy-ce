package org.mule.galaxy.impl;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;

public class PropertyTest extends AbstractGalaxyTest {
    
    public void testTypes() throws Exception {
        Type type = typeManager.getDefaultType();
        
        assertNotNull(type);
        assertNotNull(type.getId());
        
        assertEquals(0, type.getProperties().size());
    }
    
    public void testProperties2() throws Exception {
        typeManager.getGlobalPropertyDescriptors(true);
        typeManager.getGlobalPropertyDescriptors(true);
        
        PropertyDescriptor pd = new PropertyDescriptor("location", 
                                                       "Geographic Location",
                                                       false,
                                                       false);
        
        typeManager.savePropertyDescriptor(pd);
        assertEquals("location", pd.getProperty());
        assertNotSame("location", pd.getId());
        
        typeManager.deletePropertyDescriptor(pd.getId());
    }

    public void testRenameProperty() throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor("location", 
                                                       "Geographic Location",
                                                       false,
                                                       false);
        
        typeManager.savePropertyDescriptor(pd);
        assertEquals("location", pd.getProperty());
        
        Item item = registry.newItem("test", getSimpleType()).getItem();
        item.setProperty("location", "Grand Rapids, MI");
        registry.save(item);
        
        pd.setProperty("newLocation");
        typeManager.savePropertyDescriptor(pd);
        
        item = registry.getItemById(item.getId());
        assertNull(item.getProperty("location"));
        assertEquals("Grand Rapids, MI", item.getProperty("newLocation"));
    }
    
    public void testProperties() throws Exception {
       importHelloWsdl();
       
       PropertyDescriptor pd = new PropertyDescriptor("location", 
                                                      "Geographic Location",
                                                      false,
                                                      false);
       
       typeManager.savePropertyDescriptor(pd);
       assertEquals("location", pd.getProperty());
       
       PropertyDescriptor pd2 = typeManager.getPropertyDescriptor(pd.getId());
       assertNotNull(pd2);
       assertEquals(pd.getDescription(), pd2.getDescription());
       
       Collection<PropertyDescriptor> pds = typeManager.getGlobalPropertyDescriptors(true);
       // 12 of these are index related
//       assertEquals(23, pds.size());
       assertNotNull(pds);
       
       pds = typeManager.getGlobalPropertyDescriptors(true);
       PropertyDescriptor pd3 = typeManager.getPropertyDescriptorByName(pd.getProperty(), null);
       assertNotNull(pd3);
       
       pd.setId(null);
       try {
           typeManager.savePropertyDescriptor(pd);
           fail("DuplicateItemException expected");
       } catch (DuplicateItemException e) {
       }
    }

    
    public void testTypeProperties() throws Exception {
       Type type = new Type();
       type.setName("Test Type");
       
       // we have to save the type here so it can get an ID, so we can use that
       // when creating a PropertyDescriptor.
       typeManager.saveType(type);
       
       assertNotNull(type.getId());
       
       // This property is specific to this type
       PropertyDescriptor pd = new PropertyDescriptor("test", "Test");
       pd.setType(type);
       type.setProperties(Arrays.asList(pd));
       
       typeManager.saveType(type);
       typeManager.savePropertyDescriptor(pd);
       
       PropertyDescriptor pd2 = new PropertyDescriptor("test", "Test");
       pd2.setType(type);
       try {
           // ensure that our node names are being generated correct so we get duplicate exceptions
           typeManager.savePropertyDescriptor(pd2);
           fail("That was a duplicate!");
       } catch (DuplicateItemException e) {
       }
       
       Map<String,Object> props = new HashMap<String,Object>();
       props.put(pd.getProperty(), "foo");
       
       Item item = registry.newItem("test", type, props).getItem();
       
       PropertyInfo info = item.getPropertyInfo("test");
       assertEquals(pd.getId(), info.getPropertyDescriptor().getId());
       
       Collection<PropertyDescriptor> pds = typeManager.getPropertyDescriptors(type);
       assertEquals(1, pds.size());
       
       pds = typeManager.getGlobalPropertyDescriptors(false);
       assertFalse(pds.contains(pd));
    }
}
