package org.mule.galaxy.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Dao;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.web.client.ArtifactTypeInfo;
import org.mule.galaxy.web.client.RegistryService;
import org.mule.galaxy.web.client.WorkspaceInfo;

public class RegistryServiceImpl implements RegistryService {
    private Registry registry;
    private Dao<ArtifactType> artifactTypeDao;
    
    public Collection getWorkspaces() {
        try {
             Collection<Workspace> workspaces = registry.getWorkspaces();
             List wis = new ArrayList();
             
             for (Workspace w : workspaces) {
                 wis.add(new WorkspaceInfo(w.getId(), w.getName()));
             }
             return wis;
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection getArtifactTypes() {
        Collection<ArtifactType> artifactTypes = artifactTypeDao.listAll();
        List atis = new ArrayList();
        
        for (ArtifactType a : artifactTypes) {
            atis.add(new ArtifactTypeInfo(a.getId(), a.getDescription()));
        }
        return atis;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setArtifactTypeDao(Dao<ArtifactType> artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }
}
