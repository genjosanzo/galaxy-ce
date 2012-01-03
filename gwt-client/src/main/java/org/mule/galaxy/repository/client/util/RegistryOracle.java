package org.mule.galaxy.repository.client.util;

import java.util.ArrayList;
import java.util.Collection;

import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.client.ui.field.Oracle;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RegistryOracle extends Oracle {

    private final String type;

    public RegistryOracle(RegistryServiceAsync svc) {
        this(svc, null);
    }

    /**
     * Allows you to specify what types of things this oracle will show. If you
     * wish to show "Workspace"s, then it will do some magic to format things differently.
     *
     * @param svc
     * @param type
     */
    public RegistryOracle(RegistryServiceAsync svc, String type) {
        this.type = type;
        ComboBox<ModelData> combo = new ComboBox<ModelData>();

        boolean workspace = "Workspace".equals(type);
        String template = workspace || type == null ? getWorkspaceTemplate() : getTemplate();

        initialize(getProxy(svc, combo, type), template, combo, "Start typing...");

        if (workspace) {
            combo.setDisplayField("fullPath");
        }
    }

    public RegistryOracle(RegistryServiceAsync svc, String type, String suggestText) {
        this(svc, type, suggestText, "xxx");
    }

    public RegistryOracle(RegistryServiceAsync svc, String type, String suggestText, String excludePath) {

        this.type = type;
        ComboBox<ModelData> combo = new ComboBox<ModelData>();

        boolean workspace = "Workspace".equals(type);
        String template = workspace ? getWorkspaceTemplate() : getTemplate();

        initialize(getProxy(svc, combo, type, excludePath), template, combo, suggestText);

        if (workspace) {
            combo.setDisplayField("fullPath");
        }
    }

    private DataProxy getProxy(final RegistryServiceAsync svc,
                               final ComboBox<ModelData> combo,
                               final String searchType) {

        return getProxy(svc, combo, searchType, "xxx");
    }

    private DataProxy getProxy(final RegistryServiceAsync svc,
                               final ComboBox<ModelData> combo,
                               final String searchType,
                               final String excludePath) {
        RpcProxy<PagingLoadResult<ModelData>> proxy = new RpcProxy<PagingLoadResult<ModelData>>() {
            @Override
            protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<ModelData>> callback) {

                AsyncCallback<Collection<ItemInfo>> wrapper = new AsyncCallback<Collection<ItemInfo>>() {

                    public void onFailure(Throwable arg0) {
                        callback.onFailure(arg0);
                    }

                    public void onSuccess(Collection<ItemInfo> items) {
                        ArrayList<ModelData> models = new ArrayList<ModelData>();
                        for (ItemInfo i : items) {
                            BaseModelData data = new BaseModelData();
                            data.set("name", i.getName());
                            data.set("path", i.getParentPath());
                            data.set("fullPath", i.getParentPath() != null ?
                                    i.getParentPath() + "/" + i.getName() : "/" + i.getName());
                            data.set("item", i);
                            models.add(data);
                        }
                        
                        if (type == null || "Workspace".equals(type))
                        {
                            BaseModelData data = new BaseModelData();
                            data.set("name", "");
                            data.set("path", "");
                            data.set("fullPath", "/");
                            models.add(data);
                        }
                        PagingLoadResult<ModelData> result = new BasePagingLoadResult<ModelData>(models);
                        callback.onSuccess(result);
                    }

                };

                String text = combo.getRawValue();
                if (text == null)
                    text = "";

                svc.suggestItems(text, false, excludePath, new String[]{searchType}, wrapper);
            }
        };

        return proxy;
    }

    private static native String getTemplate() /*-{
        return [
        '<tpl for="."><div style="padding: 3px; vertical-align: middle;" class="search-item">',
        '<strong>{name}</strong> in {path}',
        '</div></tpl>'
        ].join("");
    }-*/;

    private static native String getWorkspaceTemplate() /*-{
        return [
        '<tpl for="."><div style="padding: 3px; vertical-align: middle;" class="search-item">',
        '<strong>{fullPath}</strong>',
        '</div></tpl>'
        ].join("");
    }-*/;
}
