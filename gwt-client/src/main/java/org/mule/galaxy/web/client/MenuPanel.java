package org.mule.galaxy.web.client;

import java.util.List;

import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MenuPanel extends AbstractErrorShowingComposite {

    private DockPanel panel;
    private FlowPanel leftMenuContainer;
    private Widget mainWidget;
    private FlowPanel topPanel;
    private Widget topWidget;
    private FlowPanel leftMenu;
    private FlowPanel centerPanel;
    
    public MenuPanel() {
        super();
        
        panel = new DockPanel();
        panel.setSpacing(0);

        leftMenu = new FlowPanel() {
            protected void onLoad() {

                Element br = DOM.createElement("br");
                DOM.setElementAttribute(br, "class", "clearit");
                DOM.appendChild(DOM.getParent(this.getElement()), br);
            }
        };
        leftMenu.setStyleName("left-menu");
        
        panel.add(leftMenu, DockPanel.WEST);
        
        leftMenuContainer = new FlowPanel(){

            protected void onLoad() {

                Element br = DOM.createElement("br");
                DOM.setElementAttribute(br, "class", "clearit");
                DOM.appendChild(DOM.getParent(this.getElement()), br);
            }
            
        };
        leftMenuContainer.setStyleName("left-menu-container");
        
        leftMenu.add(leftMenuContainer);

        centerPanel = new FlowPanel();
        panel.add(centerPanel, DockPanel.CENTER);
        panel.setCellWidth(centerPanel, "100%");
        
        centerPanel.add(getMainPanel());
        
        topPanel = new FlowPanel();
        topPanel.setStyleName("top-panel");
        
        initWidget(panel);
    }
    
    public void onShow(List params) {
        if (mainWidget instanceof AbstractComposite) {
            ((AbstractComposite) mainWidget).onShow(params);
        }
    }

    public void addMenuItem(Widget widget) {
        leftMenuContainer.add(widget);
    }
    
    public void removeMenuItem(Widget widget) {
        leftMenuContainer.remove(widget);
    }
    
    public void setMain(Widget widget) {
        FlowPanel mainPanel = getMainPanel();
        
        if (mainWidget != null) {
            mainPanel.remove(mainWidget);
        }
        
        clearErrorMessage();
        
        this.mainWidget = widget;
        
        mainPanel.add(widget);
    }    
    
    public void setTop(Widget widget) {
        if (centerPanel.getWidgetIndex(topPanel) == -1) {
            centerPanel.insert(topPanel, 0);
        }
        
        if (topWidget != null)
            topPanel.remove(topWidget);
        
        if (widget instanceof AbstractComposite) {
            ((AbstractComposite)widget).onShow();
        }
        topWidget = widget;
        topPanel.add(widget);
    }
    
    public Widget getMain() {
        return mainWidget;
    };
}
