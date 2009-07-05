package org.mule.galaxy.impl;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.TreeItem;
import org.mule.galaxy.impl.tree.TreeExtension;
import org.mule.galaxy.impl.tree.TreeItemDao;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.util.GalaxyUtils;

public class PropertyTest extends AbstractGalaxyTest {
    
    protected TreeItemDao treeItemDao;
    
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
       PropertyDescriptor pd3 = typeManager.getPropertyDescriptorByName(pd.getProperty());
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
    
    public void testTreeProperties() throws Exception {
        
        TreeItem root = new TreeItem("Public Services");
        treeItemDao.save(root);
        
        TreeItem child = root.addChild(new TreeItem("Logistics", root));
        treeItemDao.save(child);
        treeItemDao.save(root);

        TreeItem root2 = treeItemDao.get(root.getId());
        assertNotNull(root2.getChildren());
        assertEquals(1, root2.getChildren().size());
        assertTrue(root2.getChildren().iterator().next() instanceof TreeItem);
        
        // Create a property descriptor for this tree
        PropertyDescriptor pd = new PropertyDescriptor("groups", "Groups", registry.getExtension("treeExtension"));
        pd.setConfiguration(GalaxyUtils.asMap(TreeExtension.ROOT_ITEM_KEY, root.getId()));
        typeManager.savePropertyDescriptor(pd);
        
        List<TreeItem> items = treeItemDao.getRootTreeItems();
        assertEquals(1, items.size());
        
        Item item = registry.newItem("test", getSimpleType()).getItem();
        
        item.setProperty(pd.getProperty(), Arrays.asList(child));
        registry.save(item);
        
        item = registry.getItemById(item.getId());
        
        Collection<TreeItem> retreived = (Collection<TreeItem>) item.getProperty(pd.getProperty());
        assertEquals(1, retreived.size());
        TreeItem t = (TreeItem) retreived.iterator().next();
        assertEquals(child.getName(), t.getName());
        assertEquals("Public Services/Logistics", t.getFullPath());
        
        SearchResults search = registry.search("select where groups = 'Public Services/Logistics'", 0, 100);
        
        assertEquals(1, search.getTotal());
        
        search = registry.search("select where groups.id = '" + child.getId() + "'", 0, 100);
        
        assertEquals(1, search.getTotal());
    }
}
