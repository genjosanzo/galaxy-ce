import java.util.jar.JarFile

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
} finally {
    jarFile?.close()
}

println "Done"