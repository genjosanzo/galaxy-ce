package org.mule.galaxy.web.client.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.ItemPathOracle;
import org.mule.galaxy.web.client.validation.Validator;
import org.mule.galaxy.web.client.validation.ui.ValidatableSuggestBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.LinkInfo;
import org.mule.galaxy.web.rpc.WLinks;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * NOTE: I apologize for the mess that is this class.
 */
public class LinksRenderer extends AbstractListRenderer {

    private ValidatableSuggestBox suggest;
    private Button addButton;
    private String reciprocalName;
    private Label verifyLabel;

    @Override
    public void initialize(Galaxy galaxy, ErrorPanel errorPanel, Object value, boolean bulkEdit) {
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
        this.value = value;
        
        WLinks links = getLinks();
        values = new ArrayList<Object>();
        values.addAll(links.getLinks());
        values.addAll(links.getReciprocal());
        
        reciprocalName = links.getReciprocalName();
    }

    @Override
    public Object getValueToSave() {
        return getLinks();
    }

    private WLinks getLinks() {
        if (value == null) {
            WLinks links = new WLinks();
            links.setLinks(new ArrayList<LinkInfo>());
            links.setReciprocal(new ArrayList<LinkInfo>());
            value = links;
        }
        return (WLinks) value;
    }

    @Override
    protected void redrawEditPanel() {
        editValuesPanel.clear();

        WLinks links = (WLinks) value;
        
        addEditLinks(links.getLinks());
    }

    private void addEditLinks(List<LinkInfo> links2) {
        for (Iterator<? extends Object> itr = links2.iterator(); itr.hasNext();) {
            Object value = itr.next();
            LinkInfo li = (LinkInfo) value;
            
            if (!li.isReciprocal() && !li.isAutoDetected()) {
                editValuesPanel.add(createLabel(value));
            }
        }
    }
    
    @Override
    protected Widget getAddWidget() {
        FlowPanel addPanel = new FlowPanel();
        addPanel.setStyleName("renderer-add-panel");
        verifyLabel = new Label();
        addPanel.add(verifyLabel);
        
        Validator validator = new Validator() {
            public String getFailureMessage() {
                return "At least one link must be supplied.";
            }

            public boolean validate(Object value) {
                return getLinks().getLinks().size() > 0;
            }
        };
        
        suggest = new ValidatableSuggestBox(validator, new ItemPathOracle(galaxy, errorPanel, "xxx"));
        addPanel.add(suggest);

        addButton = new Button();
        addButton.setText("Add");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                verifyLabel.setText("");
                suggest.clearError();
                String value = suggest.getText();
                
                verify(value);
            }
        });
        addPanel.add(addButton);
        
        redrawEditPanel();
        
        return addPanel;
    }
    
    protected void verify(final String path) {
        setEnabled(false);
        
        galaxy.getRegistryService().itemExists(path, new AbstractCallback<Boolean>(errorPanel) {

            public void onSuccess(Boolean exists) {
                if (isDuplicate(path)) {
                    verifyLabel.setText("A link to that item already exists.");
                    setEnabled(true);
                } else if (!exists) {
                    verifyLabel.setText("Item does not exist in the registry!");
                    setEnabled(true);
                } else {
                    verified(path);
                }
            }
            
        });
    }
    
    private void setEnabled(boolean e) {
        addButton.setEnabled(e);
    }

    protected boolean isDuplicate(String path) {
        for (LinkInfo l : getLinks().getReciprocal()) {
            if (l.getItemName().equals(path)) {
                return true;
            }
        }
        
        return values.contains(path);
    }

    protected void verified(String path) {
        LinkInfo l = new LinkInfo();
        l.setItemName(path);
        l.setItemType(LinkInfo.TYPE_ENTRY);
        getLinks().getLinks().add(l);
        editValuesPanel.add(createLabel(l));
        setEnabled(true);
    }

    public Widget createViewWidget() {
        InlineFlowPanel container = new InlineFlowPanel();
        InlineFlowPanel linkPanel = new InlineFlowPanel();
        InlineFlowPanel recipPanel = new InlineFlowPanel();
        
        recipPanel.setStyleName("linksPropertyPanel");
        recipPanel.add(new Label("[" + reciprocalName + ": "));

        addViewLinks(values, container, linkPanel, recipPanel);
        
        return container;
    }

    private void addViewLinks(Collection<Object> links, 
                              Panel container, 
                              InlineFlowPanel linkPanel,
                              InlineFlowPanel recipPanel) {
        for (Object o : links) {
            final LinkInfo info = (LinkInfo) o;

            Widget w;
            if (info.getItemType() == LinkInfo.TYPE_NOT_FOUND) {
                w = new Label(info.getItemName());
            } else {
                String prefix;
                if (info.getItemType() == LinkInfo.TYPE_ENTRY) {
                    prefix = "artifact/";
                } else {
                    prefix = "artifact-version/";
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
                if (recipPanel.getWidgetCount() > 1) {
                    recipPanel.add(new Label(", "));
                }
                recipPanel.add(w);
            } else {
                if (linkPanel.getWidgetCount() > 0) {
                    linkPanel.add(new Label(", "));
                }
                linkPanel.add(w);
            }
        }
        
        recipPanel.add(new Label("] "));

        if (recipPanel.getWidgetCount() > 1 && linkPanel.getWidgetCount() > 0) {
            linkPanel.add(new Label(" "));
        }
        container.add(linkPanel);
        
        if (recipPanel.getWidgetCount() > 2) {
            container.add(recipPanel);
        }
    }
    
    @Override
    protected void removeLabel(Object value) {
        getLinks().getLinks().remove(value);
        super.removeLabel(value);
    }

    protected void removeLink(String value) {
        List<LinkInfo> links = getLinks().getLinks();
        for (LinkInfo l : links) {
            if (l.getLinkId().equals(value)) {
                links.remove(l);
                return;
            }
        }
    }

    @Override
    protected String getRenderedText(Object value) {
        if (value instanceof String) {
            return value.toString();
        } else {
            return ((LinkInfo) value).getItemName();
        }
    }

    @Override
    public boolean validate() {
        return suggest.validate();
    }
    
}
