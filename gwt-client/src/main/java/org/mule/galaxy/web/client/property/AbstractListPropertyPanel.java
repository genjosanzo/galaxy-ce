package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.util.InlineFlowPanel;

/**
 * Encapsulate a list of properties that is always editable. 
 */
public abstract class AbstractListPropertyPanel extends AbstractEditPropertyPanel {
    protected List<String> values;
    protected FlowPanel editPanel;
    protected InlineFlowPanel editValuesPanel;
    protected InlineFlowPanel viewValuesPanel;
    
    public void initialize() {
        editValuesPanel = new InlineFlowPanel();

        editPanel = new FlowPanel();
        editPanel.add(editValuesPanel);
        editPanel.add(getAddWidget());
        
        viewValuesPanel = new InlineFlowPanel();
        
        values = property.getListValue();
        if (values == null) {
            values = new ArrayList<String>();
        }
        
        super.initialize();
        
        loadRemote();
    }

    protected abstract void loadRemote();
    
    protected void onFinishLoad() {
        redraw();
    }
    
    protected void redraw() {
        editValuesPanel.clear();
        viewValuesPanel.clear();
        
        StringBuffer sb = new StringBuffer();
        for (Iterator<String> itr = values.iterator(); itr.hasNext();) {
            String id = itr.next();
            editValuesPanel.add(createLabel(id));
            
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(getRenderedText(id));
        }
        viewValuesPanel.add(new Label(sb.toString()));
    }

    protected Widget createLabel(final String id) {
        final SimplePanel container = new SimplePanel();
        container.setStyleName("listPropertyContainer");
        
        final InlineFlowPanel valuePanel = new InlineFlowPanel();
        valuePanel.setStyleName("listProperty");
        valuePanel.add(newLabel(getRenderedText(id), "listPropertyLeft"));
        final Label right = newLabel("x", "listPropertyRight");
        right.addMouseListener(new MouseListenerAdapter() {
            public void onMouseEnter(Widget sender) {
                right.setStyleName("listPropertyRightHover");
            }

            public void onMouseLeave(Widget sender) {
                right.setStyleName("listPropertyRight");
            }
        });
        right.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                values.remove(id);
                editValuesPanel.remove(container);
                
                removeLabel(id);
            }
            
        });
        valuePanel.add(right);
        
        container.add(valuePanel);
        
        return container;
    }

    protected void removeLabel(String id) {
    }

    protected String getRenderedText(String id) {
        return id;
    }

    protected Object getValueToSave() {
        return values;
    }

    protected void onSave(Object value) {
        
    }

    protected Widget createEditForm() {
        return editPanel;
    }

    protected Widget createViewWidget() {
        return viewValuesPanel;
    }
    
    protected abstract Widget getAddWidget();
}
