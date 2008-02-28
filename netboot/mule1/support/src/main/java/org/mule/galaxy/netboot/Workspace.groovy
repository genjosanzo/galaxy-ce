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

import java.util.concurrent.Callable
import org.apache.commons.httpclient.methods.GetMethod
import org.mule.galaxy.client.Galaxy
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import com.google.gdata.util.httputil.FastURLEncoder


class Workspace {

    def String parentWorkspace = ''
    def String name
    def String cacheDir
    def String query = "select artifact where artifact.contentType='application/java-archive'"
    
    def Galaxy galaxy

    def boolean debug = false

    def List<Voter> voters = []

    def numUnits = Runtime.runtime.availableProcessors() * 4

    def ExecutorService exec = Executors.newFixedThreadPool(numUnits)
    def ExecutorCompletionService compService = new ExecutorCompletionService(exec)


    def List<URL> process() {

        GetMethod response

        try {
            def enc = FastURLEncoder.&encode // just a method shortcut for readability
            def safe = FastURLEncoder.createSafeOctetBitSet() // don't make latin chars unreadable, only special ones
            def encodedQuery = enc(query, safe, true) // space as +
            def encodedName = enc(name, safe, false) // space as %20, not +
            def relativeUrl = parentWorkspace.size() > 0 ? "$parentWorkspace/$encodedName?q=$encodedQuery" : "$encodedName?q=$encodedQuery"
            response = galaxy.get(relativeUrl)

            // local cache dir
            def dir = new File(cacheDir, name)
            dir.mkdirs()
            assert dir.exists()

            def feed = new XmlSlurper().parse(response.responseBodyAsStream)

            int totalCount = 0
            feed.entry.each {
                // close on a local var for it to be accessible in Callable, fixes NPE
                def node = it
                def task = {
                    URL url = new URL("http://${galaxy.host}:$galaxy.port${node.content.@src}")

                    def jarName = node.title.text()

                    File localJar = new File(dir, jarName)

                    // TODO plug voters here to decide if it needs to be downloaded really
                    if (lastUpdatedVote (localJar, node)) {
                        println "Updating a local copy of $jarName"
                        def jarRelativeUrl = parentWorkspace.size() > 0 ? "$parentWorkspace/$encodedName/$jarName" : "$encodedName/$jarName"
                        GetMethod content = galaxy.get(jarRelativeUrl)
                        try {
                            // stream to a temp location to protect startup from corrupted jars
                            // TODO this is the place to plug checksum verification
                            final File downloadsDir = new File(System.properties.'mule.home', 'lib/cache/_temp_downloads')
                            downloadsDir.mkdirs()
                            assert downloadsDir.exists()
                            File tempJar = File.createTempFile('mule-netboot-', null, downloadsDir)
                            tempJar.deleteOnExit()

                            tempJar.withOutputStream { it << content.responseBodyAsStream }

                            if (localJar.exists()) {
                                // overwriting a file means implicit delete first
                                localJar.delete()
                            }
                            assert tempJar.renameTo(localJar)
                        } finally {
                            content?.releaseConnection()
                        }
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

            return urls
        } finally {
            exec.shutdown()
            response?.releaseConnection()
        }

    }

    // return true if update from Galaxy is required
    def boolean lastUpdatedVote (File localFile, atomEntryNode) {
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
