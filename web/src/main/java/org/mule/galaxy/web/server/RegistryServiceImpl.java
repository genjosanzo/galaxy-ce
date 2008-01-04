package org.mule.galaxy.web.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.acegisecurity.context.SecurityContextHolder;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.ArtifactTypeDao;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Comment;
import org.mule.galaxy.Dependency;
import org.mule.galaxy.Index;
import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.lifecycle.TransitionException;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.PolicyInfo;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.view.ArtifactTypeView;
import org.mule.galaxy.view.ViewManager;
import org.mule.galaxy.web.client.RPCException;
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.ArtifactVersionInfo;
import org.mule.galaxy.web.rpc.BasicArtifactInfo;
import org.mule.galaxy.web.rpc.DependencyInfo;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.RegistryService;
import org.mule.galaxy.web.rpc.TransitionResponse;
import org.mule.galaxy.web.rpc.WArtifactType;
import org.mule.galaxy.web.rpc.WComment;
import org.mule.galaxy.web.rpc.WGovernanceInfo;
import org.mule.galaxy.web.rpc.WProperty;
import org.mule.galaxy.web.rpc.WWorkspace;

public class RegistryServiceImpl implements RegistryService {
    private Logger LOGGER = LogUtils.getL7dLogger(RegistryServiceImpl.class);

    private Registry registry;
    private ArtifactTypeDao artifactTypeDao;
    private ViewManager viewManager;
    private PolicyManager policyManager;
    private LifecycleManager lifecycleManager;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a, MMMM d, yyyy");
    
    @SuppressWarnings("unchecked")
    public Collection getWorkspaces() throws RPCException {
        try {
             Collection<Workspace> workspaces = registry.getWorkspaces();
             List wis = new ArrayList();
             
             for (Workspace w : workspaces) {
                 WWorkspace ww = new WWorkspace(w.getId(), w.getName(), w.getPath());
                 wis.add(ww);
                 
                 Collection<Workspace> children = w.getWorkspaces();
                 if (children != null && children.size() > 0) {
                     ww.setWorkspaces(new ArrayList());
                     addWorkspaces(ww, children);
                 }
             }
             return wis;
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }
    }
    

    @SuppressWarnings("unchecked")
    private void addWorkspaces(WWorkspace parent, Collection<Workspace> workspaces) {
        for (Workspace w : workspaces) {
            WWorkspace ww = new WWorkspace(w.getId(), w.getName(), w.getPath());
            parent.getWorkspaces().add(ww);
            
            Collection<Workspace> children = w.getWorkspaces();
            if (children != null && children.size() > 0) {
                ww.setWorkspaces(new ArrayList());
                addWorkspaces(ww, children);
            }
        }
    }


    public void addWorkspace(String parentWorkspaceId, String workspaceName) throws RPCException {
        try {
            if (parentWorkspaceId == null || "[No parent]".equals(parentWorkspaceId)) {
                registry.createWorkspace(workspaceName);
            } else {
                Workspace parent = registry.getWorkspace(parentWorkspaceId);
                registry.createWorkspace(parent, workspaceName);
            }
       } catch (RegistryException e) {
           LOGGER.log(Level.WARNING, e.getMessage(), e);
           throw new RPCException(e.getMessage());
       }
    }

    

    public void updateWorkspace(String workspaceId, String parentWorkspaceId, String workspaceName)
        throws RPCException {
        try {
            if (parentWorkspaceId == null || "[No parent]".equals(parentWorkspaceId)) {
                parentWorkspaceId = null;
            } 

            registry.updateWorkspace(registry.getWorkspace(workspaceId), workspaceName, parentWorkspaceId);
       } catch (RegistryException e) {
           LOGGER.log(Level.WARNING, e.getMessage(), e);
           throw new RPCException(e.getMessage());
       }
    }


