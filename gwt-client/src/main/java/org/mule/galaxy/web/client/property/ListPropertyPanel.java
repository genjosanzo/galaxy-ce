package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.util.InlineFlowPanel;

/**
 * Encapsulate a list of properties that is always editable. 
 */
public abstract class ListPropertyPanel extends AbstractEditPropertyPanel {
    protected List<Object> values;
    protected Collection<String> valuesToSave = new ArrayList<String>();
    protected Collection<String> valuesToDelete = new ArrayList<String>();
    
    protected FlowPanel editPanel;
    protected InlineFlowPanel editValuesPanel;
    protected InlineFlowPanel viewValuesPanel;
    
    public void initialize() {
        editValuesPanel = new InlineFlowPanel();
        editValuesPanel.setStyleName("add-property-inline");
        
        editPanel = new FlowPanel();
        editPanel.add(editValuesPanel);
        editPanel.add(getAddWidget());
        
        viewValuesPanel = new InlineFlowPanel();
        
        values = new ArrayList<Object>();
        if (property.getListValue() != null) {
            values.addAll(property.getListValue());
        }
        
        super.initialize();
        
        loadRemote();
    }

    protected abstract void loadRemote();
    
    protected void onFinishLoad() {
        redraw();
    }
    
    protected void redraw() {
        redrawEditPanel();
        
        redrawViewPanel();
    }

    protected void redrawViewPanel() {
        viewValuesPanel.clear();
        StringBuffer sb = new StringBuffer();
        for (Iterator<? extends Object> itr = values.iterator(); itr.hasNext();) {
            Object value = itr.next();
            
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(getRenderedText(value));
        }
        
        viewValuesPanel.add(new Label(sb.toString()));
    }

    protected void redrawEditPanel() {
        editValuesPanel.clear();

        for (Iterator<? extends Object> itr = values.iterator(); itr.hasNext();) {
            Object value = itr.next();
            editValuesPanel.add(createLabel(value));
        }
    }

    protected Widget createLabel(final Object value) {
        final SimplePanel container = new SimplePanel();
        container.setStyleName("listPropertyContainer");
        
        final InlineFlowPanel valuePanel = new InlineFlowPanel();
        valuePanel.setStyleName("listProperty");
        valuePanel.add(newLabel(getRenderedText(value), "listPropertyLeft"));
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
                values.remove(value);
                editValuesPanel.remove(container);
                
                removeLabel(value);
            }
            
        });
        valuePanel.add(right);
        
        container.add(valuePanel);
        
        return container;
    }

    protected void removeLabel(Object value) {
    }

    protected String getRenderedText(Object value) {
        return value.toString();
    }

    protected Object getValueToSave() {
        return values;
    }

    protected void onSave(Object value, Object response) {
        
    }

    protected Widget createEditForm() {
        return editPanel;
    }

    protected Widget createViewWidget() {
        return viewValuesPanel;
    }
    
    protected abstract Widget getAddWidget();
}
