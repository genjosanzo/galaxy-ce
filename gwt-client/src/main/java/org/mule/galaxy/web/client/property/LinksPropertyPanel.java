package org.mule.galaxy.web.client.property;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.util.EntrySuggestOracle;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.LinkInfo;
import org.mule.galaxy.web.rpc.WLinks;

public class LinksPropertyPanel extends ListPropertyPanel {

    protected Collection<LinkInfo> links;
    private SuggestBox suggest;
    private FlowPanel editLinksPanel;
    private Button addButton;
    private String reciprocalName;
    private Label verifyLabel;
    
    public void loadRemote() {
        galaxy.getRegistryService().getLinks(itemId, 
                                             getProperty().getName(), 
                                             new AbstractCallback<WLinks>(errorPanel) {

            public void onSuccess(WLinks o) {
                links = new ArrayList<LinkInfo>();
                links.addAll(o.getLinks());
                links.addAll(o.getReciprocal());
                
                reciprocalName = o.getReciprocalName();
                
                values.clear();
                values.addAll(links);
                onFinishLoad();
            }
        });
    }

    @Override
    protected void removeLabel(Object value) {
        if (value instanceof String) {
            valuesToSave.remove(value);
        } else {
            valuesToDelete.add(((LinkInfo)value).getLinkId());
        }
        super.removeLabel(value);
    }

    @Override
    protected void redrawViewPanel() {
        viewValuesPanel.clear();
        
        createViewWidgets(viewValuesPanel);
    }

    protected void redrawEditPanel() {
        editValuesPanel.clear();

        for (Iterator<? extends Object> itr = values.iterator(); itr.hasNext();) {
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
        
        verifyLabel = new Label();
        addPanel.add(verifyLabel);
        
        editLinksPanel = new FlowPanel();
        addPanel.add(editLinksPanel);
        
        suggest = new SuggestBox(new EntrySuggestOracle(galaxy, errorPanel));
        addPanel.add(suggest);

        addButton = new Button();
        addButton.setText("Add");
        addButton.addClickListener(new ClickListener() {
            public void onClick(Widget arg0) {
                verifyLabel.setText("");
                String value = suggest.getText();
                
                verify(value);
            }
        });
        addPanel.add(addButton);
        
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
    
    protected boolean isDuplicate(String path) {
        for (LinkInfo l : links) {
            if (l.getItemName().equals(path)) {
                return true;
            }
        }
        
        return valuesToSave.contains(path);
    }

    protected void verified(String path) {
        valuesToSave.add(path);
        editValuesPanel.add(createLabel(path));
        setEnabled(true);
    }

    private void createViewWidgets(Panel container) {
        InlineFlowPanel linkPanel = new InlineFlowPanel();
        InlineFlowPanel recipPanel = new InlineFlowPanel();
        
        recipPanel.add(new Label("(" + reciprocalName + ": "));
        
        for (Iterator<LinkInfo> itr = links.iterator(); itr.hasNext();) {
            final LinkInfo info = itr.next();

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
        
        recipPanel.add(new Label(")"));

        if (recipPanel.getWidgetCount() > 1 && linkPanel.getWidgetCount() > 0) {
            linkPanel.add(new Label(" "));
        }
        container.add(linkPanel);
        
        if (recipPanel.getWidgetCount() > 2) {
            container.add(recipPanel);
        }
    }
    
    protected void save() {
        setEnabled(false);
        
        if (valuesToSave.isEmpty()) {
            deleteLinks();
            return;
        }
        
        saveNext();
    }

    @SuppressWarnings("unchecked")
    private void saveNext() {
        final Object value = valuesToSave.iterator().next();
        AbstractCallback addCallback = new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                onSaveFailure(caught, this);
            }

            public void onSuccess(Object response) {
                valuesToSave.remove(value);
                links.add((LinkInfo) response);
                if (valuesToSave.isEmpty()) {
                    deleteLinks();
                } else {
                    saveNext();
                }
            }
        };
        
        galaxy.getRegistryService().addLink(itemId, property.getName(), (String) value, addCallback);
    }
    
    protected void deleteLinks() {
        if (valuesToDelete.isEmpty()) {
            onSave(null, null);
            return;
        }
        
        deleteNextLink();
    }
    
    private void deleteNextLink() {
        final String value = valuesToDelete.iterator().next();
        
        AbstractCallback addCallback = new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                onSaveFailure(caught, this);
            }

            public void onSuccess(Object response) {
                removeLink(value);
                valuesToDelete.remove(value);
                if (valuesToDelete.isEmpty()) {
                    onSave(null, null);
                } else {
                    deleteNextLink();
                }
            }
        };
        
        galaxy.getRegistryService().removeLink(itemId, property.getName(), value, addCallback);
    }
    
    protected void removeLink(String value) {
        for (LinkInfo l : links) {
            if (l.getLinkId().equals(value)) {
                links.remove(l);
                return;
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        addButton.setEnabled(enabled);
    }

    @Override
    protected void onSave(Object value, Object response) {
        values.clear();
        values.addAll(links);

        valuesToDelete.clear();
        valuesToSave.clear();
        
        setEnabled(true);
        
        showView();

        redraw();
        
        suggest.setText("");
        verifyLabel.setText("");
        
        if (saveListener != null) {
            saveListener.onClick(null);
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

    protected void cancel() {
        super.cancel();
        valuesToDelete.clear();
        valuesToSave.clear();
    }

    
}
