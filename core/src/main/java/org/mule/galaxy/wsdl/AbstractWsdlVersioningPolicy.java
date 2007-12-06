package org.mule.galaxy.wsdl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.WSDLException;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.impl.RegistryWSDLLocator;
import org.mule.galaxy.policy.Approval;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

import org.w3c.dom.Document;

/**
 * Provides means to ensure that WSDL versioning rules are met.
 */
public abstract class AbstractWsdlVersioningPolicy implements ArtifactPolicy {
    private Logger LOGGER = LogUtils.getL7dLogger(AbstractWsdlVersioningPolicy.class);

    public Approval isApproved(final Artifact a, ArtifactVersion previous, final ArtifactVersion next) {
        if (previous == null) {
            return Approval.APPROVED;
        }
        
        final Approval app = new Approval();
        app.setApproved(true);
        
        try {
            WsdlDiff diff = new WsdlDiff();
            diff.setOriginalWSDL((Document) previous.getData(), new RegistryWSDLLocator());
            diff.setNewWSDL((Document) next.getData(), new RegistryWSDLLocator());
            diff.check(new DifferenceListener() {
                public void onEvent(DifferenceEvent event) {
                    System.out.println("EVENT " + event.getDescription());
                    check(app, event);
                }
            });
        } catch (WSDLException e) {
            app.setApproved(false);
            app.getMessages().add("There was an error processing the WSDL: " + e.getMessage());
            LOGGER.log(Level.INFO, "There was an error processing the Artifact " + a.getId(), e);
        }
        
        return app;
    }
    
    protected abstract void check(final Approval app, DifferenceEvent event);
}
