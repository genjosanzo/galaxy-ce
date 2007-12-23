package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractCallback;
import org.mule.galaxy.web.client.ArtifactGroup;
import org.mule.galaxy.web.client.BasicArtifactInfo;
import org.mule.galaxy.web.client.DependencyInfo;
import org.mule.galaxy.web.client.RegistryPanel;

public class ArtifactInfoPanel extends Composite {

    private HorizontalPanel topPanel;

    public ArtifactInfoPanel(RegistryPanel registryPanel, 
                             ArtifactGroup group,
                             BasicArtifactInfo info) {
        VerticalPanel panel = new VerticalPanel();
        
        topPanel = new HorizontalPanel();
        
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
        
        panel.add(topPanel);
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
            DependencyInfo info = (DependencyInfo) itr.next();
            
            if (info.isDependsOn()) {
                depPanel.add(new Label(info.getArtifactName()));
                
                if (!addedDeps) {
                    group.add(depPanel);
                    addedDeps = true;
                }
            } else {
                depOnPanel.add(new Label(info.getArtifactName()));
                
                if (!addedDependedOn) {
                    group.add(depOnPanel);
                    addedDependedOn = true;
                }
            }
        }
        topPanel.add(group);
    }

}
