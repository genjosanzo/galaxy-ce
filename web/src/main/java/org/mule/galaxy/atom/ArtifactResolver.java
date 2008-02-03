package org.mule.galaxy.atom;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.DefaultTarget;
import org.apache.abdera.protocol.server.impl.DefaultWorkspaceManager;
import org.apache.commons.lang.StringUtils;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;

public class ArtifactResolver implements Resolver<Target> {

    private static final String WORKSPACES_CLASSIFIER = "workspaces";
    public static final String WORKSPACE = "workspace";
    public static final String ARTIFACT = "artifact";
    public static final String COLLECTION_HREF = "collectionHref";
    
    private Registry registry;
    private ArtifactHistoryCollection historyCollection;
    private SearchableArtifactCollection searchableCollection;
    private ArtifactWorkspaceCollection artifactWorkspaceCollection;
    private CommentCollectionProvider commentCollection;
    private WorkspaceCollection workspaceCollection;
    
    public Target resolve(Request request) {
        RequestContext context = (RequestContext) request;
        String path = context.getTargetPath();
        if (path.startsWith("/api")) {
            path = path.substring(4);
        }
        
        // trim query part
        int qIdx = path.lastIndexOf('?');
        if (qIdx != -1) {
            path = path.substring(0, qIdx);
        }

        // Find the resource representation type
        String classifier = null;
        int cIdx = path.lastIndexOf(';');
        if (cIdx != -1) {
            classifier = path.substring(cIdx+1);
            path = path.substring(0, cIdx);
        }
        
        // remove front slash
        if (path.startsWith("/")) {
            path =  path.substring(1);
        }
        // remove end slash
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        
        // find where this is going to - comments, registry, etc
        int sIdx = path.indexOf('/');
        if (sIdx == -1 && path.length() == 0) {
            return new DefaultTarget(TargetType.TYPE_SERVICE, context);
        }
        
        String atomWorkspace = null;
        if (sIdx != -1) {
            atomWorkspace = path.substring(0, sIdx);
            path = path.substring(sIdx);
        } else {
            atomWorkspace = path;
            path = "";
        }
        
        if ("comments".equals(atomWorkspace)) {
            context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, commentCollection);
            sIdx = path.indexOf('/');
            if (sIdx != -1 && sIdx != path.length()) {
                return new DefaultTarget(TargetType.TYPE_ENTRY, context);
            } else {
                return new DefaultTarget(TargetType.TYPE_COLLECTION, context);
            }
        }
        
        if (!"registry".equals(atomWorkspace)) {
            return returnUnknownLocation(context);
        }
        
        // Somebody hit /api/registry
        if (path.length() == 0) {
            if (WORKSPACES_CLASSIFIER.equals(classifier)) {
                // the user is listing out the root workspaces
                // the user is going to the searchable, main registry URL
                context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, workspaceCollection);
                return new DefaultTarget(TargetType.TYPE_COLLECTION, context);
            } else if (StringUtils.isEmpty(classifier)) {
                // the user is going to the searchable, main registry URL
                context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, searchableCollection);
                return new DefaultTarget(TargetType.TYPE_COLLECTION, context);
            } else {
                return returnUnknownLocation(context);
            }
           
        }

        // somebody hit /api/registry/foo....
        path = UrlEncoding.decode(path);
        
        try {
            try {
                return resolveWorkspace(path, classifier, context);
            } catch (NotFoundException e) {
                return resolveArtifact(path, classifier, context);
            } 
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
    }

    private Target returnUnknownLocation(RequestContext context) {
        return new DefaultTarget(TargetType.TYPE_UNKNOWN, context);
    }

    private Target resolveArtifact(String path, String classifier, RequestContext context)
        throws RegistryException {
        // The user is looking at an artifact
        int idx = path.lastIndexOf('/');
        
        String artifactName = path.substring(idx+1);
        path = path.substring(0, idx);
        
        try {
            Workspace workspace = registry.getWorkspaceByPath(path);
            
            Artifact artifact = registry.getArtifact(workspace, artifactName);
            
            context.setAttribute(WORKSPACE, workspace);
            context.setAttribute(ARTIFACT, artifact);
            
            context.setAttribute(COLLECTION_HREF, getPathWithoutArtifact(context));
            
            CollectionAdapter collection = (context.getParameter("version") != null) ? historyCollection : searchableCollection;
            if ("atom".equals(classifier)) {
                context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, collection);
                return new DefaultTarget(TargetType.TYPE_ENTRY, context);
            } else if ("categories".equals(classifier)) {
                context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, collection);
                return new DefaultTarget(TargetType.TYPE_CATEGORIES, context);
            }  else if ("history".equals(classifier)) {
                context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, historyCollection);
                return new DefaultTarget(TargetType.TYPE_COLLECTION, context);
            } else if (classifier == null) {
                context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, collection);
                return new DefaultTarget(TargetType.TYPE_MEDIA, context);
            }
        } catch (NotFoundException e1) {
            return returnUnknownLocation(context);
        }
        
        return returnUnknownLocation(context);
    }

    private String getPathWithoutArtifact(RequestContext context) {
        String s = context.getTargetPath();
        
        int idx = s.lastIndexOf('/');
        if (idx != -1) {
            s = s.substring(0, idx);
        }
        return s;
    }

    private Target resolveWorkspace(String path, String classifier, RequestContext context) throws RegistryException,
        NotFoundException {
        // The user is browsing a workspace
        Workspace workspace = registry.getWorkspaceByPath(path);
        
        context.setAttribute(WORKSPACE, workspace);
        
        context.setAttribute(COLLECTION_HREF, context.getTargetPath());
        if (WORKSPACES_CLASSIFIER.equals(classifier) || "atom".equals(classifier)) {
            context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, workspaceCollection);
        } else {
            context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, artifactWorkspaceCollection);
        }
        
         
        if ("GET".equals(context.getMethod()) || "POST".equals(context.getMethod())) {
            return new DefaultTarget(TargetType.TYPE_COLLECTION, context);
        } else {
            // TODO: not sure if this is what we should be doing.
            // maybe we should be deleting to /workspace;atom instead of just /workspace 
            context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, workspaceCollection);
            return new DefaultTarget(TargetType.TYPE_ENTRY, context);
        }
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public ArtifactHistoryCollection getHistoryCollection() {
        return historyCollection;
    }

    public void setHistoryCollection(ArtifactHistoryCollection historyCollection) {
        this.historyCollection = historyCollection;
    }

    public SearchableArtifactCollection getSearchableCollection() {
        return searchableCollection;
    }

    public void setSearchableCollection(SearchableArtifactCollection searchableCollection) {
        this.searchableCollection = searchableCollection;
    }


    public ArtifactWorkspaceCollection getArtifactWorkspaceCollection() {
        return artifactWorkspaceCollection;
    }

    public void setArtifactWorkspaceCollection(ArtifactWorkspaceCollection artifactWorkspaceCollection) {
        this.artifactWorkspaceCollection = artifactWorkspaceCollection;
    }

    public CommentCollectionProvider getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(CommentCollectionProvider commentCollection) {
        this.commentCollection = commentCollection;
    }

    public WorkspaceCollection getWorkspaceCollection() {
        return workspaceCollection;
    }

    public void setWorkspaceCollection(WorkspaceCollection workspaceCollection) {
        this.workspaceCollection = workspaceCollection;
    }

}
