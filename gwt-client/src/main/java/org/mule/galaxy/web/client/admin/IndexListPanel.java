package org.mule.galaxy.web.client.admin;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.MenuPanelPageInfo;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WIndex;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

public class IndexListPanel
    extends AbstractComposite
{
    private AdministrationPanel adminPanel;
    private FlowPanel panel;

    public IndexListPanel(AdministrationPanel a) {
        super();
        
        this.adminPanel = a;
        
        panel = new FlowPanel();
        initWidget(panel);
    }
    
    public void onShow() {
        panel.clear();
        
        final FlexTable table = createTitledRowTable(panel, "Indexes");
        
        table.setText(0, 0, "Index");
        table.setText(0, 1, "Language");
        table.setText(0, 2, "Query Type");
        
        adminPanel.getRegistryService().getIndexes(new AbstractCallback(adminPanel) {

            public void onSuccess(Object result) {
                Collection indexes = (Collection) result;
                
                int i = 1;
                for (Iterator itr = indexes.iterator(); itr.hasNext();) {
                    final WIndex idx = (WIndex) itr.next();
                    
                    String type = idx.getIndexer();
                    if ("org.mule.galaxy.impl.index.GroovyIndexer".equalsIgnoreCase(type))
                    {
                        table.setText(i, 0, idx.getDescription());
                    }
                    else 
                    {
                        Hyperlink hyperlink = new Hyperlink(idx.getDescription(), 
                                                            "user-" + idx.getId());
                        MenuPanelPageInfo page = new MenuPanelPageInfo(hyperlink, adminPanel) {
                            public AbstractComposite createInstance() {
                                return new IndexForm(adminPanel, idx, false);
                            }
                            
                        };
                        adminPanel.addPage(page);
                        
                        table.setWidget(i, 0, hyperlink);
                    }
                    
                    if ("xpath".equalsIgnoreCase(type))
                    {
                        table.setText(i, 1, "XPath");
                    }
                    else if ("xquery".equalsIgnoreCase(type))
                    {
                        table.setText(i, 1, "XQuery");
                    }
                    else if ("groovy".equalsIgnoreCase(type))
                    {
                        table.setText(i, 1, "Groovy");
                    }
                    else
                    {
                        table.setText(i, 1, type);
                    }
                    table.setText(i, 2, idx.getResultType());
                    table.getRowFormatter().setStyleName(i, "artifactTableEntry");
                    i++;
                }
            }
            
        });
    }
}
