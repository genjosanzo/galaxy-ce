package org.mule.galaxy.policy.wsdl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.wsdl.WSDLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.impl.RegistryLocator;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.type.TypeManager;
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
        return item.getType().inheritsFrom(TypeManager.ARTIFACT_VERSION) && 
            Constants.WSDL_DEFINITION_QNAME.equals(((Artifact)item.getProperty("artifact")).getDocumentType());
    }

    @SuppressWarnings("unchecked")
    public Collection<ApprovalMessage> isApproved(final Item item) {
        final Collection<ApprovalMessage> messages = new ArrayList<ApprovalMessage>();
        
        try {
            Artifact next = (Artifact) item.getProperty("artifact");
            Item previousItem = null;
            for (Item i : item.getParent().getItems()) {
                if (previousItem == null && i != item) {
                    previousItem = i;
                } else if (previousItem != null 
                        && i.getCreated().before(item.getCreated()) 
                        && i.getCreated().after(previousItem.getCreated())) {
                    previousItem = i;
                }
            }
            
            Artifact previous = (Artifact) previousItem.getProperty("artifact");
            if (previous == null) {
                return messages;
            }
            
            Item w = (Item) item.getParent().getParent();
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
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
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
