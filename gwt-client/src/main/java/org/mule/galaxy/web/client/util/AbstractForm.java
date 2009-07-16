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

package org.mule.galaxy.web.client.util;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemExistsException;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.List;

public abstract class AbstractForm extends AbstractComposite {

    protected FlowPanel panel;
    protected boolean newItem;
    private Button save;
    private Button delete;
    private Button cancel;
    private String successToken;
    private final String successMessage;
    protected final ErrorPanel errorPanel;
    private final String deleteMessage;
    private final String CANCEL_MESSAGE = "Action Canceled";
    private String existsMessage;

    public AbstractForm(ErrorPanel errorPanel, String successToken,
                        String successMessage, String deleteMessage, String existsMessage) {
        super();
        this.errorPanel = errorPanel;
        this.successToken = successToken;
        this.successMessage = successMessage;
        this.deleteMessage = deleteMessage;
        this.existsMessage = existsMessage;

        panel = new FlowPanel();

        initWidget(panel);
    }

    @Override
    public void show(List<String> params) {
        super.show();

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
        panel.clear();
        panel.add(createPrimaryTitle(getTitle()));

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

        save = new Button("Save", listener);
        delete = new Button("Delete", listener);
        cancel = new Button("Cancel", listener);

        FlexTable table = createFormTable();
        addFields(table);

        panel.add(table);

        ButtonBar bb = new ButtonBar();
        bb.add(save);

        if (newItem) {
            bb.add(cancel);
        } else {
            bb.add(delete);
            bb.add(cancel);
        }
        panel.add(bb);
    }

    protected abstract void fetchItem(String id);

    protected abstract void initializeItem(Object o);

    protected abstract void initializeNewItem();

    protected AsyncCallback getFetchCallback() {
        return new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object o) {
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
        save.setText("Deleting...");
    }

    protected void setEnabled(boolean enabled) {
        save.setEnabled(enabled);
        delete.setEnabled(enabled);
        cancel.setEnabled(enabled);

        if (enabled) {
            save.setText("Save");
            delete.setText("Delete");
            cancel.setText("Cancel");
        }
    }

    protected void save() {
        setEnabled(false);
        save.setText("Saving...");
    }


    /* Use the successToken page as the cancel redirect page */
    protected void cancel() {
        setEnabled(false);
        cancel.setText("Canceling...");
        ((AdministrationPanel) errorPanel).getGalaxy().setMessageAndGoto(successToken,
                CANCEL_MESSAGE);
    }


    public abstract String getTitle();

    protected AsyncCallback getSaveCallback() {
        return new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                onSaveFailure(this, caught);
            }

            public void onSuccess(Object arg0) {
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
            callback.onFailureDirect(caught);
        }
    }

    protected AsyncCallback getDeleteCallback() {
        return new AbstractCallback(errorPanel) {

            public void onFailure(Throwable caught) {
                setEnabled(true);
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
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


}
