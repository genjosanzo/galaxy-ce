package org.mule.galaxy.collab;

import java.util.Calendar;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.mapping.OneToMany;
import org.mule.galaxy.security.User;

public class Comment implements Identifiable {
    private String id;
    private User user;
    private Calendar date;
    private Comment parent;
    private Artifact artifact;
    private String text;
    
    private Set<Comment> comments;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Calendar getDate() {
        return date;
    }
    public void setDate(Calendar date) {
        this.date = date;
    }
    public Comment getParent() {
        return parent;
    }
    public void setParent(Comment parent) {
        this.parent = parent;
    }
    public Artifact getArtifact() {
        return artifact;
    }
    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    
    @OneToMany(mappedBy="parent")
    public Set<Comment> getComments() {
        return comments;
    }
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
    
}
