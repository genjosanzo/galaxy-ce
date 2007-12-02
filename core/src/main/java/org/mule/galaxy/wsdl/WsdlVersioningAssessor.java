package org.mule.galaxy.wsdl;

import java.util.Set;

import javax.wsdl.Definition;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.VersionApproval;
import org.mule.galaxy.VersionAssessor;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

/**
 * Provides means to ensure that WSDL versioning rules are met.
 */
public class WsdlVersioningAssessor implements VersionAssessor {
    private Set<Phase> forwardCompatabilityPhases;
    private Set<Phase> backwardCompatabilityPhases;
    
    public VersionApproval isApproved(final Artifact a, final ArtifactVersion next) {
        ArtifactVersion latest = a.getLatestVersion();
        
        final VersionApproval app = new VersionApproval();
        
        WsdlDiff diff = new WsdlDiff();
        diff.setOriginalWSDL((Definition) latest.getData());
        diff.setNewWSDL((Definition) next.getData());
        diff.check(new DifferenceListener() {
            public void onEvent(DifferenceEvent event) {
                if (!event.isBackwardCompatabile() && backwardCompatabilityPhases != null
                    && backwardCompatabilityPhases.contains(a.getPhase())) {
                    app.getMessages().add(event.getDescription());
                    app.setApproved(false);
                }
                
                if (!event.isForwardCompatabile() && forwardCompatabilityPhases != null
                    && forwardCompatabilityPhases.contains(a.getPhase())) {
                    app.getMessages().add(event.getDescription());
                    app.setApproved(false);
                }
            }
        });
        
        return app;
    }

    public Set<Phase> getForwardCompatabilityPhases() {
        return forwardCompatabilityPhases;
    }

    public void setForwardCompatabilityPhases(Set<Phase> forwardCompatabilityPhases) {
        this.forwardCompatabilityPhases = forwardCompatabilityPhases;
    }

    public Set<Phase> getBackwardCompatabilityPhases() {
        return backwardCompatabilityPhases;
    }

    public void setBackwardCompatabilityPhases(Set<Phase> backwardCompatabilityPhases) {
        this.backwardCompatabilityPhases = backwardCompatabilityPhases;
    }
}
