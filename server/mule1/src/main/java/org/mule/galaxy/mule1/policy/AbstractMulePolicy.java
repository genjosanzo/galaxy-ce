package org.mule.galaxy.mule1.policy;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.util.Constants;

public abstract class AbstractMulePolicy implements Policy {

    public boolean applies(Item item) {
        return item instanceof ArtifactVersion && Constants.MULE_QNAME.equals(((Artifact)item.getParent()).getDocumentType());
    }

}
