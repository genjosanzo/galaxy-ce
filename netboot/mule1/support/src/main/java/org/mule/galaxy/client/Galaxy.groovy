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

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpMethod
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.methods.DeleteMethod
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.methods.InputStreamRequestEntity
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.PutMethod
import org.apache.commons.httpclient.methods.StringRequestEntity
import java.text.SimpleDateFormat

class Galaxy {

    String host = 'localhost'
    int port = 8080
    String apiUrl = '/api/registry'

    String username = 'admin'
    String password = 'admin'

    boolean debug = false

    def methods = [
            get: { path ->
                def get = new GetMethod("http://$host:$port$apiUrl/$path")
                remoteCall(get, {status, responseStream ->
                    assert status >= 200 && status < 400
                    return get
                })
            },

            create: {path, contentType, artifactName, InputStream body ->

                def post = new PostMethod("http://$host:$port$apiUrl/$path")
                post.addRequestHeader 'Content-Type', contentType
                // TODO Probably allow for customizing the initial version label
                post.addRequestHeader 'X-Artifact-Version', '1'
                post.addRequestHeader 'Slug', artifactName

                assert body != null

                post.requestEntity = new InputStreamRequestEntity(body)
                remoteCall(post, {status, responseStream ->
                    assert status == 201
                    status
                })
            },

            createWorkspace: {parent, name ->

                def workspace = parent ? "/$parent" : '' // workaround for GALAXY-97

                def post = new PostMethod("http://$host:$port$apiUrl${workspace};workspaces")
                post.addRequestHeader 'Content-Type', 'application/atom+xml;type=entry'

                // not thread-safe, create a local instance
                def iso8601Date = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:sssZ")

                // TODO UUID
                def entry = """<?xml version='1.0' encoding='UTF-8'?>
                    <entry xmlns="http://www.w3.org/2005/Atom">
                      <title type="text">$name</title>
                      <updated>${iso8601Date.format(new Date())}</updated>
                      <author>
                        <name>$username</name>
                      </author>
                      <id>urn:uuid:F2FEB15CD2E3B755911201905042360</id>
                      <content type="text"></content>
                    </entry>
                """

                if (debug) {
                    println entry
                }

                post.requestEntity = new StringRequestEntity(entry)

                remoteCall(post, {status, responseStream ->
                    assert status == 201
                    status
                })
            },

            delete: {path ->
                def delete = new DeleteMethod("http://$host:$port$apiUrl/$path")
                remoteCall(delete, {status, responseStream ->
                    //assert status == 204
                    status
                })
            },

            update: {path, contentType, newVersion, InputStream body ->
                def put = new PutMethod("http://$host:$port$apiUrl/$path")
                put.addRequestHeader 'Content-Type', contentType
                put.addRequestHeader 'X-Artifact-Version', newVersion

                assert body != null
                put.requestEntity = new InputStreamRequestEntity(body)
                
                remoteCall(put, {status, responseStream ->
                    assert status == 200
                    status
                })
            }
    ]

    def remoteCall(HttpMethod method, Closure responseHandler) {

        def client = new HttpClient()
        client.params.authenticationPreemptive = true
        def auth = new UsernamePasswordCredentials(username, password)
        client.state.setCredentials(new AuthScope(host, port, ''), auth)

        method.doAuthentication = true
        responseHandler(client.executeMethod(method), method.responseBodyAsStream)
    }

    public Object invokeMethod(String name, Object params) {
        def method = methods[name]
        if (debug) {
            println "{$name} call params: $params"
        }
        return method(*params.toList())
    }

}