/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.repository.client.browse;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.ui.panel.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;

public class PolicyResultsPanel extends AbstractErrorShowingComposite {

    public PolicyResultsPanel(Galaxy galaxy, Collection<String> warnings, Collection<String> failures) {
        super();
        
        FlowPanel panel = getMainPanel();
        
        if (warnings.size() > 0) {
            panel.add(createTitle("Warnings"));
            
            for (Iterator<String> itr = warnings.iterator(); itr.hasNext();) {
                String warning = itr.next();
                
                Label warningLabel = new Label(warning);
                warningLabel.setStyleName("warning-label");
                panel.add(warningLabel);
            }
        }
        

        if (failures.size() > 0) {
            panel.add(createTitle("Failures"));
            
            for (Iterator<String> itr = failures.iterator(); itr.hasNext();) {
                String failure = itr.next();
                
                Label failureLabel = new Label(failure);
                failureLabel.setStyleName("failure-label");
                panel.add(failureLabel);
            }
        }
        
        initWidget(panel);
    }

    @Override
    public void showPage(List<String> params) {
        super.showPage(params);
    }

}
