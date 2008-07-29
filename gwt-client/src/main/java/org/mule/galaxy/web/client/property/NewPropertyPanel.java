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

package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.PropertyDescriptorComparator;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WProperty;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

public class NewPropertyPanel extends Composite {

    private ListBox propertiesBox;
    private FlowPanel propertyPanel;
    private ErrorPanel errorPanel;
    private String itemId;
    private EntryMetadataPanel metadataPanel;
    private RegistryServiceAsync svc;
    private Panel propertiesPanel;
    private ClickListener cancelListener;
    private SimplePanel renderHolder;
    private List propertyDescriptors;
    private final Galaxy galaxy;
    private WProperty property;
    private PropertyPanel renderer;

    public NewPropertyPanel(final Galaxy galaxy,
                            final ErrorPanel registryPanel, 
                            final RegistryServiceAsync registryService,
                            final String itemId,
                            final Panel propertiesPanel,
                            final EntryMetadataPanel metadataPanel,
                            final FlexTable table) {
        this.galaxy = galaxy;
        this.errorPanel = registryPanel;
        this.itemId = itemId;
        this.propertiesPanel = propertiesPanel; 
        this.metadataPanel = metadataPanel;
        this.svc = registryService;
        
        HorizontalPanel panel = new HorizontalPanel();
        panel.setStyleName("add-property-panel");
        panel.add(new Label("Add Property: "));
        
        propertyPanel = new FlowPanel();
        propertiesBox = new ListBox();
        propertiesBox.setMultipleSelect(false);
        propertiesBox.addItem("", "");
        
        propertiesBox.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                onPropertySelect((ListBox)w);
            }
            
        });
        propertyPanel.add(propertiesBox);
        
        renderHolder = new SimplePanel();
        propertyPanel.add(renderHolder);
        
        // Create a wrapper so the propertyPanel gets formatted correctly
        SimplePanel propPanelContainer = new SimplePanel();
        propPanelContainer.add(propertyPanel);
        panel.add(propPanelContainer);
        
        svc.getPropertyDescriptors(new AbstractCallback(registryPanel) {
            public void onSuccess(Object o) {
                initProperties((List) o);
            }
        });

        cancelListener = new ClickListener() {
            public void onClick(Widget arg0) {
                remove();
            }
        };
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(cancelListener);
        renderHolder.add(cancelButton);
        
        initWidget(panel);
    }
    

    protected void onPropertySelect(ListBox w) {
        int i = w.getSelectedIndex();
        String txt = w.getValue(i);
        
        WPropertyDescriptor pd = getPropertyDescriptor(txt);
        
        renderer = galaxy.getPropertyPanelFactory().createRenderer(pd.getExtension(), pd.isMultiValued());
        
        property = new WProperty(pd.getName(), pd.getDescription(), null, pd.getExtension(), false);
        renderer.setProperty(property);
        renderer.setGalaxy(galaxy);
        renderer.setItemId(itemId);
        renderer.setErrorPanel(errorPanel);
        renderer.setSaveListener(new ClickListener() {
            public void onClick(Widget arg0) {
                save();
            }
        });
        renderer.setCancelListener(cancelListener);
        renderer.initialize();
        renderer.showEdit();

        renderHolder.clear();
        renderHolder.add(renderer);
    }

    protected void save() {
        remove();
        
        metadataPanel.addRow(property);
    }
    
    private void remove() {
        propertiesPanel.remove(this);
    }


    private WPropertyDescriptor getPropertyDescriptor(String txt) {
        for (Iterator itr = propertyDescriptors.iterator(); itr.hasNext();) {
            WPropertyDescriptor pd = (WPropertyDescriptor) itr.next();
           
            if (txt.equals(pd.getName())) {
                return pd;
            }
        }
        return null;
    }

    protected void initProperties(List o) {
        this.propertyDescriptors = o;
        Collections.sort(o, new PropertyDescriptorComparator());
        
        for (Iterator itr = o.iterator(); itr.hasNext();) {
            WPropertyDescriptor pd = (WPropertyDescriptor) itr.next();
            propertiesBox.addItem(pd.getDescription(), pd.getName());
        }
    }

}
