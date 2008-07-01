/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z merv $
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

package org.mule.galaxy.web.client.registry;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.rpc.WSearchResults;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;


public class ArtifactPropertyListPanel extends AbstractComposite {

    private WSearchResults wsearch;
    private SimplePanel lifecyclePanel;
    private SimplePanel phasePanel;
    private SimplePanel securityPanel;
    private FlowPanel panel;

    public ArtifactPropertyListPanel(WSearchResults o) {
        super();
        this.wsearch = o;
        this.lifecyclePanel = new SimplePanel();
        this.securityPanel = new SimplePanel();
        this.phasePanel = new SimplePanel();
        this.panel = new FlowPanel();
        initWidget(panel);
    }

    public void onShow() {
        super.onShow();
    }


    public void render() {
        initLifecycleProperties();
        initSecurityProperties();
        initPhaseProperties();
    }

    private void initLifecycleProperties() {
        Label label = new Label("LifeCycle");
        label.setStyleName("right-title");
        lifecyclePanel.add(label);
        panel.add(lifecyclePanel);
    }

    private void initPhaseProperties() {
        Label label = new Label("Phase");
        label.setStyleName("right-title");
        phasePanel.add(label);
        panel.add(phasePanel);
    }


    private void initSecurityProperties() {
        Label label = new Label("Security");
        label.setStyleName("right-title");
        securityPanel.add(label);
        panel.add(securityPanel);
    }

    // edit the property
    public void doEdit() {

    }

}
