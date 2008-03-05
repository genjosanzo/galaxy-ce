package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.impl.content.JarContentHandler;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Java Archive (JAR) artifact plugin.
 */
public class JarArtifactPlugin extends AbstractArtifactPlugin implements Constants
{

    private ContentService contentService;

    public void initializeOnce() throws Exception
    {
        artifactTypeDao.save(new ArtifactType("Java Archives (JARs)", "application/java-archive"));
        System.out.println(">>> Installed JAR plugin");
    }

    public void initializeEverytime() throws Exception
    {
        // TODO figure out a nice way to upgrade, for now just a hack to re-init completely while developing
        List<ArtifactType> jarHandlers = artifactTypeDao.find("contentType", "application/java-archive");

        if (!jarHandlers.isEmpty())
        {
            System.out.printf("Found %d jar handlers, will remove them and re-register ours\n", jarHandlers.size());
            for (ArtifactType handler : jarHandlers)
            {
                artifactTypeDao.delete(handler.getId());
            }
        }

        artifactTypeDao.save(new ArtifactType("Java Archives (JARs)", "application/java-archive", new QName("application/java-archive")));
        System.out.println(">>> Updated JAR plugin");

        Map<String, String> config = new HashMap<String, String>();
        config.put("scriptSource", "C:\\projects\\mule\\galaxy\\branches\\jar-indexer\\core\\src\\main\\resources\\JarManifestIndex.groovy");

        // TODO Index revolves too much around XML, needs a serious refactoring
        Index idx = new Index("jar.manifest", "JAR Manifest", "application/java-archive",
                              new QName("application/java-archive"), // the constructor should be overloaded and QName go
                              String.class,
                              "org.mule.galaxy.impl.index.GroovyIndexer", config);


        // dynamically register jar content handler (instead of putting it in core's spring config
        contentService.registerContentHandler(new JarContentHandler());
        indexManager.save(idx, true);

    }

    public ContentService getContentService()
    {
        return contentService;
    }

    public void setContentService(final ContentService contentService)
    {
        this.contentService = contentService;
    }
}