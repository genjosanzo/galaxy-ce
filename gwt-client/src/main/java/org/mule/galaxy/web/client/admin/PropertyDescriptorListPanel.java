package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.MenuPanelPageInfo;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

public class PropertyDescriptorListPanel
    extends AbstractComposite
{
    private AdministrationPanel adminPanel;
    private FlowPanel panel;

    public PropertyDescriptorListPanel(AdministrationPanel a) {
        super();
        
        this.adminPanel = a;
        
        panel = new FlowPanel();
        initWidget(panel);
    }
    
    public void onShow() {
        panel.clear();
        
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
                                                        "property-" + prop.getName());
                    MenuPanelPageInfo page = new MenuPanelPageInfo(hyperlink, adminPanel) {
                        public AbstractComposite createInstance() {
                            return new PropertyDescriptorForm(adminPanel, prop, false);
                        }
                    };
                    adminPanel.addPage(page);
                    
                    table.setWidget(i, 0, hyperlink);
                    table.setText(i, 1, prop.getDescription());
                    
                    table.getRowFormatter().setStyleName(i, "artifactTableEntry");
                    i++;
                }
            }
            
        });
    }
}
