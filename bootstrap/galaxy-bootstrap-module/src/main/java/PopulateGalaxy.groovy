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

import org.mule.galaxy.client.Galaxy
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Callable

// configure CLI
def cli = new CliBuilder(usage: '-[SHPUupwdhm]\n')
cli.S(longOpt: 'httpScheme', args: 1, 'http (default) or https. Not used yet.')
cli.h(longOpt: 'help', 'this help screen')
cli.H(longOpt: 'host', args: 1, 'Galaxy host (default: localhost)')
cli.P(longOpt: 'port', args: 1, 'Galaxy port (default: 8080)')
cli.U(longOpt: 'apiUrl', args: 1, 'Galaxy API url (default: /api/registry)')
cli.u(longOpt: 'username', args: 1, 'Galaxy username (default: admin)')
cli.p(longOpt: 'password', args: 1, 'Galaxy password (default: admin)')
cli.w(longOpt: 'workspace', args: 1, 'Galaxy workspace to configure, escaped \'Like%20this\' (default: Mule)')
cli.d(longOpt: 'deleteWorkspace', 'If specified, a workspace will be deleted first and re-created from scratch')
cli.m(longOpt: 'muleHome', args: 1, 'Override MULE_HOME, (default: value of the MULE_HOME env property)')
cli.X(longOpt: 'debug', 'If enabled, prints debug info at runtime')

def opts = cli.parse(args)

if (!opts) {
    System.exit(-1)// if there was an error, exit
}

if (opts.h) {
    println '''\nPush Mule snapshot to Galaxy\n''' 
    cli.usage()
    System.exit(0)
}

// some definitions and constants
def httpScheme = opts.S ?: 'http'
def host = opts.H ?: 'localhost'
def port = opts.P ?: 8080
def apiUrl = opts.U ?: '/api/registry'
def username = opts.u ?: 'admin'
def password = opts.p ?: 'admin'
def workspace = opts.w ?: 'Mule'
def deleteWorkspace = opts.d
def debug = opts.X

// Passed in as -Dmule.home
def muleHome = opts.m ?: System.properties.'mule.home'

def lib = "$muleHome/lib"
def libMule = "$lib/mule"
def libOpt = "$lib/opt"

def mimeType = "application/java-archive"

// TODO there seems to be a race condition in Abdera, it's failing with IllegalStateException under load
def numUnits = 1
//def numUnits = Runtime.runtime.availableProcessors() * 4

def ExecutorService exec = Executors.newFixedThreadPool(numUnits)
def ExecutorCompletionService compService = new ExecutorCompletionService(exec)

println """
${'=' * 78}
MULE_HOME: $muleHome
Galaxy URL: $httpScheme://$host:$port$apiUrl
Username: $username
Workspace: $workspace
Delete Workspace?: $deleteWorkspace
Proc Units: $numUnits
${'=' * 78}
"""

// TODO httpScheme is not used yet

// Galaxy client
def galaxy = new Galaxy(username: username,
                        password: password,
                        host: host,
                        port: port,
                        apiUrl: apiUrl,
                        debug: debug)


// helper methods/closures

int totalCount = 0
def upload = { libFolder ->
    new File(lib, libFolder).eachFileMatch( ~/.*\.jar$/ ) { file ->
        def task = {
            println file.name
            galaxy.create("$workspace/lib/$libFolder", mimeType, file.name, file.newInputStream())
        } as Callable

        compService.submit task

        totalCount++
    }
}

// actual body


if (deleteWorkspace) {
    galaxy.delete(workspace)
}

galaxy.createWorkspace(null, workspace) // create under root

// mirroring $MULE_HOME/lib
galaxy.createWorkspace(workspace, "lib")
galaxy.createWorkspace("$workspace/lib", "mule")
galaxy.createWorkspace("$workspace/lib", "opt")
galaxy.createWorkspace("$workspace/lib", "user")

upload('mule')
upload('opt')
upload('user')

totalCount.times {
    // just ensure all submitted ones finish
    compService.take().get()
}

exec.shutdown()