    public void deleteWorkspace(String workspaceId) throws RPCException {
        try {
            registry.deleteWorkspace(workspaceId);
       } catch (RegistryException e) {
           LOGGER.log(Level.WARNING, e.getMessage(), e);
           throw new RPCException(e.getMessage());
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
        return createBasicArtifactInfo(a, view, info);
    }

    private BasicArtifactInfo createBasicArtifactInfo(Artifact a, ArtifactTypeView view,
                                                      BasicArtifactInfo info) {
        info.setId(a.getId());
        for (int i = 0; i < view.getColumnNames().length; i++) {
            info.setColumn(i, view.getColumnValue(a, i));
        }
        return info;
    }

    @SuppressWarnings("unchecked")
    public Map getIndexes() {
        Map map = new HashMap();
        
        Set<Index> indices = registry.getIndexes();
        for (Index idx : indices) {
            map.put(idx.getName(), idx.getId());
        }
        
        return map;
    }

    @SuppressWarnings("unchecked")
    public Collection getDependencyInfo(String artifactId) throws RPCException {
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
            e.printStackTrace();
            throw new RPCException("Could not find artifact " + artifactId);
        }
        
        
    }
    

    @SuppressWarnings("unchecked")
    public ArtifactGroup getArtifact(String artifactId) throws RPCException {
        try {
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
            
            
            ExtendedArtifactInfo info = new ExtendedArtifactInfo();
            createBasicArtifactInfo(a, view, info);
            
            info.setDescription(a.getDescription());
            
            for (Iterator<PropertyInfo> props = a.getProperties(); props.hasNext();) {
                PropertyInfo p = props.next();
                
                Object val = p.getValue();
                if (val instanceof Collection) {
                    String s = val.toString();
                    val = s.substring(1, s.length()-1);
                } else if (val != null) {
                    val = val.toString();
                } else {
                    val = "";
                }
                
                String desc = p.getDescription();
                if (desc == null) {
                    desc = p.getName();
                }
                info.getProperties().add(new WProperty(p.getName(),
                                                       desc,
                                                       val.toString(),
                                                       p.isLocked()));
            }
            
            Collections.sort(info.getProperties(), new Comparator() {

                public int compare(Object o1, Object o2) {
                    return ((WProperty) o1).getDescription().compareTo(
                               ((WProperty) o2).getDescription());
                }
                
            });
            
            List wcs = info.getComments();
            
            List<Comment> comments = registry.getComments(a);
            for (Comment c : comments) {
                WComment wc = new WComment(c.getId(),
                                           c.getUser().getUsername(),
                                           dateFormat.format(c.getDate().getTime()),
                                           c.getText());
                wcs.add(wc);
                
                Set<Comment> children = c.getComments();
                if (children != null && children.size() > 0) {
                    addComments(wc, children);
                }
            }
            
            g.getRows().add(info);
            
            info.setArtifactLink(getLink("/api/repository", a));
            info.setCommentsFeedLink(getLink("/api/comments", a));
            
            return g;
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }
    }

    
    private String getLink(String base, Artifact a) {
        StringBuilder sb = new StringBuilder();
        Workspace w = a.getWorkspace();
        
        sb.append(base)
          .append(w.getPath())
          .append(a.getName());
        return sb.toString();
    }

    public WComment addComment(String artifactId, String parentComment, String text) throws RPCException {
        try {
            Artifact artifact = registry.getArtifact(artifactId);
          
            Comment comment = new Comment();
            comment.setText(text);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            comment.setDate(cal);
            
            comment.setUser(getCurrentUser());
            
            if (parentComment != null) {
                Comment c = registry.getComment(parentComment);
                if (c == null) {
                    throw new RPCException("Invalid parent comment");
                }
                comment.setParent(c);
            } else {
                comment.setArtifact(artifact);
            }
            registry.addComment(comment);
            
            return new WComment(comment.getId(),
                                comment.getUser().getUsername(),
                                dateFormat.format(comment.getDate().getTime()),
                                comment.getText());
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }
    }


    private User getCurrentUser() throws RPCException {
        UserDetailsWrapper wrapper = 
            (UserDetailsWrapper) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (wrapper == null) {
            throw new RPCException("No user is logged in!");
        }
        User user = wrapper.getUser();
        return user;
    }

    @SuppressWarnings("unchecked")
    private void addComments(WComment parent, Set<Comment> comments) {
        for (Comment c : comments) {
            WComment child = new WComment(c.getId(),
                                          c.getUser().getUsername(),
                                          dateFormat.format(c.getDate().getTime()),
                                          c.getText());
            parent.getComments().add(child);
            
            Set<Comment> children = c.getComments();
            if (children != null && children.size() > 0) {
                addComments(child, children);
            }
        }
    }

