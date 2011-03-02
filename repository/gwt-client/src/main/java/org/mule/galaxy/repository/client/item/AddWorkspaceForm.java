package org.mule.galaxy.repository.client.item;

import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;

public class AddWorkspaceForm extends AddNamedItemForm {

    public AddWorkspaceForm(RegistryServiceAsync registryService, ItemInfo parent) {
        super("Add New Workspace", "Workspace", registryService, parent);
    }

}
