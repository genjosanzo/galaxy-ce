package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;

public class ItemTest extends AbstractGalaxyTest {
    public void testItems() throws Exception {
        Item root = registry.getItems().iterator().next();
        assertEquals("/Default Workspace", root.getPath());
        
        Type simpleType = getSimpleType();
        NewItemResult r = root.newItem("MyService", simpleType);
        assertNotNull(r);
    
        Item e = r.getItem();
        assertNotNull(e);
    
        PropertyDescriptor pd = new PropertyDescriptor("endpoint", "endpoint");
        typeManager.savePropertyDescriptor(pd);
        e.setProperty("endpoint", "http://localhost:9000/foo");
    
        r = e.newItem("1.0", simpleType);
        assertNotNull(r);
        assertEquals("/Default Workspace/MyService/1.0", r.getItem().getPath());
        assertEquals("1.0",  r.getItem().getName());
        
        e = registry.getItemByPath(r.getItem().getPath());
        assertNotNull(e);

        assertEquals("/Default Workspace/MyService/1.0", e.getPath());
        
        Item v2 = e.getParent().newItem("2.0", simpleType).getItem();
        Item prev = v2.getPrevious();
        assertNotNull(prev);
        assertEquals("1.0", prev.getName());
        
        Item v3 = e.getParent().newItem("3.0", simpleType).getItem();
        prev = v3.getPrevious();
        assertNotNull(prev);
        assertEquals("2.0", prev.getName());
        
        // test all the characters. [] is not allowed.
        r = e.newItem("!@#$%^&*()'_+`-=<>.,{}|\\", simpleType);
        assertNotNull(r);
        assertEquals("/Default Workspace/MyService/1.0/!@#$%^&*()'_+`-=<>.,{}|\\", r.getItem().getPath());
        assertEquals("!@#$%^&*()'_+`-=<>.,{}|\\",  r.getItem().getName());
     
        Item item = root.getItem("MyService").getItem("1.0").getItem("!@#$%^&*()'_+`-=<>.,{}|\\");
        assertNotNull(item);
        
        SearchResults results = registry.search(new Query().add(OpRestriction.eq("name", e.getName())));
        assertEquals(1, results.getTotal());
         
    }
    
    public void testAllowedChildren() throws Exception {
        Item root = registry.getItems().iterator().next();
        assertEquals("/Default Workspace", root.getPath());
        
        NewItemResult r = root.newItem("MyService", typeManager.getTypeByName(TypeManager.VERSIONED));
        assertNotNull(r);
    
        Item svc = r.getItem();
        assertNotNull(svc);
    
        Type simpleType = getSimpleType();
        try {
            svc.newItem("1.0", simpleType);
            fail("this type is not allowed here");
        } catch (PolicyException ex) {
        }
        
        Item child = root.newItem("1.0", simpleType).getItem();
        try {
            registry.move(child, svc.getPath(), "1.0");
            fail("this type is not allowed here");
        } catch (PolicyException ex) {
        }
    }
    
    public void testTypeRequirements() throws Exception {
        Item root = registry.getItems().iterator().next();
        assertEquals("/Default Workspace", root.getPath());
        
        PropertyDescriptor pd = new PropertyDescriptor();
        pd.setProperty("URL");
        typeManager.savePropertyDescriptor(pd);
        
        Type type = new Type();
        type.setName("Service");
        type.setProperties(Arrays.asList(pd));
        typeManager.saveType(type);
        
        try {
            root.newItem("MyService", type);
            fail("Expected property exception");
        } catch (PropertyException e) {
        }

        Map<String,Object> props = new HashMap<String, Object>();
        props.put("URL", "http://test");
        root.newItem("MyService", type, props);
    }
    
    public void testVersionResolution() throws Exception {
        Item root = registry.getItems().iterator().next();
        
        NewItemResult r = root.newItem("foo", typeManager.getTypeByName(TypeManager.VERSIONED));
        assertNotNull(r);
    
        Item versioned = r.getItem();
        assertNotNull(versioned);
        assertNotNull(versioned.getType());
        assertEquals(TypeManager.VERSIONED, versioned.getType().getName());
        assertNotNull(versioned.getType().getId());
        
        r = versioned.newItem("1.0", typeManager.getTypeByName(TypeManager.VERSION));
//        Item v1 = r.getItem();
        
        r = versioned.newItem("2.0", typeManager.getTypeByName(TypeManager.VERSION));
//        Item v2 = r.getItem();
//        
//        Item result = registry.resolve(root, "./foo?version=default");
//        assertEquals(v2.getId(), result.getId());
//
//        result = registry.resolve(root, "foo/1.0");
//        assertEquals(v1.getId(), result.getId());
//        
//        versioned.setProperty(TypeManager.DEFAULT_VERSION, v1);
//        result = registry.resolve(root, "foo?version=default");
//        assertEquals(v1.getId(), result.getId());        
    }

    public void testQueries() throws Exception {
        Item root = registry.getItems().iterator().next();
    
        NewItemResult r = root.newItem("MyService",  getSimpleType());
        assertNotNull(r);
    
        Item e = r.getItem();
        assertNotNull(e);

        PropertyDescriptor pd = new PropertyDescriptor("endpoint", "endpoint");
        typeManager.savePropertyDescriptor(pd);
        
        String address = "http://localhost:9000/foo";
        e.setProperty("endpoint", address);
        registry.save(e);
        
        Query q = new Query().add(OpRestriction.eq("endpoint", address));
        
        SearchResults results = registry.search(q);
        
        assertEquals(1, results.getTotal());
        
        q = new Query().add(OpRestriction.eq("name", e.getName()));
        
        results = registry.search(q);
        assertEquals(1, results.getTotal());
        
    }

}