    public void newPropertyDescriptor(String name, String description, boolean multivalued)
        throws RPCException {
        if (name.contains(" ")) {
            throw new RPCException("The property name cannot contain a space.");
        }
        
        PropertyDescriptor pd = new PropertyDescriptor(name, description, multivalued);
        
        try {
            registry.savePropertyDescriptor(pd);
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }
    }

    public void setProperty(String artifactId, String propertyName, String propertyValue) throws RPCException {
        try {
            Artifact artifact = registry.getArtifact(artifactId);
          
            
            artifact.setProperty(propertyName, propertyValue);
            registry.save(artifact);
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (PropertyException e) {
            // occurs if property name is formatted wrong
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }
    }
    

    public void deleteProperty(String artifactId, String propertyName) throws RPCException {
        try {
            Artifact artifact = registry.getArtifact(artifactId);
            artifact.setProperty(propertyName, null);
            registry.save(artifact);
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (PropertyException e) {
            // occurs if property name is formatted wrong
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }
    }

    public Map getProperties() throws RPCException {
        try {
            Collection<PropertyDescriptor> pds = registry.getPropertyDescriptors();
            
            HashMap<Object, Object> props = new HashMap<Object, Object>();
            for (PropertyDescriptor pd : pds) {
                props.put(pd.getProperty(), pd.getDescription());
            }
            
            return props;
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } 
    }

    public void setDescription(String artifactId, String description) throws RPCException {
        try {
            Artifact artifact = registry.getArtifact(artifactId);
          
            artifact.setDescription(description);
            
            registry.save(artifact);
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } 
    }

    
    public WGovernanceInfo getGovernanceInfo(String artifactId) throws RPCException {
        try {
            Artifact artifact = registry.getArtifact(artifactId);
          
            WGovernanceInfo gov = new WGovernanceInfo();
            Phase phase = artifact.getPhase();
            gov.setCurrentPhase(phase.getName());
            gov.setLifecycle(phase.getLifecycle().getName());
            
            Set<Phase> nextPhases = phase.getNextPhases();
            ArrayList<String> nextPhaseNames = new ArrayList<String>();
            for (Phase p : nextPhases) {
                nextPhaseNames.add(p.getName());
            }
            gov.setNextPhases(nextPhaseNames);
            
            Collection<PolicyInfo> policies = policyManager.getActivePolicies(artifact, false);
            
            
            return gov;
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } 
    }


    public TransitionResponse transition(String artifactId, String nextPhaseName) throws RPCException {
        try {
            Artifact artifact = registry.getArtifact(artifactId);
          
            Lifecycle lifecycle = artifact.getPhase().getLifecycle();
            Phase nextPhase = lifecycle.getPhase(nextPhaseName);
            
            TransitionResponse tr = new TransitionResponse();
            
            try {
                lifecycleManager.transition(artifact, nextPhase, getCurrentUser());
                
                tr.setSuccess(true);
            } catch (TransitionException e) {
                tr.setSuccess(false);
                tr.addMessage("Phase " + nextPhaseName + " isn't a valid next phase!", false);
            } catch (ArtifactPolicyException e) {
                tr.setSuccess(false);
                for (ApprovalMessage app : e.getApprovals()) {
                    tr.addMessage(app.getMessage(), app.isWarning());
                }
            }
            
            return tr;
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } 
    }


    @SuppressWarnings("unchecked")
    public Collection getArtifactVersions(String artifactId) throws RPCException {
        try {
            Artifact artifact = registry.getArtifact(artifactId);
          
            List versions = new ArrayList();
            for (ArtifactVersion av : artifact.getVersions()) {
                versions.add(new ArtifactVersionInfo(av.getVersionLabel(),
                                                     getVersionLink(av),
                                                     av.getCreated().getTime(),
                                                     av.getAuthor().getName(),
                                                     av.getAuthor().getUsername()));
            }
            return versions;
        } catch (RegistryException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } 
    }


    private String getVersionLink(ArtifactVersion av) {
        Artifact a = av.getParent();
        StringBuilder sb = new StringBuilder();
        Workspace w = a.getWorkspace();

        sb.append("/api/repository")
          .append(w.getPath())
          .append(a.getName())
          .append("?version=")
          .append(av.getVersionLabel());
        return sb.toString();
    }


    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }


    public void setArtifactTypeDao(ArtifactTypeDao artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }


    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }
    
}
