package org.mule.galaxy.web.rpc;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.web.client.RPCException;

public class ExtendedArtifactInfo extends BasicArtifactInfo{
    /*
     * @gwt typeArgs org.mule.galaxy.rpc.WProperty
     */
    private List properties = new ArrayList();
    /*
     * @gwt typeArgs org.mule.galaxy.rpc.WComment
     */
    private List comments = new ArrayList();
    private String description;
    private String commentsFeedLink;
    private String artifactLink;
    private String artifactFeedLink;
    
    public String getArtifactFeedLink() {
        return artifactFeedLink;
    }

    public void setArtifactFeedLink(String artifactFeedLink) {
        this.artifactFeedLink = artifactFeedLink;
    }

    public String getCommentsFeedLink() {
        return commentsFeedLink;
    }

    public void setCommentsFeedLink(String commentsFeedLink) {
        this.commentsFeedLink = commentsFeedLink;
    }

    public String getArtifactLink() {
        return artifactLink;
    }

    public void setArtifactLink(String artifactLink) {
        this.artifactLink = artifactLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @gwt typeArgs org.mule.galaxy.web.rpc.WProperty
     */
    public List getProperties() {
        return properties;
    }

    /**
     * @gwt typeArgs org.mule.galaxy.web.rpc.WComment
     */
    public List getComments() {
        return comments;
    }
    
}
