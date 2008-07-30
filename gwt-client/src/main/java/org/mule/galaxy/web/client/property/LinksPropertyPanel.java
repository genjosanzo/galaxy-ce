package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.LinkInfo;

public class LinksPropertyPanel extends PropertyPanel {

    protected Collection links;

    public void initialize() {
        super.initialize();
        
        galaxy.getRegistryService().getLinks(itemId, getProperty().getName(), new AbstractCallback(errorPanel) {

            public void onSuccess(Object o) {
                links = (Collection) o;
                updateLabel();
            }
            
        });
    }

    protected void updateLabel() {
        InlineFlowPanel linkPanel = new InlineFlowPanel();
        InlineFlowPanel recipPanel = new InlineFlowPanel();

        recipPanel.add(new Label("(Depended on by: "));
        
        for (Iterator itr = links.iterator(); itr.hasNext();) {
            final LinkInfo info = (LinkInfo) itr.next();

            Widget w;
            if (info.getItemType() == LinkInfo.TYPE_NOT_FOUND) {
                w = new Label(info.getItemName());
            } else {
                String prefix;
                if (info.getItemType() == LinkInfo.TYPE_ENTRY) {
                    prefix = "artifact_";
                } else {
                    prefix = "artifact-version_";
                }
                final String token = prefix + info.getItemId();
                Hyperlink hl = new Hyperlink(info.getItemName(), token);
                hl.addClickListener(new ClickListener() {
    
                    public void onClick(Widget arg0) {
                        History.newItem(token);
                    }
                });
                
                w = hl;
            }
            
            if (info.isReciprocal()) {
                if (recipPanel.getWidgetCount() > 2) {
                    recipPanel.add(new Label(", "));
                }
                recipPanel.add(w);
            } else {
                if (linkPanel.getWidgetCount() > 2) {
                    linkPanel.add(new Label(", "));
                }
                linkPanel.add(w);
            }
        }
        panel.clear();
        
        recipPanel.add(new Label(")"));

        FlowPanel container = new FlowPanel();
        container.add(linkPanel);
        
        if (recipPanel.getWidgetCount() > 2) {
            container.add(recipPanel);
        }
        panel.add(container);
    }

    protected Object getValueToSave() {
        // TODO Auto-generated method stub
        return null;
    }

    protected void onSave(Object value) {
        // TODO Auto-generated method stub
        
    }

    public void showView() {
        // TODO Auto-generated method stub
        
    }

    public void showEdit() {
        
    }
    

    
}
