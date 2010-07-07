package org.mule.galaxy.atom.client;

import java.util.Collection;
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
        
        ItemInfo item = gwtRegistry.getItemInfo(a.getParent().getId(), true);
        assertEquals("hello.wsdl", item.getName());
        
        item = gwtRegistry.getItemInfo(a.getId(), true);
        assertEquals("1.0", item.getName());
        assertNotNull(item.getArtifactFeedLink());
        
        Collection<ItemInfo> items = gwtRegistry.getItems(attached.getId(), false);
        assertEquals(1, items.size());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        gwtRegistry = (RegistryService) getApplicationContext().getBean("gwtRegistry");
    }
}
