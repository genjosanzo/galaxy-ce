package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.util.QNameUtil;
import org.springmodules.jcr.JcrCallback;

public class JcrArtifact extends JcrEntry implements Artifact {
    public static final String CONTENT_TYPE = "contentType";
    public static final String CREATED = "created";
    public static final String DESCRIPTION = "description";
    public static final String NAME = "name";
    public static final String DOCUMENT_TYPE = "documentType";
    
    private JcrWorkspaceManager manager;
    private ContentHandler contentHandler;
    
    public JcrArtifact(Workspace w, Node node, JcrWorkspaceManager manager) 
        throws RepositoryException {
        super(w, node, manager);
        this.manager = manager;
    }

    @Override
    protected EntryVersion createVersion(Node node) throws RepositoryException {
        return new JcrVersion(this, node);
    }

    @Override
    protected String getNodeType() {
        return JcrWorkspaceManager.ARTIFACT_VERSION_NODE_TYPE;
    }
    
    public MimeType getContentType() {
        String ct = getStringOrNull(CONTENT_TYPE);
        
        if (ct == null) {
            return null;
        }
        
        try {
            return new MimeType(ct);
        } catch (MimeTypeParseException e) {
            // we've already previously validated this, so this can't happen
            throw new RuntimeException(e);
        }
    }
    
    public QName getDocumentType() {
        return QNameUtil.fromString(getStringOrNull(DOCUMENT_TYPE));
    }

    public void setContentType(MimeType contentType) {
        try {
            node.setProperty(CONTENT_TYPE, contentType.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setDocumentType(QName documentType) {
        try {
            node.setProperty(DOCUMENT_TYPE, documentType.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public EntryResult newVersion(Object data, String versionLabel, User user)
            throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
        return manager.newVersion(this, data, versionLabel, user);
    }

    public EntryResult newVersion(InputStream inputStream, String versionLabel, User user)
            throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
        return manager.newVersion(this, inputStream, versionLabel, user);
    }

    public void delete() throws RegistryException, AccessException {
        manager.delete(this);
    }

    public JcrWorkspaceManager getManager() {
        return manager;
    }

    public ContentHandler getContentHandler() {
        if (contentHandler == null) {
            if (getDocumentType() != null) {
                contentHandler = manager.getContentService().getContentHandler(getDocumentType());
            } else {
                contentHandler = manager.getContentService().getContentHandler(getContentType());
            }
        }
        return contentHandler;
    }


}
