package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

import org.gwtwidgets.client.ui.LightBox;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WIndex;

public class IndexDeleteForm extends AbstractAdministrationComposite {

    private RadioButton yesButton;
    private RadioButton noButton;

    public IndexDeleteForm(AdministrationPanel a) {
        super(a);
    }

    public void onShow(List params) {
        String indexId = (String) params.get(0);
        
        adminPanel.getRegistryService().getIndex(indexId, new AbstractCallback(adminPanel) {

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
        deleteButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                delete(index);
            }
            
        });
        vertical.add(deleteButton);
        
        panel.add(vertical);
    }

    protected void delete(final WIndex index) {
        adminPanel.clearErrorMessage();
        
        if (!yesButton.isChecked() && !noButton.isChecked()) {
            adminPanel.setMessage("You must select either yes or no.");
            return;
        }
        
        final ConfirmDialog dialog = new ConfirmDialog(new ConfirmDialogAdapter() {
            public void onConfirm() {
                adminPanel.getRegistryService().deleteIndex(index.getId(), 
                                                            yesButton.isChecked(),
                                                            getDeleteCallback());
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
