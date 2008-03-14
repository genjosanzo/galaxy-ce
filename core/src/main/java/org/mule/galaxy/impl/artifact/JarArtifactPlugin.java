package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.GalaxyException;
import org.mule.galaxy.impl.content.JarContentHandler;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.util.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

/**
 * Java Archive (JAR) artifact plugin.
 */
public class JarArtifactPlugin extends AbstractArtifactPlugin implements Constants
{

    private final Log log = LogFactory.getLog(getClass());

    private ContentService contentService;

    private JcrTemplate jcrTemplate;

    public void initializeOnce() throws Exception
    {
        artifactTypeDao.save(new ArtifactType("Java Archives (JARs)", "application/java-archive"));
        log.info(("Installed JAR plugin"));
    }

    public void initializeEverytime() throws Exception
    {
        JcrUtil.doInTransaction(jcrTemplate.getSessionFactory(), new JcrCallback()
        {
            public Object doInJcr(final Session session) throws IOException, RepositoryException
            {

                // TODO figure out a nice way to upgrade, for now just a hack to re-init completely while developing
                List<ArtifactType> jarHandlers = artifactTypeDao.find("contentType", "application/java-archive");


                if (!jarHandlers.isEmpty())
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug(String.format("Found %d jar handlers, will remove them and re-register ours", jarHandlers.size()));
                    }
                    for (ArtifactType handler : jarHandlers)
                    {
                        artifactTypeDao.delete(handler.getId());
                    }
                }

                artifactTypeDao.save(new ArtifactType("Java Archives (JARs)", "application/java-archive", new QName("application/java-archive")));
                if (log.isDebugEnabled())
                {
                    log.info("Updated JAR plugin");
                }

                // TODO this should probably go, not really used now
                // dynamically register jar content handler (instead of putting it in core's spring config
                contentService.registerContentHandler(new JarContentHandler());

                // Configure and register a JarIndex
                Map<String, String> manifestIndexConfig = new HashMap<String, String>();
                manifestIndexConfig.put("scriptSource", "JarIndex.groovy");

                // TODO Index revolves too much around XML, needs a serious refactoring
                Index jarIndex = new Index("jar", "JAR", "application/java-archive",
                                      new QName("application/java-archive"), // the constructor should be overloaded and QName go
                                      String.class,
                                      "org.mule.galaxy.impl.index.GroovyIndexer", manifestIndexConfig);



                // Configure and register Java Annotations indexer
                Map<String, String> annIndexConfig = new HashMap<String, String>();
                annIndexConfig.put("scriptSource", "JavaAnnotationsIndex.groovy");

                Index annotationsIndex = new Index("java.annotations", "Java Annotations", "application/java-archive",
                                       new QName("application/java-archive"), // the constructor should be overloaded and QName go
                                       String.class,
                                       "org.mule.galaxy.impl.index.GroovyIndexer", annIndexConfig);

                try
                {
                    indexManager.save(jarIndex, true);
                    indexManager.save(annotationsIndex, true);
                }
                catch (GalaxyException e)
                {
                    throw new RepositoryException(e);
                }
                return null;
            }
        });

    }

    public ContentService getContentService()
    {
        return contentService;
    }

    public void setContentService(final ContentService contentService)
    {
        this.contentService = contentService;
    }

    public JcrTemplate getJcrTemplate()
    {
        return jcrTemplate;
    }

    public void setJcrTemplate(final JcrTemplate jcrTemplate)
    {
        this.jcrTemplate = jcrTemplate;
    }
}