package org.mule.galaxy.web.client.property;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulate a list of properties that is always editable.
 */
public abstract class AbstractListRenderer extends AbstractPropertyRenderer {
    protected List<Object> values;
    private Label valueLabel;
    protected FlowPanel editValuesPanel;

    public Widget createEditForm() {
        editValuesPanel = new FlowPanel();
//        editValuesPanel.setStyleName("add-property-inline");

        FlowPanel editPanel = new FlowPanel();
        editPanel.add(editValuesPanel);

        loadRemote();

        editPanel.add(getAddWidget());

        return editPanel;
    }

    public Widget createViewWidget() {
        InlineFlowPanel viewValuesPanel = new InlineFlowPanel();
        valueLabel = new Label();
        viewValuesPanel.add(valueLabel);

        loadRemote();

        return viewValuesPanel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Galaxy galaxy, ErrorPanel errorPanel, Object value, boolean bulkEdit) {
        super.initialize(galaxy, errorPanel, value, bulkEdit);

        values = new ArrayList<Object>();
        if (value != null) {
            values.addAll((List<Object>) value);
        }
    }

    protected void loadRemote() {
    }

    protected void onFinishLoad() {
        redraw();
    }

    protected void redraw() {
        if (editValuesPanel != null) {
            redrawEditPanel();
        }

        if (valueLabel != null) {
            redrawViewPanel();
        }
    }

    protected void redrawViewPanel() {
        StringBuffer sb = new StringBuffer();
        for (Iterator<? extends Object> itr = values.iterator(); itr.hasNext();) {
            Object value = itr.next();

            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(getRenderedText(value));
        }

        valueLabel.setText(sb.toString());
    }

    protected void redrawEditPanel() {
        editValuesPanel.clear();

        for (Iterator<? extends Object> itr = values.iterator(); itr.hasNext();) {
            Object value = itr.next();
            editValuesPanel.add(createLabel(value));
        }
    }

    protected Widget createLabel(final Object value) {
        final FlowPanel container = new FlowPanel();
        container.setStyleName("clearfix listPropertyContainer");

        final InlineFlowPanel valuePanel = new InlineFlowPanel();
        valuePanel.setStyleName("listProperty");
        Label left = new Label(getRenderedText(value));
        left.setStylePrimaryName("listPropertyLeft");
        valuePanel.add(left);
        Image del = new Image("images/page_text_delete.gif");
        del.setStyleName("icon-baseline");
        /*
        final Label right = newLabel(" ", "listPropertyRight");

        del.addMouseListener(new MouseListenerAdapter() {
            public void onMouseEnter(Widget sender) {
                del.setStyleName("listPropertyRightHover");
            }

            public void onMouseLeave(Widget sender) {
                del.setStyleName("listPropertyRight");
            }
        });
        */
        del.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                editValuesPanel.remove(container);
                removeLabel(value);
            }

        });
        //valuePanel.add(right);
        valuePanel.add(del);
        container.add(valuePanel);
        return container;
    }

    protected void removeLabel(Object value) {
        values.remove(value);
    }

    protected String getRenderedText(Object value) {
        return value.toString();
    }

    public Object getValueToSave() {
        return values;
    }

    protected abstract Widget getAddWidget();
}
