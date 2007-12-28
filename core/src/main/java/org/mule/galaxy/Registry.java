package org.mule.galaxy;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.mule.galaxy.Index.Language;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.security.User;

public interface Registry {
    Workspace createWorkspace(String name) throws RegistryException;

    Workspace createWorkspace(Workspace parent, String name) throws RegistryException;
    
    void deleteWorkspace(String id) throws RegistryException, NotFoundException;
    
    Workspace getWorkspace(String id) throws RegistryException, NotFoundException;
    
    Workspace getWorkspaceByPath(String path) throws RegistryException, NotFoundException;
    
    void updateWorkspace(Workspace w, String name, String parentId)
        throws RegistryException, NotFoundException;
    
    Collection<Workspace> getWorkspaces() throws RegistryException;
    
    /**
     * Creates an artifact from a Java representation of it (as opposed
     * to a byte[] level representation). The artifact must be apporved
     * by the appropriate policies, or an ArtifactPolicyException will be
     * throw.
     * 
     * @param workspace
     * @param data
     * @param versionLabel
     * @param user
     * @return
     * @throws RegistryException
     * @throws ArtifactPolicyException
     * @throws MimeTypeParseException
     */
    ArtifactResult createArtifact(Workspace workspace, 
                                  Object data, 
                                  String versionLabel, 
                                  User user) 
        throws RegistryException, ArtifactPolicyException, MimeTypeParseException;
    
    ArtifactResult createArtifact(Workspace workspace, 
                                  String contentType, 
                                  String name,
                                  String versionLabel, 
                                  InputStream inputStream, 
                                  User user) 
        throws RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException;
    
    /**
     * Create a new ArtifactVersion from a POJOish object.
     * @param artifact
     * @param data
     * @param versionLabel
     * @param user TODO
     * @return
     * @throws RegistryException
     * @throws IOException
     */
    ArtifactResult newVersion(Artifact artifact, 
                              Object data, 
                              String versionLabel, 
                              User user) 
        throws RegistryException, ArtifactPolicyException, IOException;
    
    /**
     * Create a new ArtifactVersion its byte form. 
     * @param artifact
     * @param versionLabel
     * @param user TODO
     * @param data
     * @return
     * @throws RegistryException
     * @throws IOException
     */
    ArtifactResult newVersion(Artifact artifact, 
                               InputStream inputStream, 
                               String versionLabel, 
                               User user) 
        throws RegistryException, ArtifactPolicyException, IOException;

    Collection<Artifact> getArtifacts(Workspace workspace) throws RegistryException;
    
    Artifact getArtifact(String id) throws NotFoundException;
    
    void delete(Artifact artifact) throws RegistryException;

    void save(Artifact artifact) throws RegistryException;

    Set search(String queryString) throws RegistryException, QueryException;

    Set search(Query query) throws RegistryException, QueryException;

    Artifact getArtifact(Workspace w, String name) throws NotFoundException;

    Artifact resolve(Workspace w, String location);
    
    /* Index related methods */
    
    Index registerIndex(String indexId, 
                        String displayName, 
                        Language language,
                        Class<?> resultType, 
                        String indexExpression, 
                        QName... documentTypes) throws RegistryException;

    Index getIndex(String idxName);

    Set<Index> getIndexes();
    
    Set<Index> getIndices(QName documentType) throws RegistryException;

    /* Property related methods */
     
    Collection<PropertyDescriptor> getPropertyDescriptors() throws RegistryException;

    PropertyDescriptor getPropertyDescriptor(String propertyName) throws RegistryException;

    void savePropertyDescriptor(PropertyDescriptor pd) throws RegistryException;
    
    void deletePropertyDescriptor(String id) throws RegistryException;
    
    Object getPropertyDescriptorOrIndex(final String propertyName);
    

    /* Dependency related operations */
    
    void addDependencies(ArtifactVersion artifactVersion, Artifact... artifacts) throws RegistryException;

    void removeDependencies(ArtifactVersion artifactVersion, Artifact... artifact) throws RegistryException;
    
    Set<Dependency> getDependedOnBy(Artifact a) throws RegistryException;

    /* Comment related operations */
    
    Comment getComment(String commentId) throws NotFoundException;

    List<Comment> getComments(Artifact a);

    void addComment(Comment c);
}
