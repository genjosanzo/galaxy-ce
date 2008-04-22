package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.GalaxyException;
import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.impl.content.JarContentHandler;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.security.AccessException;

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
public class JarArtifactPlugin extends AbstractArtifactPlugin
{

    private final Log log = LogFactory.getLog(getClass());

    private ContentService contentService;

    private JcrTemplate jcrTemplate;

    @Override
    public void doInstall() throws Exception
    {
        JcrUtil.doInTransaction(jcrTemplate.getSessionFactory(), new JcrCallback()
        {
            public Object doInJcr(final Session session) throws IOException, RepositoryException
            {

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

                // TODO this should probably go, not really used now
                // dynamically register jar content handler (instead of putting it in core's spring config
                contentService.registerContentHandler(new JarContentHandler());

                // Configure and register a JarIndex
                Map<String, String> manifestIndexConfig = new HashMap<String, String>();
                manifestIndexConfig.put("scriptSource", "JarIndex.groovy");

                // TODO Index revolves too much around XML, needs a serious refactoring
                Index jarIndex = new Index("JAR Indexes",
                                           "application/java-archive",
                                           String.class,
                                           "org.mule.galaxy.impl.index.GroovyIndexer",
                                           manifestIndexConfig);

                // Configure and register Java Annotations indexer
                Map<String, String> annIndexConfig = new HashMap<String, String>();
                annIndexConfig.put("scriptSource", "JavaAnnotationsIndex.groovy");

                Index annotationsIndex = new Index("Java Annotation Indexes",
                                                   "application/java-archive",
                                                   String.class,
                                                   "org.mule.galaxy.impl.index.GroovyIndexer",
                                                   annIndexConfig);

                try
                {
                    indexManager.save(jarIndex, true);
                    indexManager.save(annotationsIndex, true);
                    registerJarPropertyDescriptors();
                    registerJavaAnnotationsPropertyDescriptors();
                }
                catch (GalaxyException e)
                {
                    throw new RepositoryException(e);
                }
                return null;
            }
        });

    }

    public int getVersion()
    {
        return 1;
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

    protected void registerJarPropertyDescriptors()
            throws RegistryException, AccessException
    {
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.entries", "JAR Contents List", true));
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.manifest.foo",
                                                               "A placeholder property. Actual MANIFEST.MF " +
                                                               "keys are prefixed with 'jar.manifest.'",
                                                               false));
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.osgi.Export-Package", "OSGi Package Exports", true));
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.osgi.Import-Package", "OSGi Package Imports", true));
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.osgi.Ignore-Package", "OSGi Ignore Packages", true));
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.osgi.Private-Package", "OSGi Private Packages", true));
    }

    protected void registerJavaAnnotationsPropertyDescriptors()
            throws RegistryException, AccessException
    {
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.annotations.level.class", "Java Class-Level Annotations", true));
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.annotations.level.field", "Java Field-Level Annotations", true));
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.annotations.level.method", "Java Method-Level Annotations", true));
        registry.savePropertyDescriptor(new PropertyDescriptor("jar.annotations.level.param", "Java Param-Level Annotations", true));
    }
}