package org.mule.galaxy.web.rpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtendedArtifactInfo extends BasicArtifactInfo{
    
    /*
     * @gwt typeArgs org.mule.galaxy.rpc.WComment
     */
    private List comments = new ArrayList();
    
    private String description;
    private String commentsFeedLink;
    private String artifactLink;
    private String artifactFeedLink;
    
    /*
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     */
    private Collection versions;
    
    public Collection getVersions() {
        return versions;
    }

    public void setVersions(Collection versions) {
        this.versions = versions;
    }

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
     * @gwt typeArgs org.mule.galaxy.web.rpc.WComment
     */
    public List getComments() {
        return comments;
    }
    
}
