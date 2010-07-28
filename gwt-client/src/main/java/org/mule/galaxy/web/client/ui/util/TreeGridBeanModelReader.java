package org.mule.galaxy.web.client.ui.util;

import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;

import java.util.List;

public class TreeGridBeanModelReader implements DataReader<List<ModelData>> {

    private BeanModelReader reader = new BeanModelReader();

    public boolean isFactoryForEachBean() {
        return reader.isFactoryForEachBean();
    }

    public List<ModelData> read(Object loadConfig, Object data) {
        ListLoadResult<ModelData> models = reader.read(loadConfig, data);
        return models.getData();
    }

    public void setFactoryForEachBean(boolean factoryForEachBean) {
        reader.setFactoryForEachBean(factoryForEachBean);
    }
}