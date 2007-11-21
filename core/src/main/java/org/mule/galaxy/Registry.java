package org.mule.galaxy;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.mule.galaxy.Index.Language;
import org.mule.galaxy.query.Query;

public interface Registry {
    public static final String PRODUCTION_TAG = "production";
    public static final String DEVELOPMENT_TAG = "development";

    Workspace getWorkspace(String id) throws RegistryException, NotFoundException;
    
    Collection<Workspace> getWorkspaces() throws RegistryException;
    
    Artifact createArtifact(Workspace workspace, Object data, String versionLabel) throws RegistryException, MimeTypeParseException;
    
    Artifact createArtifact(Workspace workspace, String contentType, String name, String versionLabel, InputStream inputStream) 
        throws RegistryException, IOException, MimeTypeParseException;
    
    ArtifactVersion newVersion(Artifact artifact, Object data, String versionLabel) throws RegistryException, IOException;
    
    ArtifactVersion newVersion(Artifact artifact, InputStream inputStream, String versionLabel) throws RegistryException, IOException;

    Collection<Artifact> getArtifacts(Workspace workspace) throws RegistryException;
    
    Artifact getArtifact(String id) throws NotFoundException;
    
    void delete(Artifact artifact) throws RegistryException;

    Index registerIndex(String indexId, 
                        String displayName, 
                        Language language,
                        Class<?> resultType, 
                        String indexExpression, 
                        QName... documentTypes) throws RegistryException;

    Set search(String queryString) throws RegistryException, QueryException;

    Set search(Query query) throws RegistryException, QueryException;

    Set<Index> getIndices();
    
    Set<Index> getIndices(QName documentType) throws RegistryException;
    
}
