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

import org.apache.commons.httpclient.methods.GetMethod
import org.mule.galaxy.client.Galaxy
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorCompletionService
import java.text.SimpleDateFormat

class GalaxyBootstrap {

    def static g
    def static workspace
    def static boolean debug
    def static File cacheDir

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
        workspace = p.'galaxy.workspace' ?: 'Mule'
        debug = 'true'.equalsIgnoreCase(p.'galaxy.debug')

        // Passed in as -Dmule.home
        def muleHome = p.'mule.home'
        // create a local cache dir if needed
        cacheDir = new File(muleHome, "lib/cache/$workspace")
        cacheDir.mkdirs()
        assert cacheDir.exists()

        println """\n
${'=' * 78}
MULE_HOME: $muleHome
Local Jar Cache: $cacheDir.canonicalPath
Galaxy URL: $httpScheme://$host:$port$apiUrl
Username: $username
Workspace: $workspace
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
            cachedUrls += processWorkspace('user')
            cachedUrls += processWorkspace('mule')
            cachedUrls += processWorkspace('opt')

            urls += cachedUrls
        } catch (ConnectException cex) {
            println "Galaxy server is not available, will try a local cache..."
            new File(cacheDir, 'lib/user').listFiles().each { file ->
                urls << file.toURI().toURL()
            }
            new File(cacheDir, 'lib/mule').listFiles().each { file ->
                urls << file.toURI().toURL()
            }
            new File(cacheDir, 'lib/opt').listFiles().each { file ->
                urls << file.toURI().toURL()
            }
        }

        exec.shutdown()

        println '\nLaunching Mule, get set for a hyper-jump...\n'

        urls.toArray(new URL[urls.size()])
    }

    private static processWorkspace(name) {
        GetMethod response = g.get("$workspace/lib/$name")

        // local cache dir
        def dir = new File(cacheDir, "lib/$name")
        dir.mkdirs()
        assert dir.exists()

        def feed = new XmlSlurper().parse(response.responseBodyAsStream)

        int totalCount = 0
        feed.entry.each {
            // close on a local var for it to be accessible in Callable, fixes NPE
            def node = it
            def task = {
                URL url = new URL("http://${g.host}:$g.port${node.content.@src}")

                def jarName = node.title.text()

                // TODO plug voters here to decide if it needs to be downloaded really
                // cache jars locally
                File localJar = new File(dir, jarName)

                if (lastUpdatedVote (localJar, node)) {
                    println "Updating a local copy of $jarName"
                    GetMethod content = g.get("$workspace/lib/$name/$jarName")
                    localJar.newOutputStream() << content.responseBodyAsStream
                    content.releaseConnection()
                }

                // fix broken URLs for File class before Java 6
                return localJar.toURI().toURL()
            } as Callable

            compService.submit task

            totalCount++
        } 

        // locally cached jar urls
        def urls = []
        totalCount.times {
            urls << compService.take().get()
        }

        response.releaseConnection()

        urls
    }

    // return true if update from Galaxy is required
    def static boolean lastUpdatedVote (File localFile, atomEntryNode) {
        boolean vote = false
        if (!localFile || !localFile.exists()) {
            vote = true
        }

        // not thread-safe, create a local instance
        // Galaxy returns update timestamps in GMT
        def iso8601Date = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:sss")
        iso8601Date.setTimeZone(TimeZone.getTimeZone('GMT'))

        Date galaxyUpdate = iso8601Date.parse (atomEntryNode.updated.text())

        final Date localFileUpdate = new Date(localFile.lastModified())
        vote = galaxyUpdate.after(localFileUpdate)
        if (debug) {
            println "$localFile.name >>$vote<<  galaxyUpdate: $galaxyUpdate || localFile: ${localFileUpdate}"
        }

        return vote
    }
}
    