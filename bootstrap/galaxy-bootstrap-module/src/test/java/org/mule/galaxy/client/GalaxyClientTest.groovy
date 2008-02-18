/*
 * $Id$
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

package org.mule.galaxy.client

import junit.framework.TestCase
import org.mule.galaxy.client.Galaxy

class GalaxyClientTest extends TestCase {

    def defaultWorskspace = 'Default%20Workspace'

    def void testDemo() {
        def port = 8080 // override here, 8080 is the default really

        Galaxy g = new Galaxy(username: 'admin',
                password: 'admin',
                port: port)

        def jar = new URL("http://repo1.maven.org/maven2/org/mule/mule-core/2.0.0-RC2/mule-core-2.0.0-RC2.jar")

        def artifactName = jar.path.substring(jar.path.lastIndexOf('/') + 1)
        println artifactName
        //def defaultWorskspace = 'Mule/lib/mule'
        def mimeType = 'application/java-archive'

        println 'Create: ' + g.create(defaultWorskspace, mimeType, artifactName, jar.newInputStream())

        println 'Update: ' + g.update("$defaultWorskspace/$artifactName", mimeType, '2', jar.newInputStream())

        println 'Delete: ' + g.delete("$defaultWorskspace/$artifactName")

    }

    def void testCreateWorkspace() {
        Galaxy g = new Galaxy(username: 'admin', password: 'admin')

        println 'Creating a new AtomAPI workspace under Default Workspace'
        g.createWorkspace(defaultWorskspace, 'AtomAPI')
    }
}