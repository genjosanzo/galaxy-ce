package org.mule.galaxy;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.mule.galaxy.Index.Language;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;

public interface Registry {
    Workspace createWorkspace(String name) throws RegistryException;

    Workspace createWorkspace(Workspace parent, String name) throws RegistryException;
    
    Workspace getWorkspace(String id) throws RegistryException, NotFoundException;
    
    Collection<Workspace> getWorkspaces() throws RegistryException;
    
    Artifact createArtifact(Workspace workspace, Object data, String versionLabel) throws RegistryException, MimeTypeParseException;
    
    Artifact createArtifact(Workspace workspace, 
                            String contentType, 
                            String name, 
                            String versionLabel, 
                            InputStream inputStream) 
        throws RegistryException, IOException, MimeTypeParseException;
    
    /**
     * Create a new ArtifactVersion from a POJOish object.
     * @param artifact
     * @param data
     * @param versionLabel
     * @return
     * @throws RegistryException
     * @throws IOException
     */
    ArtifactVersion newVersion(Artifact artifact, 
                               Object data, 
                               String versionLabel) throws RegistryException, IOException;
    
    /**
     * Create a new ArtifactVersion its byte form. 
     * @param artifact
     * @param data
     * @param versionLabel
     * @return
     * @throws RegistryException
     * @throws IOException
     */
    ArtifactVersion newVersion(Artifact artifact, 
                               InputStream inputStream, 
                               String versionLabel) throws RegistryException, IOException;

    /**
     * Assess whether or not the new version is approved based on the policies which
     * apply to this artifact & workspace. 
     * 
     * @param newVersion
     * @return
     * @throws RegistryException
     */
    Collection<VersionApproval> approve(ArtifactVersion newVersion) throws RegistryException;
    
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

    void removeWorkspace(Workspace newWork) throws RegistryException;


    
}
