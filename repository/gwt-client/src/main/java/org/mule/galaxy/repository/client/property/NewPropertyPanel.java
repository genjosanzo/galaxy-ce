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

package org.mule.galaxy.repository.client.property;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.repository.client.RepositoryModule;
import org.mule.galaxy.repository.client.util.PropertyDescriptorComparator;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WProperty;
import org.mule.galaxy.repository.rpc.WPropertyDescriptor;
import org.mule.galaxy.web.client.ui.panel.ErrorPanel;
import org.mule.galaxy.web.client.ui.panel.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class NewPropertyPanel extends Composite {

    private ListBox propertiesBox;
    private ErrorPanel errorPanel;
    private String itemId;
    private EntryMetadataPanel metadataPanel;
    private RegistryServiceAsync svc;
    private Panel propertiesPanel;
    private ClickListener cancelListener;
    private List propertyDescriptors;
    private WProperty property;
    private Button cancelButton;
    private FlexTable panel;
    private InlineFlowPanel selectorPanel;
    private final RepositoryModule repositoryModule;

    public NewPropertyPanel(final RepositoryModule repositoryModule,
                            final ErrorPanel registryPanel,
                            final RegistryServiceAsync registryService,
                            final String itemId,
                            final Panel propertiesPanel,
                            final EntryMetadataPanel metadataPanel,
                            final FlexTable table) {
        this.repositoryModule = repositoryModule;
        this.errorPanel = registryPanel;
        this.itemId = itemId;
        this.propertiesPanel = propertiesPanel; 
        this.metadataPanel = metadataPanel;
        this.svc = registryService;
        
        FlowPanel main = new FlowPanel();
        
        panel = new FlexTable();
        main.add(panel);
        panel.setStyleName("add-property-panel");
        panel.getCellFormatter().setWidth(0, 0, "225");
        selectorPanel = new InlineFlowPanel();
        
        selectorPanel.add(new Label("Add Property: "));
        
        propertiesBox = new ListBox();
        propertiesBox.setMultipleSelect(false);
        propertiesBox.addItem("", "");
        
        propertiesBox.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                onPropertySelect((ListBox)w);
            }
            
        });
        selectorPanel.add(propertiesBox);
        
        panel.setWidget(0, 0, selectorPanel);
        
        svc.getPropertyDescriptors(false, new AbstractCallback(registryPanel) {
            @SuppressWarnings("unchecked")
            public void onSuccess(Object o) {
                initProperties((List<WPropertyDescriptor>) o);
            }
        });

        cancelListener = new ClickListener() {
            public void onClick(Widget arg0) {
                remove();
            }
        };
        cancelButton = new Button("Cancel");
        cancelButton.addClickListener(cancelListener);
        selectorPanel.add(cancelButton);
        
        main.add(new SimplePanel());
        initWidget(main);
    }
    

    protected void onPropertySelect(ListBox w) {
        int i = w.getSelectedIndex();
        if (i == -1) {
            return;
        }
        selectorPanel.remove(cancelButton);
        
        String txt = w.getValue(i);
        
        WPropertyDescriptor pd = getPropertyDescriptor(txt);

        AbstractPropertyRenderer renderer = 
            repositoryModule.getPropertyInterfaceManager().createRenderer(pd.getExtension(), pd.isMultiValued());
        
        EditPropertyPanel render = new EditPropertyPanel(renderer, errorPanel);
        property = new WProperty(pd.getName(), pd.getDescription(), null, pd.getExtension(), false);
        property.setMultiValued(pd.isMultiValued());
        render.setProperty(property);
        render.setRepositoryModule(repositoryModule);
        render.setItemId(itemId);
        render.setErrorPanel(errorPanel);
        render.setSaveListener(new ClickListener() {
            public void onClick(Widget arg0) {
                save();
            }
        });
        render.setCancelListener(cancelListener);
        render.initialize();
        render.showEdit();
        
        panel.setWidget(0, 1, render);
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

    protected void initProperties(List<WPropertyDescriptor> o) {
        this.propertyDescriptors = o;
        Collections.sort(o, new PropertyDescriptorComparator());
        
        for (Iterator itr = o.iterator(); itr.hasNext();) {
            WPropertyDescriptor pd = (WPropertyDescriptor) itr.next();
            
            if (!metadataPanel.hasProperty(pd.getName())) {
                propertiesBox.addItem(pd.getDescription(), pd.getName());
            }
        }
    }

}
