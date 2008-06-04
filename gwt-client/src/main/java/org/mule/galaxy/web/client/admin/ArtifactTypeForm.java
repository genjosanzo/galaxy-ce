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

package org.mule.galaxy.web.client.admin;

import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.util.QNameListBox;
import org.mule.galaxy.web.client.util.StringListBox;
import org.mule.galaxy.web.rpc.WArtifactType;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

import org.gwtwidgets.client.ui.LightBox;

public class ArtifactTypeForm extends AbstractAdministrationForm {

    private WArtifactType artifactType;
    private TextBox descriptionTB;
    private QNameListBox docTypesLB;
    private TextBox mediaTypeTB;
    private StringListBox extsLB;
    
    public ArtifactTypeForm(AdministrationPanel adminPanel){
        super(adminPanel, 
              "artifact-types", 
              "Artifact type was saved.", 
              "Artifact type was deleted.", null);
    }

    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Description:");
        table.setText(1, 0, "Media Type:");
        table.setText(2, 0, "Document Types:");
        table.setText(3, 0, "File Extensions:");
        
        descriptionTB = new TextBox();
        descriptionTB.setText(artifactType.getDescription());
        table.setWidget(0, 1, descriptionTB);
        
        mediaTypeTB = new TextBox();
        mediaTypeTB.setText(artifactType.getMediaType());
        table.setWidget(1, 1, mediaTypeTB);
        
        docTypesLB = new QNameListBox(artifactType.getDocumentTypes());
        table.setWidget(2, 1, docTypesLB);

        extsLB = new StringListBox(artifactType.getFileExtensions());
        table.setWidget(3, 1, extsLB);
        
        styleHeaderColumn(table);
    }

    public String getTitle() {
        if (newItem) {
            return "Add Artifact Type";
        } else {
            return "Edit Artifact Type" ;
        }
    }

    protected void fetchItem(String id) {
        adminPanel.getRegistryService().getArtifactType(id, getFetchCallback());
    }

    protected void initializeItem(Object o) {
        this.artifactType = (WArtifactType) o;
    }

    protected void initializeNewItem() {
        this.artifactType = new WArtifactType();
    }

    protected void save() {
        super.save();

        artifactType.setMediaType(mediaTypeTB.getText());
        artifactType.setDocumentTypes(docTypesLB.getItems());
        artifactType.setDescription(descriptionTB.getText());
        artifactType.setFileExtensions(extsLB.getItems());
        
        adminPanel.getRegistryService().saveArtifactType(artifactType, getSaveCallback());
    }

    protected void delete() {
        final ConfirmDialog dialog = new ConfirmDialog(new ConfirmDialogAdapter() {
            public void onConfirm() {
                ArtifactTypeForm.super.delete();
                adminPanel.getRegistryService().deleteArtifactType(artifactType.getId(), getDeleteCallback());
            }
        }, "Are you sure you want to delete artifact type " + artifactType.getDescription() + "?");
        new LightBox(dialog).show();
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        docTypesLB.setEnabled(enabled);
    }


}
