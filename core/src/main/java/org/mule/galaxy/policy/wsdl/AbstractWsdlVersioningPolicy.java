package org.mule.galaxy.policy.wsdl;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.Registry;
import org.mule.galaxy.api.policy.ApprovalMessage;
import org.mule.galaxy.api.policy.ArtifactPolicy;
import org.mule.galaxy.api.util.LogUtils;
import org.mule.galaxy.impl.RegistryLocator;
import org.mule.galaxy.api.util.Constants;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.WSDLException;

import org.w3c.dom.Document;

/**
 * Provides means to ensure that WSDL versioning rules are met.
 */
public abstract class AbstractWsdlVersioningPolicy implements ArtifactPolicy
{
    private Logger LOGGER = LogUtils.getL7dLogger(AbstractWsdlVersioningPolicy.class);
    private Registry registry;
    
    public boolean applies(Artifact a) {
        return Constants.WSDL_DEFINITION_QNAME.equals(a.getDocumentType());
    }

    @SuppressWarnings("unchecked")
    public Collection<ApprovalMessage> isApproved(final Artifact a, ArtifactVersion previous, final ArtifactVersion next) {
        if (previous == null) {
            return Collections.EMPTY_SET;
        }
        
        final Collection<ApprovalMessage> messages = new ArrayList<ApprovalMessage>();
        
        try {
            WsdlDiff diff = new WsdlDiff();
            // TODO: make data a Definition object
            diff.setOriginalWSDL((Document) previous.getData(), new RegistryLocator(registry, a.getWorkspace()));
            diff.setNewWSDL((Document) next.getData(), new RegistryLocator(registry, a.getWorkspace()));
            diff.check(new DifferenceListener() {
                public void onEvent(DifferenceEvent event) {
                    check(messages, event);
                }
            });
        } catch (WSDLException e) {
            messages.add(new ApprovalMessage("There was an error processing the WSDL: " + e.getMessage()));
            
            LOGGER.log(Level.INFO, "There was an error processing the Artifact " + a.getId(), e);
        }
        
        return messages;
    }
    
    protected abstract void check(final Collection<ApprovalMessage> messages, DifferenceEvent event);

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
}
