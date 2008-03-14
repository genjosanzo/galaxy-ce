import java.util.jar.JarEntry
import java.util.jar.JarFile
import org.mule.galaxy.impl.index.annotations.AsmAnnotationsScanner
import org.objectweb.asm.ClassReader

def time = System.&currentTimeMillis // method ref
def long start
if (log.debugEnabled) {
    start = time()
    log.debug "Indexing ${artifact.parent.name}"
}

// copy to a temp location, not happy :(
def temp = File.createTempFile('galaxy-index', 'tmp')
temp.deleteOnExit()
temp.withOutputStream {
    it << artifact.stream
}

def jarFile
try {
    jarFile = new JarFile(temp)

    def classAnnotations = []
    def fieldAnnotations = []
    def methodAnnotations = []
    def paramAnnotations = []

    // a list of jar entries
    jarFile.entries().findAll {!it.directory && it.name.endsWith('.class')}.each { JarEntry e ->
        InputStream is = jarFile.getInputStream(e)
        def classReader = new ClassReader(is)
        def scanner = new AsmAnnotationsScanner()
        classReader.accept scanner, 0
        classAnnotations += scanner.classAnnotations
        fieldAnnotations += scanner.fieldAnnotations
        methodAnnotations += scanner.methodAnnotations
        paramAnnotations += scanner.paramAnnotations

        is?.close()
    }

    // a convenience closure to reference later
    def saveAnnotations = { level, annotations ->
        if (log.debugEnabled) {
            log.debug "Saving $level annotations: $annotations"
        }

        def propertyName = "${index.id}.level.$level"
        def encodedName = URLEncoder.encode(propertyName)
        if (annotations) {
            // JCR doesn't save non-string objects by default, transform
            annotations = annotations.collect{ a -> a.toString() }
        }
        artifact.setProperty encodedName, annotations
        artifact.setLocked encodedName, true
    }

    saveAnnotations 'class', classAnnotations
    saveAnnotations 'field', fieldAnnotations
    saveAnnotations 'method', methodAnnotations
    saveAnnotations 'param', paramAnnotations

} finally {
    jarFile?.close()
    temp?.delete()
}

if (log.debugEnabled) {
    def long end = time()
    log.debug "Time taken to index: ${end - start} ms"
}