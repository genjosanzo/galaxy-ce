package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.MenuPanelPageInfo;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WIndex;

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
        table.setText(0, 1, "Id");
        table.setText(0, 2, "Language");
        table.setText(0, 3, "Query Type");
        
        adminPanel.getRegistryService().getIndexes(new AbstractCallback(adminPanel) {

            public void onSuccess(Object result) {
                Collection indexes = (Collection) result;
                
                int i = 1;
                for (Iterator itr = indexes.iterator(); itr.hasNext();) {
                    final WIndex idx = (WIndex) itr.next();
                    
                    Hyperlink hyperlink = new Hyperlink(idx.getName(), 
                                                        "user-" + idx.getId());
                    MenuPanelPageInfo page = new MenuPanelPageInfo(hyperlink, adminPanel) {
                        public AbstractComposite createInstance() {
                            return new IndexForm(adminPanel, idx, false);
                        }
                        
                    };
                    adminPanel.addPage(page);
                    
                    table.setWidget(i, 0, hyperlink);
                    table.setText(i, 1, idx.getId());
                    String type = idx.getLanguage();
                    if ("XPATH".equals(type)) {
                        table.setText(i, 2, "XPath");
                    } else {
                        table.setText(i, 2, "XQuery");
                    }
                    table.setText(i, 3, idx.getResultType());
                    table.getRowFormatter().setStyleName(i, "artifactTableEntry");
                    i++;
                }
            }
            
        });
    }
}
