import java.util.jar.JarFile
import java.util.jar.JarEntry

println "=================== Executing a groovy index"

// copy to a temp location, not happy :(
def temp = File.createTempFile('galaxy-index', 'tmp')
temp.withOutputStream {
    it << artifact.stream
}

def jarFile
try {
    jarFile = new JarFile(temp)

    jarFile.manifest.mainAttributes.each {
        def propertyName = "${index.id}.${it.key}"
        def encodedName = URLEncoder.encode(propertyName)

        artifact.setProperty(encodedName, it.value)
        artifact.setLocked(encodedName, true)
    }

    def entries = []
    jarFile.entries().findAll { !it.directory }.each { JarEntry e ->
        def name = e.name.replaceAll('/', '\\.') // replace / with . for classnames
        name -= '.class' // drop the trailing .class from the name
        entries << name
    }

    def propertyName = "${index.id}.entries"
    def encodedName = URLEncoder.encode(propertyName)
    artifact.setProperty encodedName, entries
    artifact.setLocked encodedName, true

} finally {
    jarFile?.close()
}

println "Done"