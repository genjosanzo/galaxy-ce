package org.mule.galaxy.wsi.wsdl;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private List<AssertionResult> assertionResults = new ArrayList<AssertionResult>();

    public List<AssertionResult> getAssertionResults() {
        return assertionResults;
    }
    
    public void addAssertionResult(AssertionResult result) {
        assertionResults.add(result);
    }

    public boolean isFailed() {
        for (AssertionResult r : assertionResults) {
            if (r.isFailed()) return true;
        }
        return false;
    }
}
