package org.mule.galaxy.impl.jcr;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.jackrabbit.util.Text;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Settings;
import org.mule.galaxy.artifact.ContentHandler;
import org.mule.galaxy.artifact.ContentService;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.impl.artifact.ArtifactExtension;
import org.mule.galaxy.impl.lifecycle.LifecycleExtension;
import org.mule.galaxy.impl.link.LinkExtension;
import org.mule.galaxy.impl.upgrade.Upgrader;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.SecurityUtils;
import org.springmodules.jcr.SessionFactory;

public class RegistryInitializer {
    private static final String REPOSITORY_LAYOUT_VERSION = "version";

    private Registry registry;
    private SessionFactory sessionFactory;
    private ContentService contentService;
    private Collection<Upgrader> upgraders;
    private Settings settings;
    private Extension linkExtension;
    private Extension lifecycleExtension;
    private Extension artifactExtension;
    private JcrWorkspaceManager localWorkspaceManager;

    public void intialize() throws Exception {

        final Session session = sessionFactory.getSession();
        Node root = session.getRootNode();

        final Node workspaces = JcrUtil.getOrCreate(root, "workspaces", "galaxy:noSiblings");

        NodeIterator nodes = workspaces.getNodes();
        // ignore the system node
        if (nodes.getSize() == 0) {
            SecurityUtils.doPriveleged(new Runnable() {

                public void run() {
                    try {
                        createTypes(workspaces);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            });

        } else {
            String versionStr = JcrUtil.getStringOrNull(workspaces, REPOSITORY_LAYOUT_VERSION);
            final int version = Integer.parseInt(versionStr);
            if (version < 6) {
                SecurityUtils.doPriveleged(new Runnable() {
                    public void run() {
                        for (Upgrader u : upgraders) {
                            try {
                                u.doUpgrade(version, session, session.getRootNode());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                });
            }
            workspaces.setProperty(REPOSITORY_LAYOUT_VERSION, "6");
        }

        session.save();

        for (ContentHandler ch : contentService.getContentHandlers()) {
            ch.setRegistry(registry);
        }

        session.logout();
    }

    protected void createTypes(Node workspaces) throws Exception {

        final PropertyDescriptor lifecyclePD = new PropertyDescriptor();
        lifecyclePD.setProperty(Registry.PRIMARY_LIFECYCLE);
        lifecyclePD.setDescription("Primary lifecycle");
        lifecyclePD.setExtension(lifecycleExtension);

        final PropertyDescriptor filePD = new PropertyDescriptor();
        filePD.setProperty("artifact");
        filePD.setDescription("File");
        filePD.setExtension(artifactExtension);

        final PropertyDescriptor defaultPD = new PropertyDescriptor();
        defaultPD.setProperty("default.version");
        defaultPD.setDescription("Default Version");
        defaultPD.setMultivalued(false);
        defaultPD.setExtension(linkExtension);

        Map<String, String> config = new HashMap<String,String>();
        config.put(LinkExtension.RECIPROCAL_CONFIG_KEY, "Default Version For Item");
        defaultPD.setConfiguration(config);
        
        // Create default types

        final Type baseType = new Type();
        baseType.setName("Base Type");
        baseType.setSystemType(true);
        
        final Type version = new Type();
        version.setName(TypeManager.VERSION);
        version.setMixins(Arrays.asList(baseType));
        version.setSystemType(true);
        
        final Type versioned = new Type();
        versioned.setName(TypeManager.VERSIONED);
        versioned.setAllowedChildren(Arrays.asList(version));
        versioned.setSystemType(true);
        
        final Type artifactVersion = new Type();
        artifactVersion.setName(TypeManager.ARTIFACT_VERSION);
        artifactVersion.setProperties(Arrays.asList(filePD, lifecyclePD));
        artifactVersion.setMixins(Arrays.asList(version));
        artifactVersion.setSystemType(true);
        
        final Type artifact = new Type();
        artifact.setName(TypeManager.ARTIFACT);
        artifact.setAllowedChildren(Arrays.asList(artifactVersion));
        artifact.setMixins(Arrays.asList(versioned));
        artifact.setSystemType(true);
        
        final Type workspaceType = new Type();
        workspaceType.setName(TypeManager.WORKSPACE);
        workspaceType.setSystemType(true);
        
        TypeManager tm = localWorkspaceManager.getTypeManager();
        tm.savePropertyDescriptor(lifecyclePD);
        tm.savePropertyDescriptor(filePD);
        tm.savePropertyDescriptor(defaultPD);
        tm.saveType(baseType);
        tm.saveType(version);
        tm.saveType(versioned);
        tm.saveType(artifactVersion);
        tm.saveType(artifact);
        tm.saveType(workspaceType);
        
        Node node = workspaces.addNode(Text.escapeIllegalJcrChars(settings.getDefaultWorkspaceName()), "galaxy:item");
        node.addMixin("mix:referenceable");
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        node.setProperty(JcrItem.CREATED, now);

        JcrItem w = new JcrItem(node, localWorkspaceManager);
        w.setName(settings.getDefaultWorkspaceName());
        w.setType(workspaceType);

        workspaces.setProperty(REPOSITORY_LAYOUT_VERSION, "4");
    }

    public void setUpgraders(Collection<Upgrader> upgraders) {
        this.upgraders = upgraders;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setLifecycleExtension(LifecycleExtension lifecycleExtension) {
        this.lifecycleExtension = lifecycleExtension;
    }

    public void setArtifactExtension(ArtifactExtension artifactExtension) {
        this.artifactExtension = artifactExtension;
    }

    public void setLocalWorkspaceManager(JcrWorkspaceManager localWorkspaceManager) {
        this.localWorkspaceManager = localWorkspaceManager;
    }

    public void setLinkExtension(Extension linkExtension) {
        this.linkExtension = linkExtension;
    }

}
