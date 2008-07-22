package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.contact.Contact;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.impl.extension.IdentifiableExtensionQueryBuilder;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class EntryTest extends AbstractGalaxyTest {
    public void testEntries() throws Exception {
        Workspace root = registry.getWorkspaces().iterator().next();
    
        EntryResult r = root.newEntry("MyService", "1.0");
        assertNotNull(r);
    
        Entry e = r.getEntry();
        assertNotNull(e);
    
        EntryVersion ev = e.getDefaultOrLastVersion();
        assertNotNull(ev);
        ev.setProperty("endpoint", "http://localhost:9000/foo");
        ev.setProperty("serviceType", "HTTP");
    
        assertEquals("1.0", ev.getVersionLabel());
        
        r = e.newVersion("2.0");
        assertNotNull(r);
        
        ev = r.getEntryVersion();
        assertNotNull(ev);
        
        assertEquals("2.0", ev.getVersionLabel());
    }
    
    public void testExtension() throws Exception {
	Workspace root = registry.getWorkspaces().iterator().next();
	
	EntryResult r = root.newEntry("MyService", "1.0");
	assertNotNull(r);
	
	PropertyDescriptor pd = new PropertyDescriptor();
	pd.setExtension((Extension) applicationContext.getBean("contactExtension"));
	pd.setDescription("Primary Contact");
	pd.setProperty("contact");
	
	registry.savePropertyDescriptor(pd);
	assertNotNull(pd.getId());
	
	pd = registry.getPropertyDescriptor(pd.getId());
	assertNotNull(pd);
	assertNotNull(pd.getExtension());
	
	Entry e = r.getEntry();
	assertNotNull(e);
	
	Contact contact = new Contact();
	contact.setName("Dan Diephouse");
	e.setProperty("contact", contact);
	
	Contact c2 = (Contact) e.getProperty("contact");
	assertNotNull(c2);
	
	IdentifiableExtensionQueryBuilder qb = (IdentifiableExtensionQueryBuilder) applicationContext.getBean("contactQueryBuilder");
	assertNotNull(qb);
	
	String[] propArray = qb.getProperties();
	List<String> props = Arrays.asList(propArray);
	
	assertTrue(props.contains("contact.name"));
	assertTrue(props.contains("contact.email"));
	
	Query q = new Query(Entry.class).add(OpRestriction.eq("contact.name", contact.getName()));
	
	SearchResults result = registry.search(q);
	
	assertEquals(1, result.getTotal());
	
    }
}
