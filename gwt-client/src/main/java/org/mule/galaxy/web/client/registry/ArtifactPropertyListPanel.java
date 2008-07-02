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
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.rpc.WSearchResults;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPermissionGrant;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.Collection;


public class ArtifactPropertyListPanel extends AbstractComposite {

    private WSearchResults wsearch;
    private SimplePanel lifecyclePanel;
    private SimplePanel phasePanel;
    private SimplePanel securityPanel;
    private FlowPanel panel;
    private final Galaxy galaxy;


    public ArtifactPropertyListPanel(WSearchResults o, Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
        this.wsearch = o;
        this.lifecyclePanel = new SimplePanel();
        this.securityPanel = new SimplePanel();
        this.phasePanel = new SimplePanel();
        this.panel = new FlowPanel();
        initWidget(panel);
    }

    public void onShow() {
        super.onShow();

        /*

        galaxy.getRegistryService().getLifecycles(new AbstractCallback(errorPanel){
            public void onSuccess(Object arg0) {
                initLifecycleProperties((Collection)arg0);
            }

        });


        initPhaseProperties(XXX);
        initSecurityProperties(XXX);
        */
    }


    private void initLifecycleProperties(Collection lifecycles) {
        Label label = new Label("LifeCycle");
        label.setStyleName("right-title");
        lifecyclePanel.add(label);
        panel.add(lifecyclePanel);
    }

    private void initPhaseProperties(WLifecycle phases) {
        Label label = new Label("Phase");
        label.setStyleName("right-title");
        phasePanel.add(label);
        panel.add(phasePanel);
    }


    private void initSecurityProperties(WPermissionGrant pg) {
        Label label = new Label("Security");
        label.setStyleName("right-title");
        securityPanel.add(label);
        panel.add(securityPanel);
    }

    // edit the property
    public void doEdit() {

    }

}
