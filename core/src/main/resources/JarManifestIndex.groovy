println "=================== Executing a groovy index"

artifact.setProperty(index.id, 'JAR Manifest Indexed Value')
artifact.setLocked (index.id, true)

println "Done"