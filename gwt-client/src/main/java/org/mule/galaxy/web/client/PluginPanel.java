package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.FlowPanel;
import org.mule.galaxy.web.rpc.Plugin;

/**
 * Provides a single element with the id "plugin" which GWT modules
 * can insert themselves at.
 */
public class PluginPanel extends AbstractShowable {

    private final Plugin plugin;
    private FlowPanel main;

    public PluginPanel(Plugin plugin) {
        super();
        this.plugin = plugin;
        main = new FlowPanel();
        
        initWidget(main);
    }

    @Override
    public void doShowPage() {
        FlowPanel insertPoint = new FlowPanel();
        insertPoint.getElement().setId("plugin");
        main.add(insertPoint);
        
        showPlugin(plugin.getRootToken());
    }

    @Override
    public void hidePage() {
        main.clear();
        
        super.hidePage();
    }

    public native void showPlugin(String token)
    /*-{
        $wnd.showPlugin(token);
    }-*/;
}
