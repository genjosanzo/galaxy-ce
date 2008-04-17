import org.mule.galaxy.impl.index.osgi.OsgiManifestUtil

import java.util.jar.JarFile
import java.util.jar.JarEntry

println "=================== Executing a groovy index"

// copy to a temp location, not happy :(
def temp = File.createTempFile('galaxy-index', 'tmp')
temp.deleteOnExit()
temp.withOutputStream {
    it << artifact.stream
}

// just for a start, will populate with values from org.osgi.framework.Constants
def osgiHeaders = [
        'Export-Package',
        'Import-Package',
        'Ignore-Package',
        'Private-Package',
]

def jarFile
try {
    jarFile = new JarFile(temp)

    def attrs = jarFile.manifest.mainAttributes 

    def osgiAttrs = attrs.findAll {
        osgiHeaders.contains it.key?.toString() // it's Attributes.Name class, thus the need for toString()
    }

    def nonOsgiAttrs = attrs.findAll {
        !osgiHeaders.contains(it.key?.toString())
    }

    nonOsgiAttrs.each {
        def propertyName = "jar.${it.key}"
        def encodedName = URLEncoder.encode(propertyName)

        artifact.setProperty(encodedName, it.value)
        artifact.setLocked(encodedName, true)
    }

    osgiAttrs.each {
        // TODO needs to be optimized and refactored most likely
        def List exports = OsgiManifestUtil.parseEntries(it.key.toString(), it.value, false, true, false)

        def propertyName = "jar.${it.key}.packages"
        def encodedName = URLEncoder.encode(propertyName)

        def pkgs = exports.collect { it.keys[0] }
        artifact.setProperty encodedName, pkgs
        artifact.setLocked encodedName, true

        /*
        exports.each {exp ->
            def name = exp.keys[0]
            println "$name =="
            exp.each {
                if (it.key != 'keys')
                {
                    println "$it.key = $it.value"
                }
            }

            println ''
        }
        */
    }

    def entries = []
    jarFile.entries().findAll { !it.directory }.each { JarEntry e ->
        def name = e.name.replaceAll('/', '\\.') // replace / with . for classnames
        name -= '.class' // drop the trailing .class from the name
        entries << name
    }

    def propertyName = "jar.entries"
    def encodedName = URLEncoder.encode(propertyName)
    artifact.setProperty encodedName, entries
    artifact.setLocked encodedName, true

} finally {
    jarFile?.close()
    temp?.delete()
}

println "Done"