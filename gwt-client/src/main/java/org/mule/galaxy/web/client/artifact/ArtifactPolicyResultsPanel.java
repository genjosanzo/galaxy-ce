package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.AbstractErrorShowingComposite;

public class ArtifactPolicyResultsPanel extends AbstractErrorShowingComposite {

    public ArtifactPolicyResultsPanel(Collection warnings, Collection failures) {
        super();
        
        FlowPanel panel = getMainPanel();
        if (warnings.size() > 0) {
            panel.add(createTitle("Warnings"));
            
            for (Iterator itr = warnings.iterator(); itr.hasNext();) {
                String warning = (String)itr.next();
                
                Label warningLabel = new Label(warning);
                warningLabel.setStyleName("warning-label");
                panel.add(warningLabel);
            }
        }
        

        if (failures.size() > 0) {
            panel.add(createTitle("Failures"));
            
            for (Iterator itr = failures.iterator(); itr.hasNext();) {
                String failure = (String)itr.next();
                
                Label failureLabel = new Label(failure);
                failureLabel.setStyleName("failure-label");
                panel.add(failureLabel);
            }
        }
        
        initWidget(panel);
    }
    
}
