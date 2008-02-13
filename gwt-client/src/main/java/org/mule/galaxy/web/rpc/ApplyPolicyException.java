package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Map;

public class ApplyPolicyException extends Exception implements IsSerializable {
    
    private Map artifactToMessages;

    public ApplyPolicyException() {
        super();
    }

    public ApplyPolicyException(Map artifactToMessages) {
        super();
        this.artifactToMessages = artifactToMessages;
    }

    public Map getPolicyFailures() {
        return artifactToMessages;
    }
}
