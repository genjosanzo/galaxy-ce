package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;

public class ItemTest extends AbstractGalaxyTest {
    public void testEntries() throws Exception {
        Item root = registry.getItems().iterator().next();
        assertEquals("/Default Workspace", root.getPath());
        
        Type simpleType = getSimpleType();
        NewItemResult r = root.newItem("MyService", simpleType);
        assertNotNull(r);
    
        Item e = r.getItem();
        assertNotNull(e);
    
        e.setProperty("endpoint", "http://localhost:9000/foo");
        e.setProperty("serviceType", "HTTP");
    
        r = e.newItem("1.0", simpleType);
        assertNotNull(r);
        assertEquals("/Default Workspace/MyService/1.0", r.getItem().getPath());
        
        e = registry.getItemByPath(r.getItem().getPath());
        assertNotNull(e);

        assertEquals("/Default Workspace/MyService/1.0", e.getPath());
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
        
        NewItemResult r = root.newItem("foo", typeManager.getType(TypeManager.VERSIONED));
        assertNotNull(r);
    
        Item versioned = r.getItem();
        assertNotNull(versioned);
        assertNotNull(versioned.getType());
        assertEquals(TypeManager.VERSIONED, versioned.getType().getName());
        assertNotNull(versioned.getType().getId());
        
        r = versioned.newItem("1.0", typeManager.getType(TypeManager.VERSION));
//        Item v1 = r.getItem();
        
        r = versioned.newItem("2.0", typeManager.getType(TypeManager.VERSION));
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