package org.mule.galaxy.impl.plugin;

import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.plugin.PluginInfo;

public class PluginDaoImpl extends AbstractReflectionDao<PluginInfo>  {
    public PluginDaoImpl() throws Exception {
        super(PluginInfo.class, "plugins", true);
    }
}
