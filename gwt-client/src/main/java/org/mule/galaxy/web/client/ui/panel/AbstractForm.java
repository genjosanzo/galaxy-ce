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

package org.mule.galaxy.web.client.ui.panel;

import static org.mule.galaxy.web.client.ClientId.GRAL_FORM_CANCEL_ID;
import static org.mule.galaxy.web.client.ClientId.GRAL_FORM_DELETE_ID;
import static org.mule.galaxy.web.client.ClientId.GRAL_FORM_SAVE_ID;

import java.util.List;

import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.ui.help.PanelMessages;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemExistsException;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

public abstract class AbstractForm extends AbstractShowable {

    protected FlowPanel panel;
    protected boolean newItem;
    private Button save;
    private Button delete;
    private Button cancel;
    private String successToken;
    private final String successMessage;
    protected final ErrorPanel errorPanel;
    private final String deleteMessage;
    private static final PanelMessages panelMessages = (PanelMessages) GWT.create(PanelMessages.class);
    private final String CANCEL_MESSAGE = panelMessages.actionCanceled();
    private String existsMessage;
    private InlineHelpPanel helpPanel;

    public AbstractForm(ErrorPanel errorPanel, String successToken,
                        String successMessage, String deleteMessage, String existsMessage) {
        this.errorPanel = errorPanel;
        this.successToken = successToken;
        this.successMessage = successMessage;
        this.deleteMessage = deleteMessage;
        this.existsMessage = existsMessage;

        panel = new FlowPanel();

        initWidget(panel);
    }

    @Override
    public void showPage(List<String> params) {
        super.showPage(params);

        if (params.size() > 0) {
            String param = params.get(0);
            if ("new".equals(param)) {
                newItem = true;
                initializeNewItem();
                onShowPostInitialize();
            } else {
                newItem = false;
                fetchItem(param);
            }
        } else {
            newItem = true;
            initializeNewItem();
            onShowPostInitialize();
        }
    }

    protected void onShowPostInitialize() {
        ContentPanel cp = new ContentPanel(new FormLayout());
        cp.setBodyBorder(false);
        cp.setAutoWidth(true);
        cp.addStyleName("x-panel-container-full");
        cp.setHeading(getTitle());

        // add optional inline help widget
        if (helpPanel != null) {
            cp.setTopComponent(helpPanel);
        }
        panel.clear();
        SelectionListener listener = new SelectionListener<ComponentEvent>() {
            public void componentSelected(ComponentEvent ce) {
                Button btn = (Button) ce.getComponent();

                if (btn == save) {
                    save();
                } else if (btn == delete) {
                    delete();
                } else if (btn == cancel) {
                    cancel();
                }
            }
        };

        save = new Button(panelMessages.save(), listener);
        save.setId(GRAL_FORM_SAVE_ID);
        delete = new Button(panelMessages.delete(), listener);
        delete.setId(GRAL_FORM_DELETE_ID);
        cancel = new Button(panelMessages.cancel(), listener);
        cancel.setId(GRAL_FORM_CANCEL_ID);

        FlexTable table = createFormTable();
        addFields(table);

        cp.add(table);

        ButtonBar bb = new ButtonBar();
        // TODO: use FormLayout to correctly position button bar.
        bb.setStyleAttribute("paddingLeft", "120");
        bb.add(save);

        if (newItem) {
            bb.add(cancel);
        } else {
            bb.add(delete);
            bb.add(cancel);
        }

        cp.add(bb);
        panel.add(cp);
    }

    protected abstract void fetchItem(String id);

    protected abstract void initializeItem(Object o);

    protected abstract void initializeNewItem();

    protected AsyncCallback getFetchCallback() {
        return new AbstractCallback(errorPanel) {

            public void onCallFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onCallSuccess(Object o) {
                initializeItem(o);
                onShowPostInitialize();
            }

        };
    }

    protected FlexTable createFormTable() {
        return createColumnTable();
    }

    protected abstract void addFields(FlexTable table);

    protected void delete() {
        setEnabled(false);
        save.setText(panelMessages.deleting());
    }

    protected void setEnabled(boolean enabled) {
        save.setEnabled(enabled);
        delete.setEnabled(enabled);
        cancel.setEnabled(enabled);

        if (enabled) {
            save.setText(panelMessages.save());
            delete.setText(panelMessages.delete());
            cancel.setText(panelMessages.cancel());
        }
    }

    protected void save() {
        setEnabled(false);
        save.setText(panelMessages.saving());
    }

    /* Use the successToken page as the cancel redirect page */
    protected void cancel() {
        setEnabled(false);
        cancel.setText(panelMessages.canceling());
        /*((AdministrationPanel) errorPanel).getGalaxy().setMessageAndGoto(successToken,
                CANCEL_MESSAGE);
                */
        ((AdministrationPanel) errorPanel).getGalaxy().setInfoMessageAndGoto(successToken, CANCEL_MESSAGE);
    }

    public abstract String getTitle();

    protected AsyncCallback getSaveCallback() {
        return new AbstractCallback(errorPanel) {

            public void onCallFailure(Throwable caught) {
                onSaveFailure(this, caught);
            }

            public void onCallSuccess(Object arg0) {
                setEnabled(false);
                ((AdministrationPanel) errorPanel).getGalaxy().setMessageAndGoto(successToken,
                        successMessage);
            }
        };
    }

    protected void onSaveFailure(AbstractCallback callback, Throwable caught) {
        setEnabled(true);

        if (caught instanceof ItemExistsException) {
            errorPanel.setMessage(existsMessage);
        } else {
            callback.onFailure(caught);
        }
    }

    protected AsyncCallback getDeleteCallback() {
        return new AbstractCallback(errorPanel) {

            public void onCallFailure(Throwable caught) {
                setEnabled(true);
                super.onFailure(caught);
            }

            public void onCallSuccess(Object arg0) {
                setEnabled(false);
                ((AdministrationPanel) errorPanel).getGalaxy().setMessageAndGoto(successToken,
                        deleteMessage);
            }
        };
    }

    public ErrorPanel getErrorPanel() {
        return errorPanel;
    }

    public Button getSave() {
        return save;
    }

    public void setSave(Button save) {
        this.save = save;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }

    public Button getCancel() {
        return cancel;
    }

    public void setCancel(Button cancel) {
        this.cancel = cancel;
    }


    public InlineHelpPanel getHelpPanel() {
        return helpPanel;
    }

    public void setHelpPanel(InlineHelpPanel helpPanel) {
        this.helpPanel = helpPanel;
    }
}
