package org.mule.galaxy.impl;

import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Workspace;
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
    }
}
