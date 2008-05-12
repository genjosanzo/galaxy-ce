package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

public class PropertyDescriptorListPanel
    extends AbstractAdministrationComposite
{
    public PropertyDescriptorListPanel(AdministrationPanel a) {
        super(a);
    }
    
    public void onShow() {
        super.onShow();
        
        final FlexTable table = createTitledRowTable(panel, "Properties");
        
        table.setText(0, 0, "Property");
        table.setText(0, 1, "Description");
        
        adminPanel.getRegistryService().getPropertyDescriptors(new AbstractCallback(adminPanel) {

            public void onSuccess(Object result) {
                Collection props = (Collection) result;
                
                int i = 1;
                for (Iterator itr = props.iterator(); itr.hasNext();) {
                    final WPropertyDescriptor prop = (WPropertyDescriptor) itr.next();
                    
                    Hyperlink hyperlink = new Hyperlink(prop.getName(), 
                                                        "properties/" + prop.getName());
                    
                    table.setWidget(i, 0, hyperlink);
                    table.setText(i, 1, prop.getDescription());
                    
                    table.getRowFormatter().setStyleName(i, "artifactTableEntry");
                    i++;
                }
            }
            
        });
    }
}
