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

import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.mule.galaxy.client.Galaxy
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException

class GalaxyBootstrap {

    def static g
    def static List workspaces
    def static netBootWorkspace
    def static boolean debug
    def static File netBootCacheDir

    def static numUnits = Runtime.runtime.availableProcessors() * 4

    def static ExecutorService exec = Executors.newFixedThreadPool(numUnits)
    def static ExecutorCompletionService compService = new ExecutorCompletionService(exec)

    static void main(args) {
        constructMuleClasspath()
    }

    static URL[] constructMuleClasspath() {
        // parse cli params
        def p = System.properties

        //def httpScheme = p.'galaxy.httpScheme' ?: 'http'
        def httpScheme = 'http' // TODO https is not yet supported
        def host = p.'galaxy.host' ?: 'localhost'
        def port = p.'galaxy.port' ?: 8080
        def apiUrl = p.'galaxy.apiUrl' ?: '/api/registry'
        def username = p.'galaxy.username' ?: 'admin'
        def password = p.'galaxy.password' ?: 'admin'
        // split by comma, prune duplicates, all in a null-safe manner
        workspaces = p.'galaxy.app.workspaces'?.split(',')?.toList()?.unique() ?: []
        netBootWorkspace = p.'galaxy.netboot.workspace' ?: 'Mule'
        debug = 'true'.equalsIgnoreCase(p.'galaxy.debug')

        // Passed in as -Dmule.home
        def muleHome = p.'mule.home'
        // create a local cache dir if needed
        File cacheDir = new File(muleHome, 'lib/cache')
        netBootCacheDir = new File(cacheDir, netBootWorkspace)
        netBootCacheDir.mkdirs()
        assert netBootCacheDir.exists()

        println """\n
${'=' * 78}
MULE_HOME: $muleHome
Local Jar Cache: $cacheDir.canonicalPath
Galaxy URL: $httpScheme://$host:$port$apiUrl
Username: $username
NetBoot Workspace: $netBootWorkspace
Application Workspaces: $workspaces
Proc Units: $numUnits
Debug: $debug
${'=' * 78}

Fetching artifacts from Galaxy...
"""

        g = new Galaxy(//httpScheme: httpScheme,
                       host: host,
                       port: new Integer(port),
                       apiUrl: apiUrl,
                       username: username,
                       password: password,
                       debug: debug)

        def urls = []
        // add $MULE_HOME/lib/user to pick up properties for now
        urls += new File(muleHome, 'lib/user').toURI().toURL()

        try {
            /*
                Use a new collection in case Galaxy goes down during the process.
                In this case we'll abandon the process immediately and try with a
                clean classpath from the cache.
             */
            def cachedUrls = []

            /*
                Process workspaces in parallel below
            */

            // lib/user
            def libUser = exec.submit({
                new NetBootWorkspace(galaxy: g,
                                  name: 'user',
                                  netBootWorkspace: netBootWorkspace,
                                  netBootCacheDir: netBootCacheDir.canonicalPath).init().process()
            } as Callable)

            // lib/mule
            def libMule = exec.submit({
                    new NetBootWorkspace(galaxy: g,
                                  name: 'mule',
                                  netBootWorkspace: netBootWorkspace,
                                  netBootCacheDir: netBootCacheDir.canonicalPath).init().process()
            } as Callable)

            // lib/opt
            def libOpt = exec.submit({
                    new NetBootWorkspace(galaxy: g,
                                  name: 'opt',
                                  netBootWorkspace: netBootWorkspace,
                                  netBootCacheDir: netBootCacheDir.canonicalPath).init().process()
            } as Callable)


            /*
                Now process application workspaces
            */

            def appWsFutures = []
            workspaces.each { String ws ->
                def name = ws.contains('/') ? ws.substring(ws.lastIndexOf('/') + 1) : ws
                def parentWorkspace = ws.contains('/') ? ws.substring(0, ws.lastIndexOf('/')) : ''


                appWsFutures << exec.submit({
                    File file = new File(cacheDir, parentWorkspace)
                    new Workspace(galaxy: g,
                                  name: name,
                                  parentWorkspace: parentWorkspace,
                                  cacheDir: file.canonicalPath).process()
                } as Callable)
            }

            urls += libUser.get() + libMule.get() + libOpt.get()
            // add application workspaces' urls
            appWsFutures.each { urls += it.get() }

        } catch (ExecutionException eex) { // wraps any execution job

            def rootCause = eex.cause?.cause // 1st cause is groovy invoker exception, next is the real one

            if (!(rootCause instanceof ConnectException)) {
                // fail-fast and rethrow
                throw rootCause
            }

            println "Galaxy server is not available, will try a local cache..."

            def fromCache = { subDir ->
                new File(cacheDir, subDir).listFiles().each { file ->
                    urls << file.toURI().toURL()
                }
            }

            // netboot cache
            fromCache("$netBootWorkspace/lib/user")
            fromCache("$netBootWorkspace/lib/mule")
            fromCache("$netBootWorkspace/lib/opt")

            // app workspaces cache
            workspaces.each { name ->
                fromCache(name)
            }
        }
        finally {
            exec.shutdown()
        }

        if (debug) { println urls.join('\n') }

        println '\nLaunching Mule, get set for a hyper-jump...\n'

        urls.toArray(new URL[urls.size()])
    }

}
    