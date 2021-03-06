package org.mule.galaxy.impl.lifecycle;

import static org.mule.galaxy.util.AbderaUtils.assertNotEmpty;
import static org.mule.galaxy.util.AbderaUtils.throwMalformed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.event.LifecycleTransitionEvent;
import org.mule.galaxy.extension.AtomExtension;
import org.mule.galaxy.impl.extension.AbstractExtension;
import org.mule.galaxy.impl.jcr.JcrItem;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.util.SecurityUtils;

public class LifecycleExtension extends AbstractExtension implements AtomExtension {
    private static final QName LIFECYCLE_QNAME = new QName(Constants.ATOM_NAMESPACE, "lifecycle");
    private static final Collection<QName> UNDERSTOOD = new ArrayList<QName>();

    static {
        UNDERSTOOD.add(LIFECYCLE_QNAME);
    }

    private LifecycleManager lifecycleManager;

    public LifecycleExtension() {
        name = "Lifecycle";

        queryProperties = new HashMap<String, String>();
        queryProperties.put("phase", "");
    }

    public Object get(Item item, PropertyDescriptor pd, boolean getWithNoData) {
        Object storedValue = item.getInternalProperty(pd.getProperty());

        if (storedValue == null) {
            return null;
        }

        List ids = (List) storedValue;
        return lifecycleManager.getPhaseById((String) ids.get(1));
    }

    public void store(Item item, PropertyDescriptor pd, Object value)
            throws PolicyException, PropertyException, AccessException {
        Phase phase = (Phase) value;

        if (value == null) {
            item.setInternalProperty(pd.getProperty(), null);
        } else {

            Phase oldPhase = item.getProperty(pd.getProperty());
            if (oldPhase != null && oldPhase.equals(phase)) {
                return;
            }
            
            if (!lifecycleManager.isTransitionAllowed(item, pd.getProperty(), phase)) {
                throw new PolicyException(item, "Transition to phase " + phase + " is not allowed.");
            }
            LifecycleTransitionEvent event = new LifecycleTransitionEvent(SecurityUtils.getCurrentUser(),
                                            item,
                                            oldPhase != null ? oldPhase.getName() : null,
                                            phase.getName(),
                                            phase.getLifecycle().getName());

            ((JcrItem) item).getSaveEvents().add(event);

            item.setInternalProperty(pd.getProperty(), 
                                     Arrays.asList(phase.getLifecycle().getId(), phase.getId()));
        }
    }

    public void validate(Item item, PropertyDescriptor pd, Object valueToStore) throws PolicyException {
        Phase phase = (Phase) valueToStore;

        if (!lifecycleManager.isTransitionAllowed(item, pd.getProperty(), phase)) {
            throw new PolicyException(item, "Transition to phase " + phase + " is not allowed.");
        }

    }

    public Object getValue(Item item, ExtensibleElement e, Factory factory) throws ResponseContextException {
        String name = e.getAttributeValue("name");
        assertNotEmpty(name, "Lifecycle name attribute cannot be null.");

        String phaseName = e.getAttributeValue("phase");
        assertNotEmpty(phaseName, "Lifecycle phase attribute cannot be null.");

        LifecycleManager lifecycleManager = item.getLifecycleManager();
        Lifecycle lifecycle = lifecycleManager.getLifecycle(name);

        if (lifecycle == null)
            throwMalformed("Lifecycle \"" + name + "\" does not exist.");

        Phase phase = lifecycle.getPhase(phaseName);

        if (phase == null)
            throwMalformed("Lifecycle phase \"" + phaseName + "\" does not exist.");

        return phase;
    }

    public Collection<QName> getUnderstoodElements() {
        return UNDERSTOOD;
    }

    public void annotateAtomEntry(Item item, PropertyDescriptor pd, Entry entry, ExtensibleElement metadata, Factory factory) {
        ExtensibleElement lifecycle = factory.newElement(LIFECYCLE_QNAME);
        lifecycle.setAttributeValue("property", pd.getProperty());

        Phase phase = item.getProperty(pd.getProperty());
        lifecycle.setAttributeValue("name", phase.getLifecycle().getName());
        lifecycle.setAttributeValue("phase", phase.getName());

        metadata.addExtension(lifecycle);

        buildAvailablePhases(phase, phase.getNextPhases(), "next-phases", lifecycle, factory);
        buildAvailablePhases(phase, phase.getPreviousPhases(), "previous-phases", lifecycle, factory);
    }

    private Element buildAvailablePhases(Phase phase, Set<Phase> phases, String name, ExtensibleElement lifecycle, Factory factory) {
        Element availPhases = factory.newElement(new QName(Constants.ATOM_NAMESPACE, name), lifecycle);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Phase p : phases) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }

            sb.append(p.getName());
        }
        availPhases.setText(sb.toString());
        return availPhases;
    }

    public boolean isMultivalueSupported() {
        return false;
    }

    public LifecycleManager getLifecycleManager() {
        return lifecycleManager;
    }
    
    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

}
