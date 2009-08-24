package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.FlowPanel;
import org.mule.galaxy.web.rpc.Plugin;

/**
 * Provides a single element with the id "plugin" which GWT modules
 * can insert themselves at.
 */
public class PluginPanel extends AbstractShowable {

    private FlowPanel insertPoint;
    private final Plugin plugin;

    public PluginPanel(Plugin plugin) {
        super();
        this.plugin = plugin;

        insertPoint = new FlowPanel();
        insertPoint.getElement().setId("plugin");
        initWidget(insertPoint);
    }

    @Override
    public void doShowPage() {
        insertPoint.clear();
        
        showPlugin(plugin.getRootToken());
    }

    public native void showPlugin(String token)
    /*-{
        $wnd.showPlugin(token);
    }-*/;
}
