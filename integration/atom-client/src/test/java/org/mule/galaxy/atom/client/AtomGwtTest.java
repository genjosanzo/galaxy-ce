package org.mule.galaxy.atom.client;

import java.util.List;

import org.mule.galaxy.Item;
import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryService;
import org.mule.galaxy.repository.rpc.WSearchResults;

public class AtomGwtTest extends AbstractAtomTest {
    protected RegistryService gwtRegistry;
    
    public void testRemote() throws Exception {
        Item attached = attatchTestWorkspace();
        Item a = importFile(attached, 
                            getClass().getResourceAsStream("/wsdl/hello.wsdl"), 
                            "hello.wsdl", 
                            "1.0", 
                            "application/xml");
        
        
        WSearchResults artifacts = gwtRegistry.getArtifacts(attached.getId(), null, true, null, null, 0, 10);
        assertNotNull(artifacts);
        
        List<ItemInfo> rows = artifacts.getRows();
        assertEquals(1, rows.size());
        
        ItemInfo item = gwtRegistry.getItemInfo(a.getParent().getId(), true);
        assertEquals("hello.wsdl", item.getName());
        
        item = gwtRegistry.getItemInfo(a.getId(), true);
        assertEquals("1.0", item.getName());
        assertNotNull(item.getArtifactFeedLink());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        gwtRegistry = (RegistryService) getApplicationContext().getBean("gwtRegistry");
    }
}
