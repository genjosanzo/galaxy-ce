package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.mule.galaxy.web.client.AbstractCallback;
import org.mule.galaxy.web.client.ArtifactGroup;
import org.mule.galaxy.web.client.DependencyInfo;
import org.mule.galaxy.web.client.ExtendedArtifactInfo;
import org.mule.galaxy.web.client.RegistryPanel;

public class ArtifactInfoPanel extends Composite {

    private HorizontalPanel topPanel;
    private RegistryPanel registryPanel;

    public ArtifactInfoPanel(RegistryPanel registryPanel, 
                             ArtifactGroup group,
                             ExtendedArtifactInfo info) {
        this.registryPanel = registryPanel;
        VerticalPanel panel = new VerticalPanel();
        Label label = new Label(info.getValue(0));
        label.setStyleName("right-title");
        panel.add(label);
        
        topPanel = new HorizontalPanel();
        panel.add(topPanel);

        FlexTable table = new FlexTable();
        table.setStyleName("gwt-FlexTable");
        table.setCellSpacing(5);
        table.setCellPadding(0);
        table.setWidth("100%");
        
        for (int i = 0; i < group.getColumns().size(); i++) {
            table.setText(i, 0, (String) group.getColumns().get(i));
        }
        
        for (int c = 0; c < group.getColumns().size(); c++) {
            table.setText(c, 1, info.getValue(c));
        }
        
        topPanel.add(table);
        
        registryPanel.getRegistryService().getDependencyInfo(info.getId(), new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initDependencies((Collection) o);
            }
            
        });
        
        table = new FlexTable();
        table.setStyleName("gwt-FlexTable");
        table.setCellSpacing(5);
        table.setCellPadding(0);
        table.setWidth("100%");
        
        int i = 0;
        for (Iterator itr = info.getProperties().entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry) itr.next();
            
            table.setText(i, 0, (String) e.getKey());
            table.setText(i, 1, (String) e.getValue());
            
            i++;
        }
        
        label = new Label("Metadata");
        label.setStyleName("right-title");

        panel.add(label);
        panel.add(table);
        
        initWidget(panel);
    }
    
    protected void initDependencies(Collection o) {
        VerticalPanel group = new VerticalPanel();
        
        VerticalPanel depPanel = new VerticalPanel();
        
        Label label = new Label("Dependencies");
        label.setStyleName("dependencyPanelHeader");
        depPanel.add(label);
        depPanel.setStyleName("dependencyPanel");
        
        VerticalPanel depOnPanel = new VerticalPanel();
        label = new Label("Depended On By");
        label.setStyleName("dependencyPanelHeader");
        depOnPanel.add(label);
        depOnPanel.setStyleName("dependencyPanel");
        
        boolean addedDeps = false;
        boolean addedDependedOn = false;
        
        for (Iterator itr = o.iterator(); itr.hasNext();) {
            final DependencyInfo info = (DependencyInfo) itr.next();
            
            Hyperlink hl = new Hyperlink(info.getArtifactName(), 
                                         "artifact-" + info.getArtifactId());
            hl.addClickListener(new ClickListener() {

                public void onClick(Widget arg0) {
                    registryPanel.setMain(new ArtifactPanel(registryPanel, 
                                                            info.getArtifactId()));
                }
            });
            
            if (info.isDependsOn()) {
                depPanel.add(hl);
                
                if (!addedDeps) {
                    group.add(depPanel);
                    addedDeps = true;
                }
            } else {
                depOnPanel.add(hl);
                
                if (!addedDependedOn) {
                    group.add(depOnPanel);
                    addedDependedOn = true;
                }
            }
        }
        topPanel.add(group);
    }

}
