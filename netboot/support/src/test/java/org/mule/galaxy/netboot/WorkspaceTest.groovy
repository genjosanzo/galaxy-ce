/*
* $Id: GalaxyBootstrap.groovy 1083 2008-06-19 20:36:06Z andrew $
* --------------------------------------------------------------------------------------
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.mule.galaxy.netboot

import java.io.File;

import org.mule.galaxy.client.Galaxy
import org.mule.galaxy.test.AbstractAtomTest

/**
 * 
 */
public class WorkspaceTest extends AbstractAtomTest {

    void testWorkspace() {
        def g = new Galaxy(port: 9002)
        
        def testJar = getClass().getResourceAsStream("/test.jar");
        assertNotNull(testJar);
        
        // set up a basic Mule mule layout
        g.createWorkspace("", "Mule");
        g.createWorkspace("Mule", "lib");
        g.createWorkspace("Mule/lib", "boot");
        
        // add a test jar todownload
        g.create("Mule/lib/boot", "application/java-archive", "test.jar", 
                 testJar, '1', true);
     
        // Try downloading the workspace
        def muleHome = new File("target/mule");
        System.setProperty("mule.home", muleHome.absolutePath);
        
        def w = new NetBootWorkspace(galaxy: g,
                                     name: 'boot',
                                     netBootWorkspace: "Mule",
                                     netBootCacheDir: muleHome.canonicalPath).init().process();
        
        // does the jar exist?
        def downloadedTestJar = new File(muleHome, "lib/boot/test.jar");
        assertTrue(downloadedTestJar.exists());
                              
    }
    
}
