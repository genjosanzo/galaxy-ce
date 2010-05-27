package org.mule.galaxy.mule2.policy;

import org.mule.galaxy.impl.policy.AbstractXmlPolicy;
import org.mule.galaxy.util.Constants;

public abstract class AbstractMulePolicy extends AbstractXmlPolicy {

    protected AbstractMulePolicy(String id, String name, String description) {
        super(id, name, description);
        
        supportedDocumentTypes.add(Constants.MULE2_0_QNAME);
        supportedDocumentTypes.add(Constants.MULE2_1_QNAME);
        supportedDocumentTypes.add(Constants.MULE2_2_QNAME);
        supportedDocumentTypes.add(Constants.MULE3_QNAME);
    }
}
