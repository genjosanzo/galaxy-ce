package org.mule.galaxy.lifecycle;

import java.util.Calendar;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.security.User;

public class PhaseLogEntry implements Identifiable {
    private String id;
    private User user;
    private Phase phase;
    private ArtifactVersion artifactVersion;
    private Calendar calendar;
    
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
    public Phase getPhase() {
        return phase;
    }
    public void setPhase(Phase phase) {
        this.phase = phase;
    }
    public ArtifactVersion getArtifactVersion() {
        return artifactVersion;
    }
    public void setArtifactVersion(ArtifactVersion artifactVersion) {
        this.artifactVersion = artifactVersion;
    }
    public Calendar getCalendar() {
        return calendar;
    }
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
