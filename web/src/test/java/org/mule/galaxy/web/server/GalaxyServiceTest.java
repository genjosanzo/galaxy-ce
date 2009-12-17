package org.mule.galaxy.web.server;

import java.util.Collection;

import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.web.rpc.ApplicationInfo;
import org.mule.galaxy.web.rpc.GalaxyService;
import org.mule.galaxy.web.rpc.PluginTabInfo;
import org.mule.galaxy.web.rpc.WUser;

public class GalaxyServiceTest extends AbstractGalaxyTest {

    protected GalaxyService galaxyService;
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-core-extensions.xml", 
                              "/META-INF/applicationContext-acegi-security.xml",
                              "/META-INF/applicationContext-web.xml",
                              "/META-INF/applicationContext-test.xml" };
        
    }
    
    public void testGwtPlugins() throws Exception {
        Collection<PluginTabInfo> plugins = galaxyService.getApplicationInfo().getPluginTabs();
        assertEquals(0, plugins.size());
    }

    public void testApplicationInfo() throws Exception {
        ApplicationInfo appInfo = galaxyService.getApplicationInfo();
        WUser user = appInfo.getUser();
        
        assertNotNull(user.getUsername());
        Collection<String> permissions = user.getPermissions();
        assertTrue(permissions.size() > 0);
        
        assertTrue(permissions.contains("MANAGE_USERS"));

        assertNotNull(appInfo.getExtensions());
    }
    
}
