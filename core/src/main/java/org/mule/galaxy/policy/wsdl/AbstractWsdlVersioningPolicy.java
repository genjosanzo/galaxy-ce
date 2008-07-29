package org.mule.galaxy.policy.wsdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.wsdl.WSDLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.RegistryLocator;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

import org.w3c.dom.Document;

/**
 * Provides means to ensure that WSDL versioning rules are met.
 */
public abstract class AbstractWsdlVersioningPolicy implements Policy
{
    private final Log log = LogFactory.getLog(getClass());
    private Registry registry;
    
    public boolean applies(Item item) {
        return item instanceof ArtifactVersion && 
            Constants.WSDL_DEFINITION_QNAME.equals(((Artifact)item.getParent()).getDocumentType());
    }

    @SuppressWarnings("unchecked")
    public Collection<ApprovalMessage> isApproved(final Item item) {
        ArtifactVersion next = (ArtifactVersion) item;
        ArtifactVersion previous = (ArtifactVersion) next.getPrevious();
        if (previous == null) {
            return Collections.EMPTY_SET;
        }
        
        final Collection<ApprovalMessage> messages = new ArrayList<ApprovalMessage>();
        
        Workspace w = (Workspace) item.getParent().getParent();
        try {
            WsdlDiff diff = new WsdlDiff();
            // TODO: make data a Definition object
            diff.setOriginalWSDL((Document) previous.getData(), new RegistryLocator(registry, w));
            diff.setNewWSDL((Document) next.getData(), new RegistryLocator(registry, w));
            diff.check(new DifferenceListener() {
                public void onEvent(DifferenceEvent event) {
                    check(messages, event);
                }
            });
        } catch (WSDLException e) {
            messages.add(new ApprovalMessage("There was an error processing the WSDL: " + e.getMessage()));
            
            log.error("There was an error processing the Artifact " + item.getPath(), e);
        }
        
        return messages;
    }
    
    protected abstract void check(final Collection<ApprovalMessage> messages, DifferenceEvent event);

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
}
