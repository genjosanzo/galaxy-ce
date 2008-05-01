package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.MenuPanelPageInfo;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;

public class LifecycleListPanel extends AbstractComposite {
    private AdministrationPanel adminPanel;
    private FlowPanel panel;

    public LifecycleListPanel(AdministrationPanel a) {
        super();

        this.adminPanel = a;

        panel = new FlowPanel();

        initWidget(panel);
    }

    public void onShow() {
        panel.clear();

        final FlexTable table = createTitledRowTable(panel, "Lifecycles");

        table.setText(0, 0, "Lifecycle");
        table.setText(0, 1, "Phases");

        adminPanel.getRegistryService().getLifecycles(new AbstractCallback(adminPanel) {

            public void onSuccess(Object arg0) {
                showLifecycles(table, (Collection)arg0);
            }

        });
    }

    protected void showLifecycles(FlexTable table, Collection lifecycles) {
         int i = 1;
         for (Iterator itr = lifecycles.iterator(); itr.hasNext();) {
             final WLifecycle l = (WLifecycle)itr.next();
             
             String text = l.getName();
             
             if (l.isDefaultLifecycle()) {
                 text += " (Default)";
             }
             
             Hyperlink lifecycleLink = new Hyperlink(text, "lifecycle-" + l.getName());
             MenuPanelPageInfo page = new MenuPanelPageInfo(lifecycleLink, adminPanel) {
                 public AbstractComposite createInstance() {
                     return new LifecycleForm(adminPanel, l, false);
                 }
             };
             adminPanel.addPage(page);
             
             table.setWidget(i, 0, lifecycleLink);
             table.setText(i, 1, getPhaseList(l));
             
             i++;
         }
     }

    private String getPhaseList(WLifecycle l) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (Iterator itr = l.getPhases().iterator(); itr.hasNext();) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(((WPhase)itr.next()).getName());
        }
        return sb.toString();
    }

}
