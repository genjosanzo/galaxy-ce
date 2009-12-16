package org.mule.galaxy.web.client.util;


import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.property.AbstractPropertyRenderer;
import org.mule.galaxy.web.client.property.ArtifactRenderer;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.WType;

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

    private final Galaxy galaxy;
    private String typeId;
    private String versionTypeId;
    private boolean addVersion;
    private ItemInfo item;
    private String itemName;
    private String itemParent;
    private String version;
    private Map properties;
    private Map<String, AbstractPropertyRenderer> renderers = new HashMap<String, AbstractPropertyRenderer>();
    private Map<String, AbstractPropertyRenderer> versionRenderers = new HashMap<String, AbstractPropertyRenderer>();
    private String fileId;
    private AbstractCallback callback;
    protected Map<String, WType> types;


    public AddItemHelper(Galaxy galaxy) {
        this.galaxy = galaxy;
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

        Map<String, Serializable> properties = getProperties(this.getRenderers());

        if (this.isAddVersion()) {
            this.galaxy.getRegistryService().addVersionedItem(parent,
                    name,
                    version,
                    null,
                    this.getTypeId(),
                    getVersionTypeId(),
                    properties,
                    getProperties(this.getVersionRenderers()),
                    callback);
        } else {
            this.galaxy.getRegistryService().addItem(parent,
                    name,
                    null,
                    getTypeId(),
                    properties,
                    callback);
        }
    }


    public Map<String, Serializable> getProperties(Map<String, AbstractPropertyRenderer> typeRenderers) {
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        for (String p : typeRenderers.keySet()) {
            AbstractPropertyRenderer r = typeRenderers.get(p);
            if (r instanceof ArtifactRenderer) {
                properties.put(p, this.getFileId());
            } else {
                properties.put(p, (Serializable) r.getValueToSave());
            }
        }
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
        galaxy.getRegistryService().getItemInfo(itemId, false, new AbstractCallback<ItemInfo>(null) {
            public void onSuccess(ItemInfo item) {
                AddItemHelper.this.setItem(item);
            }
        });
        return this.getItem();
    }


    public Map<String, WType> getTypes() {
        galaxy.getRegistryService().getTypes(new AbstractCallback<List<WType>>(null) {
            public void onSuccess(List<WType> wtypes) {
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

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public Map<String, AbstractPropertyRenderer> getRenderers() {
        return renderers;
    }

    public void setRenderers(Map<String, AbstractPropertyRenderer> renderers) {
        this.renderers = renderers;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Map<String, AbstractPropertyRenderer> getVersionRenderers() {
        return versionRenderers;
    }

    public void setVersionRenderers(Map<String, AbstractPropertyRenderer> versionRenderers) {
        this.versionRenderers = versionRenderers;
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
