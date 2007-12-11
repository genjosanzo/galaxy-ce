package org.mule.galaxy;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.mule.galaxy.Index.Language;
import org.mule.galaxy.policy.Approval;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.security.User;

public interface Registry {
    Workspace createWorkspace(String name) throws RegistryException;

    Workspace createWorkspace(Workspace parent, String name) throws RegistryException;
    
    Workspace getWorkspace(String id) throws RegistryException, NotFoundException;
    
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

    List<Comment> getComments(Artifact a);

    void addComment(Comment c);
}
