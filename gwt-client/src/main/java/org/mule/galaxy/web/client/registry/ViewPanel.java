/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.client.registry;

import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WArtifactView;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;


/**
 * Shows/Edits an ArtifactView.
 */
public class ViewPanel extends AbstractBrowsePanel {

    /**
     * This is the id new views have before being persisted.
     */
    protected static final String NEW_VIEW_ID = "new";

    private String viewId;
    private ViewSearchForm viewForm;
    private WArtifactView view;
    private FlowPanel headerPanel;
    private boolean editMode;
    private Hyperlink editLink;

    // Is this a test search we're executing.
    protected boolean testSearch;

    // do we need to update the artifact list?
    private boolean stale = true;

    public ViewPanel(Galaxy galaxy) {
        super(galaxy);
    }

    protected String getHistoryToken() {
        return "view/" + viewId;
    }

    public void onShow(List<String> params) {
        if (params.size() > 0) {
            String newId = params.get(0);
            if (!newId.equals(viewId)) {
                stale = true;
                if (artifactListPanel != null) {
                    artifactListPanel.clear();
                }
            }
            viewId = newId;
        }

        if (params.size() > 1) {
            editMode = "edit".equals(params.get(1));
        } else {
            editMode = false;
        }

        setTop(null);
        FlowPanel browseToolbar = new FlowPanel();
        browseToolbar.setStyleName("toolbar");

        viewForm = new ViewSearchForm(this, galaxy, "Save");
        viewForm.addSearchListener(new ClickListener() {
            public void onClick(Widget arg0) {
                if (!validate()) {
                    return;
                }
                save();
            }
        });

        if (NEW_VIEW_ID.equals(viewId)) {
            view = new WArtifactView();
            viewForm.setPredicates(view.getPredicates());
            showViewForm(true);
        }

        loadView();

        super.onShow(params);

        if (NEW_VIEW_ID.equals(viewId)) {
            if (artifactListPanel != null) {
                artifactListPanel.clear();
            }
        }
    }

    private void loadView() {
        galaxy.getRegistryService().getArtifactView(viewId, new AbstractCallback(this) {

            public void onSuccess(Object o) {
                view = (WArtifactView) o;
                viewForm.getNameTB().setText(view.getName());
                viewForm.getSharedCB().setChecked(view.isShared());

                headerPanel = new InlineFlowPanel();
                headerPanel.add(createPrimaryTitle(view.getName()));
                headerPanel.add(new Label(" "));

                editLink = new Hyperlink("Edit", "view/" + viewId + "/edit");
                headerPanel.add(editLink);
                viewForm.setPredicates(view.getPredicates());
                viewForm.setWorkspace(view.getWorkspace());
                viewForm.setWorkspaceSearchRecursive(view.isWorkspaceSearchRecursive());

                if (view.getQueryString() != null && !"".equals(view.getQueryString())) {
                    viewForm.setFreeformQuery(view.getQueryString());

                }
                showViewForm(editMode);
            }
        });
    }

    protected void delete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    galaxy.getRegistryService().deleteArtifactView(viewId, new AbstractCallback(ViewPanel.this) {
                        public void onSuccess(Object arg0) {
                            galaxy.setMessageAndGoto("browse", "View was deleted.");
                        }
                    });
                }
            }
        };
        MessageBox.confirm("Confirm", "Are you sure you want to delete this view?", l);
    }

    public void refresh() {
        if (testSearch || (!NEW_VIEW_ID.equals(viewId) && stale)) {
            refreshArtifacts();
        }
    }

    public void onHide() {
    }

    protected void save() {
        stale = true;
        view.setName(viewForm.getNameTB().getText());
        view.setShared(viewForm.getSharedCB().isChecked());
        view.setPredicates(viewForm.getPredicates());
        view.setWorkspace(viewForm.getWorkspacePath());
        view.setWorkspaceSearchRecursive(viewForm.isWorkspaceSearchRecursive());
        view.setQueryString(viewForm.getFreeformQuery());

        galaxy.getRegistryService().saveArtifactView(view, new AbstractCallback(this) {
            public void onSuccess(Object id) {
                view.setId((String) id);
                History.newItem("view/" + id);
            }
        });

        showViewForm(false);
    }

    protected boolean validate() {
        boolean isOk = true;

        isOk &= viewForm.getNameTB().validate();

        return isOk;
    }

    private void showViewForm(boolean showViewForm) {
        currentTopPanel = new FlowPanel();
        if (showViewForm) {
            currentTopPanel.add(viewForm);
        } else {
            currentTopPanel.add(headerPanel);
        }
        setTop(currentTopPanel);
    }

    protected void fetchArtifacts(int resultStart, int maxResults, AbstractCallback callback) {
        if (testSearch) {
            galaxy.getRegistryService().getArtifacts(null,
                    viewForm.getWorkspacePath(),
                    viewForm.isWorkspaceSearchRecursive(),
                    viewForm.getPredicates(),
                    viewForm.getFreeformQuery(),
                    resultStart, maxResults, callback);
            testSearch = false;
        } else {
            if (NEW_VIEW_ID.equals(viewId)) {
                return;
            }
            galaxy.getRegistryService().getArtifactsForView(viewId, resultStart, maxResults, callback);
        }
        stale = false;
    }

    public void setTestSearch(boolean b) {
        this.testSearch = b;
        this.stale = true;
    }

    public String getViewId() {
        return viewId;
    }

    public void setStale(boolean stale) {
        this.stale = stale;
    }
}
