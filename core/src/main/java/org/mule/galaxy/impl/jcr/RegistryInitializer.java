package org.mule.galaxy.impl.jcr;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Settings;
import org.mule.galaxy.impl.lifecycle.LifecycleExtension;
import org.mule.galaxy.impl.upgrade.Upgrader;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.policy.PolicyManager;
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
    private PolicyManager policyManager;
    private Settings settings;
    private LifecycleExtension lifecycleExtension;
    private JcrWorkspaceManager localWorkspaceManager;
    
    public void intialize() throws Exception {

        final Session session = sessionFactory.getSession();
        Node root = session.getRootNode();
        
        Node workspaces = JcrUtil.getOrCreate(root, "workspaces", "galaxy:noSiblings");
        
        NodeIterator nodes = workspaces.getNodes();
        // ignore the system node
        if (nodes.getSize() == 0) {
            Node node = workspaces.addNode(settings.getDefaultWorkspaceName(),
                                           "galaxy:workspace");
            node.addMixin("mix:referenceable");
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            node.setProperty(AbstractJcrItem.CREATED, now);
            
            JcrWorkspace w = new JcrWorkspace(localWorkspaceManager, node);
            w.setName(settings.getDefaultWorkspaceName());

            workspaces.setProperty(REPOSITORY_LAYOUT_VERSION, "4");

            final PropertyDescriptor lifecyclePD = new PropertyDescriptor();
            lifecyclePD.setProperty(Registry.PRIMARY_LIFECYCLE);
            lifecyclePD.setDescription("Primary lifecycle");
            lifecyclePD.setExtension(lifecycleExtension);
            
            final Type defaultType = new Type();
            defaultType.setName("Base Type");
            defaultType.setProperties(Arrays.asList(lifecyclePD));
            
            SecurityUtils.doPriveleged(new Runnable() {

                public void run() {
                    try {
                        TypeManager tm = localWorkspaceManager.getTypeManager();
                        tm.savePropertyDescriptor(lifecyclePD);
                        tm.saveType(defaultType);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                
            });
        } else {
            String versionStr = JcrUtil.getStringOrNull(workspaces, REPOSITORY_LAYOUT_VERSION);
            final int version = Integer.parseInt(versionStr);
            if (version < 5) {
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
            workspaces.setProperty(REPOSITORY_LAYOUT_VERSION, "5");
        }
        
        session.save();
        
        for (ContentHandler ch : contentService.getContentHandlers()) {
            ch.setRegistry(registry);
        }
        
        for (Policy a : policyManager.getPolicies()) {
            a.setRegistry(registry);
        }
        
        session.logout();
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

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setLifecycleExtension(LifecycleExtension lifecycleExtension) {
        this.lifecycleExtension = lifecycleExtension;
    }

    public void setLocalWorkspaceManager(JcrWorkspaceManager localWorkspaceManager) {
        this.localWorkspaceManager = localWorkspaceManager;
    }

}
