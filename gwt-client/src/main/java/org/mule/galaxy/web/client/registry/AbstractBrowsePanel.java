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

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.client.util.TooltipListener;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WArtifactType;
import org.mule.galaxy.web.rpc.WSearchResults;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The basis for any form that lists out groups of artifacts.
 */
public abstract class AbstractBrowsePanel extends AbstractErrorShowingComposite {

    protected Set<String> appliedArtifactTypeFilters = new HashSet<String>();
    protected Toolbox artifactTypesBox;
    protected RegistryServiceAsync service;
    protected ArtifactListPanel artifactListPanel;
    protected FlowPanel currentTopPanel;
    protected final Galaxy galaxy;
    protected RegistryMenuPanel menuPanel;
    private boolean first = true;
    private int resultStart;
    private final boolean showArtifactTypes;

    public AbstractBrowsePanel(Galaxy galaxy) {
        this(galaxy, true);
    }

    public AbstractBrowsePanel(Galaxy galaxy, boolean showArtifactTypes) {
        super();
        this.galaxy = galaxy;
        this.showArtifactTypes = showArtifactTypes;
        this.service = galaxy.getRegistryService();

        menuPanel = createRegistryMenuPanel();

        initWidget(menuPanel);
    }


    protected RegistryMenuPanel createRegistryMenuPanel() {
        return new RegistryMenuPanel(galaxy);
    }

    public void onShow(List<String> params) {
        if (params.size() > 1) {
            try {
                resultStart = Integer.valueOf(params.get(1)).intValue();
            } catch (NumberFormatException e) {
            }

            if (resultStart < 0) {
                resultStart = 0;
            }
        } else {
            resultStart = 0;
        }

        if (first) {
            artifactListPanel = new ArtifactListPanel(this, galaxy);
        }

        artifactListPanel.setResultStart(resultStart);

        if (first) {
            initializeMenuAndTop();

            if (showArtifactTypes) {
                artifactTypesBox = new Toolbox(false);
                InlineFlowPanel titlePanel = new InlineFlowPanel();

                Image resetImg = new Image("images/page_refresh.gif");
                resetImg.setStyleName("icon-baseline");
                resetImg.addClickListener(new ClickListener() {
                    public void onClick(final Widget widget) {
                        appliedArtifactTypeFilters.clear();
                        refreshArtifactTypes();
                        refreshArtifacts();
                    }
                });
                // tooltip
                resetImg.addMouseListener(new TooltipListener("Refresh and display all artifact types",
                                                              5000, "tooltip"));

                titlePanel.add(new Label("Display "));
                artifactTypesBox.setTitle(titlePanel);
                titlePanel.add(resetImg);
                showArtifactTypes();
            }
            first = false;
        }

        menuPanel.onShow();
        refresh();

        if (currentTopPanel != null) {
            menuPanel.setTop(currentTopPanel);
        }
    }

    protected abstract String getHistoryToken();

    protected void initializeMenuAndTop() {

    }

    protected void initializeBulkEdit() {

    }

    public void refresh() {
        refreshArtifactTypes();

        menuPanel.loadViews();
    }

    protected void refreshArtifactTypes() {
        artifactTypesBox.clear();
        artifactTypesBox.add(new Label("Loading..."));

        service.getArtifactTypes(new AbstractCallback(this) {

            public void onSuccess(Object o) {
                artifactTypesBox.clear();
                Collection allArtifactTypes = (Collection) o;

                // Get list of all artifact types
                for (Iterator itr = allArtifactTypes.iterator(); itr.hasNext();) {
                    final WArtifactType at = (WArtifactType) itr.next();

                    final Hyperlink hl = new Hyperlink(at.getDescription(), getHistoryToken());
                    hl.addClickListener(new ClickListener() {
                        public void onClick(Widget w) {
                            String style = w.getStyleName();
                            w.removeStyleName("unselected-link");
                            if ("unselected-link".equals(style)) {
                                w.setStyleName("selected-link");
                                addArtifactTypeFilter(at.getId());
                            } else {
                                w.setStyleName("unselected-link");
                                removeArtifactTypeFilter(at.getId());
                            }

                        }
                    });
                    final String currentStyleName = appliedArtifactTypeFilters.contains(at.getId())
                            ? "selected-link"
                            : "unselected-link";

                    hl.setStyleName(currentStyleName);
                    artifactTypesBox.add(hl, false);
                }
            }
        });
    }

    public void addArtifactTypeFilter(String id) {
        appliedArtifactTypeFilters.add(id);
        refreshArtifacts();
    }

    public void removeArtifactTypeFilter(String id) {
        appliedArtifactTypeFilters.remove(id);
        refreshArtifacts();
    }

    public void refreshArtifacts() {
        menuPanel.setMain(artifactListPanel);
        refreshArtifacts(artifactListPanel.getResultStart(),
                         artifactListPanel.getMaxResults());
    }

    public void refreshArtifacts(int resultStart, int maxResults) {
        artifactListPanel.showLoadingMessage();
        AbstractCallback callback = new AbstractCallback(this) {

            public void onSuccess(Object o) {
                artifactListPanel.initArtifacts((WSearchResults) o);
            }

            public void onFailure(Throwable caught) {
                menuPanel.setMessage(caught.getMessage());
                if (artifactListPanel != null) {
                    artifactListPanel.clear();
                }
            }
        };

        fetchArtifacts(resultStart, maxResults, callback);
    }

    @Override
    public void onHide() {
        artifactListPanel.clear();
    }

    public Set<String> getAppliedArtifactTypeFilters() {
        return appliedArtifactTypeFilters;
    }

    public void showArtifactTypes() {
        menuPanel.addMenuItem(artifactTypesBox);
    }

    public void hideArtifactTypes() {
        menuPanel.removeMenuItem(artifactTypesBox);
    }

    // TODO
    protected int getErrorPanelPosition() {
        return 0;
    }

    protected abstract void fetchArtifacts(int resultStart, int maxResults, AbstractCallback callback);
}
