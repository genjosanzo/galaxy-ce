package org.mule.galaxy.repository.client.util;


import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WType;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.google.gwt.user.client.ui.FormPanel;

/**
 * encapsulate the addItem logic in this class and
 * leave the rendering in the panels where they belong...
 * <p/>
 * helper.setType(AddItemHelper.Type.ARTIFACT);
 * helper.setAddVersion(true); //  this would tell it to submit the artifact version too
 * helper.setName("hello.war");
 * helper.setVersion("1.0");
 * helper.setProperties(mapOfProperties);
 */
public class AddItemHelper extends FormPanel {

    public static enum ItemType {
        ARTIFACT, ARTIFACT_VERSION, VERSION, VERSION_TYPE
    }

    private String typeId;
    private String versionTypeId;
    private boolean addVersion;
    private ItemInfo item;
    private String itemName;
    private String itemParent;
    private String version;
    private String fileId;
    private AbstractCallback callback;
    protected Map<String, WType> types;
    private final RegistryServiceAsync registryService;


    public AddItemHelper(RegistryServiceAsync registryService) {
        this.registryService = registryService;
        this.setEncoding(FormPanel.ENCODING_MULTIPART);
        this.setMethod(FormPanel.METHOD_POST);
    }

    public void doSubmit() {
        doSubmit(this.getCallback(), this.getItemName(), this.getItemParent(), this.getVersion());
    }

    public void doSubmit(AbstractCallback callback, String itemName, String itemParent, String version) {
        if (this.isAddVersion() || this.getFileId() != null) {
            this.submit();
        } else {
            this.addItem(callback, itemName, itemParent, version);
        }
    }

    public void addItem() {
        addItem(this.getCallback(), this.getItemName(), this.getItemParent(), this.getVersion());
    }

    public void addItem(AbstractCallback callback, String name, String parent, String version) {

        Map<String, Serializable> properties = getProperties();

        if (this.isAddVersion()) {
            registryService.addVersionedItem(parent,
                    name,
                    version,
                    null,
                    this.getTypeId(),
                    getVersionTypeId(),
                    properties,
                    new HashMap<String,Serializable>(),
                    callback);
        } else {
            registryService.addItem(parent,
                    name,
                    null,
                    getTypeId(),
                    properties,
                    callback);
        }
    }


    public Map<String, Serializable> getProperties() {
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("artifact", this.getFileId());
        return properties;
    }

    public boolean isVersioned(WType selectedType) {
        WType versionedType = getTypeByName("Versioned");

        if (versionedType == null) {
            return false;
        }

        if (selectedType.getId().equals(versionedType.getId())) {
            return true;
        }
        return selectedType.inherits(versionedType, this.getTypes());
    }


    public WType getTypeByName(String name) {
        WType artifact = null;
        for (WType type : this.getTypes().values()) {
            if (name.equals(type.getName())) {
                artifact = type;
            }
        }
        return artifact;
    }

    public ItemInfo fetchAndSetItem(String itemId) {
        registryService.getItemInfo(itemId, false, new AbstractCallback<ItemInfo>(null) {
            public void onCallSuccess(ItemInfo item) {
                AddItemHelper.this.setItem(item);
            }
        });
        return this.getItem();
    }


    public Map<String, WType> getTypes() {
        registryService.getTypes(new AbstractCallback<List<WType>>(null) {
            public void onCallSuccess(List<WType> wtypes) {
                Collections.sort(wtypes, new WTypeComparator());
                AddItemHelper.this.types = new HashMap<String, WType>();
                for (WType type : wtypes) {
                    types.put(type.getId(), type);
                }
            }
        });
        return types;
    }

    public void setTypes(Map<String, WType> types) {
        this.types = types;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public boolean isAddVersion() {
        return addVersion;
    }

    public void setAddVersion(boolean addVersion) {
        this.addVersion = addVersion;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getVersionTypeId() {
        return versionTypeId;
    }

    public void setVersionTypeId(String versionTypeId) {
        this.versionTypeId = versionTypeId;
    }

    public AbstractCallback getCallback() {
        return callback;
    }

    public void setCallback(AbstractCallback callback) {
        this.callback = callback;
    }

    public String getItemParent() {
        return itemParent;
    }

    public void setItemParent(String itemParent) {
        this.itemParent = itemParent;
    }

    public ItemInfo getItem() {
        return item;
    }

    public void setItem(ItemInfo item) {
        this.item = item;
    }


}
