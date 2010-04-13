package org.mule.galaxy.repository.client.admin;

import java.util.List;

import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WIndex;
import org.mule.galaxy.web.client.admin.AbstractAdministrationComposite;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.ui.dialog.ConfirmDialog;
import org.mule.galaxy.web.client.ui.dialog.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.ui.dialog.LightBox;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class IndexDeleteForm extends AbstractAdministrationComposite {

    private RadioButton yesButton;
    private RadioButton noButton;
    private final RegistryServiceAsync registryService;

    public IndexDeleteForm(AdministrationPanel a, RegistryServiceAsync registryService) {
        super(a);
        this.registryService = registryService;
    }

    @Override
    public void showPage(List params) {
        String indexId = (String) params.get(0);
        
        registryService.getIndex(indexId, new AbstractCallback(adminPanel) {

            public void onSuccess(Object index) {
                showIndex((WIndex) index);
            }
            
        });
    }

    protected void showIndex(final WIndex index) {
        panel.clear();
        panel.add(createPrimaryTitle("Delete Index '" + index.getDescription() + "'"));
        
        VerticalPanel vertical = new VerticalPanel();
        vertical.setStyleName("deleteIndexPanel");
        
        vertical.add(new Label("Would you like to delete the properties on artifacts that are "
                               + "associated with this index?"));
           
        yesButton = new RadioButton("deleteIndex", "Yes");
        vertical.add(yesButton);
        
        noButton = new RadioButton("deleteIndex", "No");
        vertical.add(noButton);
        
        Button deleteButton = new Button("Delete");
        deleteButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent w) {
                delete(index);
            }
            
        });
        vertical.add(deleteButton);
        
        panel.add(vertical);
    }

    protected void delete(final WIndex index) {
        adminPanel.clearErrorMessage();
        
        if (!yesButton.getValue() && !noButton.getValue()) {
            adminPanel.setMessage("You must select either yes or no.");
            return;
        }
        
        final ConfirmDialog dialog = new ConfirmDialog(new ConfirmDialogAdapter() {
            public void onConfirm() {
                registryService.deleteIndex(index.getId(), yesButton.getValue(), getDeleteCallback());
            }
        }, "Are you sure you want to delete Index " + index.getDescription() + "?");
        new LightBox(dialog).show();
    }


    protected AsyncCallback getDeleteCallback() {
        return new AbstractCallback(adminPanel) {

            public void onSuccess(Object arg0) {
                adminPanel.getGalaxy().setMessageAndGoto("indexes", "Index was deleted.");
            }
            
        };
    }

}
