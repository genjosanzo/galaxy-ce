package org.mule.galaxy.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.ArtifactTypeDao;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Dependency;
import org.mule.galaxy.Index;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.view.ArtifactTypeView;
import org.mule.galaxy.view.ViewManager;
import org.mule.galaxy.web.client.ArtifactGroup;
import org.mule.galaxy.web.client.BasicArtifactInfo;
import org.mule.galaxy.web.client.DependencyInfo;
import org.mule.galaxy.web.client.RegistryService;
import org.mule.galaxy.web.client.WArtifactType;
import org.mule.galaxy.web.client.WWorkspace;

public class RegistryServiceImpl implements RegistryService {
    private Registry registry;
    private ArtifactTypeDao artifactTypeDao;
    private ViewManager viewManager;
    
    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    public Collection getArtifactTypes() {
        Collection<ArtifactType> artifactTypes = artifactTypeDao.listAll();
        List atis = new ArrayList();
        
        for (ArtifactType a : artifactTypes) {
            atis.add(new WArtifactType(a.getId(), a.getDescription()));
        }
        return atis;
    }
    

    @SuppressWarnings("unchecked")
    public Collection getArtifacts(String workspaceId, Set artifactTypes) {
        Query q = new Query(Artifact.class)
            .workspace(workspaceId)
            .orderBy("artifactType");
        
        try {
            Set results = registry.search(q);
            Map<String, ArtifactGroup> name2group = new HashMap<String, ArtifactGroup>();
            Map<String, ArtifactTypeView> name2view = new HashMap<String, ArtifactTypeView>();
            
            for (Object o : results) {
                Artifact a = (Artifact) o;
                ArtifactType type = artifactTypeDao.getArtifactType(a.getContentType().toString(), 
                                                                    a.getDocumentType());
                
                // If we want to filter based on the artifact type, filter!
                if (artifactTypes != null && artifactTypes.size() != 0 && !artifactTypes.contains(type.getId())) {
                    continue;
                }
                
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
                
                BasicArtifactInfo info = createBasicArtifactInfo(a, view);
                
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

    private BasicArtifactInfo createBasicArtifactInfo(Artifact a, ArtifactTypeView view) {
        BasicArtifactInfo info = new BasicArtifactInfo();
        info.setId(a.getId());
        for (int i = 0; i < view.getColumnNames().length; i++) {
            info.setColumn(i, view.getColumnValue(a, i));
        }
        return info;
    }

    @SuppressWarnings("unchecked")
    public Map getArtifactIndices() {
        Map map = new Hashtable();
        
        Set<Index> indices = registry.getIndices();
        for (Index idx : indices) {
            map.put(idx.getName(), idx.getId());
        }
        
        return map;
    }

    @SuppressWarnings("unchecked")
    public Collection getDependencyInfo(String artifactId) throws Exception {
        try {
            Artifact artifact = registry.getArtifact(artifactId);
            List deps = new ArrayList();
            ArtifactVersion latest = artifact.getLatestVersion();
            for (Dependency d : latest.getDependencies()) {
                Artifact depArt = d.getArtifact();
                deps.add(new DependencyInfo(d.isUserSpecified(), 
                                            true,
                                            depArt.getName(),
                                            depArt.getId()));
            }
            
            for (Dependency d : registry.getDependedOnBy(artifact)) {
                Artifact depArt = d.getArtifact();
                deps.add(new DependencyInfo(d.isUserSpecified(), 
                                            false,
                                            depArt.getName(),
                                            depArt.getId()));
            }
            
            return deps;
        } catch (Exception e) {
            throw new Exception("Could not find artifact " + artifactId);
        }
        
        
    }
    

    @SuppressWarnings("unchecked")
    public ArtifactGroup getArtifact(String artifactId) throws Exception {
        Artifact a = registry.getArtifact(artifactId);
        ArtifactType type = artifactTypeDao.getArtifactType(a.getContentType().toString(), 
                                                            a.getDocumentType());
        
        ArtifactGroup g = new ArtifactGroup();
        g.setName(type.getDescription());
        ArtifactTypeView  view = viewManager.getArtifactTypeView(a.getDocumentType());
        if (view == null) {
            view = viewManager.getArtifactTypeView(a.getContentType().toString());
        }
        
        for (String col : view.getColumnNames()) {
            g.getColumns().add(col);
        }
        
        
        BasicArtifactInfo info = createBasicArtifactInfo(a, view);
        
        g.getRows().add(info);
        
        return g;
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
