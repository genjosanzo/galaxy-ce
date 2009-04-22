import java.util.jar.JarEntry
import java.util.jar.JarFile
import org.mule.galaxy.impl.index.osgi.OsgiManifestUtil

def time = System.&currentTimeMillis // method ref
def long start
if (log.debugEnabled) {
    start = time()
    log.debug "Indexing ${item.parent.name}"
}

// copy to a temp location, not happy :(
def temp = File.createTempFile('galaxy-index', 'tmp')
temp.deleteOnExit()
temp.withOutputStream {
    it << artifact.inputStream
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

    // a list of jar entries
    def entries = []
    jarFile.entries().findAll { !it.directory }.each { JarEntry e ->
        def name = e.name.replaceAll('/', '\\.') // replace / with . for classnames
        name -= '.class' // drop the trailing .class from the name
        entries << name
    }

    def propertyName = "jar.entries"
    def encodedName = URLEncoder.encode(propertyName)
    item.setProperty encodedName, entries
    item.setLocked encodedName, true
    item.setVisible encodedName, false

    // check if the jar has a manifest
    def manifest = jarFile.manifest
    if (!manifest) {
        if (log.debugEnabled) {
            log.debug "[${artifact.parent.name}] doesn't have a manifest, nothing else to index here"
        }

        return
    }

    def attrs = manifest.mainAttributes

    def osgiAttrs = attrs.findAll {
        osgiHeaders.contains it.key?.toString() // it's Attributes.Name class, thus the need for toString()
    }

    def nonOsgiAttrs = attrs.findAll {
        !osgiHeaders.contains(it.key?.toString())
    }

    nonOsgiAttrs.each {
        propertyName = "jar.manifest.${it.key}"
        encodedName = URLEncoder.encode(propertyName)

        item.setProperty(encodedName, it.value)
        item.setLocked(encodedName, true)
    }

    osgiAttrs.each {
        // TODO needs to be optimized and refactored most likely
        def List exports = OsgiManifestUtil.parseEntries(it.key.toString(), it.value, false, true, false)

        propertyName = "jar.osgi.${it.key}.packages"
        encodedName = URLEncoder.encode(propertyName)

        def pkgs = exports.collect { it.keys[0] }
        item.setProperty encodedName, pkgs
        item.setLocked encodedName, true

        /*
        exports.each {exp ->                                                          mani
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

} finally {
    jarFile?.close()
    temp?.delete()
}

if (log.debugEnabled) {
    def long end = time()
    log.debug "Time taken to index: ${end - start} ms"
}