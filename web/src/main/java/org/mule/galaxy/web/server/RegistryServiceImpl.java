package org.mule.galaxy.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.ArtifactTypeDao;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.view.ArtifactTypeView;
import org.mule.galaxy.view.ViewManager;
import org.mule.galaxy.web.client.ArtifactGroup;
import org.mule.galaxy.web.client.BasicArtifactInfo;
import org.mule.galaxy.web.client.RegistryService;
import org.mule.galaxy.web.client.WArtifactType;
import org.mule.galaxy.web.client.WWorkspace;

public class RegistryServiceImpl implements RegistryService {
    private Registry registry;
    private ArtifactTypeDao artifactTypeDao;
    private ViewManager viewManager;
    
    public Collection getWorkspaces() {
        try {
             Collection<Workspace> workspaces = registry.getWorkspaces();
             List wis = new ArrayList();
             
             for (Workspace w : workspaces) {
                 wis.add(new WWorkspace(w.getId(), w.getName()));
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
            atis.add(new WArtifactType(a.getId(), a.getDescription()));
        }
        return atis;
    }
    

    @SuppressWarnings("unchecked")
    public Collection getArtifacts(String workspace) {
        Query q = new Query(Artifact.class)
            .workspace(workspace)
            .orderBy("artifactType");
        
        try {
            Set results = registry.search(q);
            System.out.println("Found " + results.size());
            Map<String, ArtifactGroup> name2group = new HashMap<String, ArtifactGroup>();
            Map<String, ArtifactTypeView> name2view = new HashMap<String, ArtifactTypeView>();
            
            for (Object o : results) {
                Artifact a = (Artifact) o;
                ArtifactType type = artifactTypeDao.getArtifactType(a.getContentType().toString(), 
                                                                    a.getDocumentType());
                
                ArtifactGroup g = name2group.get(type.getDescription());
                ArtifactTypeView view = name2view.get(type.getDescription());
                
                if (g == null) {
                    g = new ArtifactGroup();
                    g.setName(type.getDescription());
                    name2group.put(type.getDescription(), g);
                    
                    view = viewManager.getArtifactTypeView(a.getDocumentType());
                    if (view == null) {
                        view = viewManager.getArtifactTypeView(a.getContentType().toString());
                    }
                    name2view.put(type.getDescription(), view);
                    
                    for (String col : view.getColumnNames()) {
                        g.getColumns().add(col);
                    }
                }
                
                BasicArtifactInfo info = new BasicArtifactInfo();
                for (int i = 0; i < view.getColumnNames().length; i++) {
                    info.setColumn(i, view.getColumnValue(a, i));
                }
                
                g.getRows().add(info);
            }
            
            ArrayList values = new ArrayList();
            values.addAll(name2group.values());
            return values;
        } catch (QueryException e) {
            throw new RuntimeException(e);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setArtifactTypeDao(ArtifactTypeDao artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }
    
}
