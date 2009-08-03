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

package org.mule.galaxy.netboot

import com.google.gdata.util.httputil.FastURLEncoder

/**
    A convenience class defaulting some values for NetBoot system workspaces.
*/
class NetBootWorkspace extends Workspace {

    def String netBootWorkspace
    def String netBootCacheDir

    // need a post-constructor call because those props above aren't yet initialised when
    // we push in a value map :/
    def Workspace init() {
        def safe = FastURLEncoder.createSafeOctetBitSet() // don't make latin chars unreadable, only special ones
        def encodedName = FastURLEncoder.encode(netBootWorkspace, safe, false) // space as %20, not +
        parentWorkspace = "$encodedName/lib"
        cacheDir = new File(netBootCacheDir, 'lib').canonicalPath

        this
    }

}