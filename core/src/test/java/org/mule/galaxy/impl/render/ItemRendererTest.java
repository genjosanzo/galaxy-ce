package org.mule.galaxy.impl.render;

import org.mule.galaxy.Item;
import org.mule.galaxy.render.RendererManager;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class ItemRendererTest extends AbstractGalaxyTest {
    protected RendererManager rendererManager;

    public void testView() throws Exception {
        assertNotNull(rendererManager);
        
        Item wsdl = importHelloWsdl();
        CustomItemRenderer view = (CustomItemRenderer) rendererManager.getRenderer(wsdl);
        
        assertEquals(6, view.getColumns().size());
 
        // Check and see if our view works
        String[] columns = view.getColumnNames();
        assertEquals(6, columns.length);
        for (String c : columns) System.out.println(c);
        String name = wsdl.getName();
        assertEquals(name, view.getColumnValue(wsdl, 0));
        assertEquals("/Default Workspace/hello_world.wsdl", view.getColumnValue(wsdl, 1));
        assertEquals("http://mule.org/hello_world", view.getColumnValue(wsdl, 2));
        assertEquals("1", view.getColumnValue(wsdl, 3));
    }
}
