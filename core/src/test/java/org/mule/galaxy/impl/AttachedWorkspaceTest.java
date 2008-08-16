package org.mule.galaxy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.workspace.AbstractWorkspaceManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.workspace.WorkspaceManager;
import org.mule.galaxy.workspace.WorkspaceManagerFactory;

public class AttachedWorkspaceTest extends AbstractGalaxyTest {
    private List<Item> items;
    
    public void testAttached() throws Exception {
        Workspace parent = registry.getWorkspaces().iterator().next();
        
        items = new ArrayList<Item>();
        Workspace attached = registry.attachWorkspace(parent, 
                                                      "attached", 
                                                      new DummyWorkspaceManagerFactory(),
                                                      new HashMap<String, String>());
        
        // is the attached workspace there?
        Collection<Workspace> workspaces = parent.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        assertEquals(attached, workspaces.iterator().next());
        
        Collection<WorkspaceManager> wms = registry.getWorkspaceManagers();
        assertEquals(2, wms.size());
        
        DummyWorkspaceManager wm = null;
        for (WorkspaceManager e : wms) {
            if (e instanceof DummyWorkspaceManager) { 
                wm = (DummyWorkspaceManager) e;
            }
        }
        
        assertEquals(attached, wm.getAttachedToWorkspace());
        assertEquals(parent, attached.getParent());
        
        assertNotNull(attached.getId());
        assertNotNull(attached.getCreated());
        assertNotNull(attached.getUpdated());
        assertEquals("attached", attached.getName());
        assertEquals(parent.getPath() + "attached/", attached.getPath());
        
    }
    
    public final class DummyWorkspaceManager extends AbstractWorkspaceManager implements WorkspaceManager {
        private String id = "test";
        private Workspace attachedWorkspace;
        
        public DummyWorkspaceManager() {
            super();
        }

        public void attachTo(Workspace workspace) {
            this.attachedWorkspace = workspace;
        }

        public Workspace getAttachedToWorkspace() {
            return attachedWorkspace;
        }

        public EntryResult createArtifact(Workspace workspace, Object data, String versionLabel, User user)
            throws RegistryException, PolicyException, MimeTypeParseException, DuplicateItemException,
            AccessException {
            return null;
        }

        public EntryResult createArtifact(Workspace workspace, String contentType, String name,
                                          String versionLabel, InputStream inputStream, User user)
            throws RegistryException, PolicyException, IOException, MimeTypeParseException,
            DuplicateItemException, AccessException {
            return null;
        }

        public void delete(Item item) throws RegistryException, AccessException {
        }

        public String getId() {
            return id;
        }

        public Item getItemById(String id) throws NotFoundException, RegistryException, AccessException {
            return null;
        }

        public Item getItemByPath(String id) throws NotFoundException, RegistryException, AccessException {
            return null;
        }

        public List<Item> getItems(Workspace w) {
            return items;
        }

        public Workspace getWorkspace(String id) throws RegistryException, NotFoundException, AccessException {
            // TODO Auto-generated method stub
            return null;
        }

        public Collection<Workspace> getWorkspaces() throws AccessException {
            // TODO Auto-generated method stub
            return null;
        }

        public Collection<Workspace> getWorkspaces(Workspace workspace) {
            // TODO Auto-generated method stub
            return null;
        }

        public EntryResult newEntry(Workspace workspace, String name, String versionLabel)
            throws DuplicateItemException, RegistryException, PolicyException, AccessException {
            // TODO Auto-generated method stub
            return null;
        }

        public EntryResult newVersion(Artifact artifact, InputStream inputStream, String versionLabel,
                                      User user) throws RegistryException, PolicyException, IOException,
            DuplicateItemException, AccessException {
            // TODO Auto-generated method stub
            return null;
        }

        public EntryResult newVersion(Artifact artifact, Object data, String versionLabel, User user)
            throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
            // TODO Auto-generated method stub
            return null;
        }

        public EntryResult newVersion(Entry jcrEntry, String versionLabel) throws DuplicateItemException,
            RegistryException, PolicyException, AccessException {
            // TODO Auto-generated method stub
            return null;
        }

        public void setEnabled(EntryVersion version, boolean enabled) throws RegistryException,
            PolicyException {    
        }
        
    }
    
    public final class DummyWorkspaceManagerFactory extends WorkspaceManagerFactory {

        @Override
        public WorkspaceManager createWorkspaceManager(Map<String, String> configuration) {
            return new DummyWorkspaceManager();
        }

        @Override
        public String getName() {
            return "Dummy";
        }
        
    }
}
