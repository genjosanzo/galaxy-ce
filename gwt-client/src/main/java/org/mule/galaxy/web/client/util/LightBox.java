package org.mule.galaxy.web.client.util;

/*
 * Copyright 2006 Robert Hanson <iamroberthanson AT gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.client.GlassPanel;

/**
 * A popup panel that grays out the rest of the page.
 * <p>
 * The image used to gray the page out is "images/lightbox.png"
 * </p>
 * 
 * @author BrianG
 */
public class LightBox implements CloseHandler<PopupPanel> {
    private PopupPanel child;
    private GlassPanel glassPanel;

    public LightBox(PopupPanel child) {
        // Create a glass panel with `autoHide = true`
        glassPanel = new GlassPanel(true);

        this.child = child;
        this.child.addCloseHandler(this);
    }

    public void onClose(CloseEvent<PopupPanel> e) {
        this.hide();
    }

    public void show() {
        // Attach (display) the glass panel
        RootPanel.get().add(glassPanel, 0, 0);
        
        hideSelects();

        child.show();
        child.center();
    }

    public void hide() {
        glassPanel.removeFromParent();
        
        showSelects();
        child.hide();
    }

    private native void hideSelects() /*-{
           var selects = $doc.getElementsByTagName("select");
           for (i = 0; i != selects.length; i++) {
               selects[i].style.visibility = "hidden";
           }
       }-*/;

    private native void showSelects() /*-{
           var selects = $doc.getElementsByTagName("select");
           for (i = 0; i != selects.length; i++) {
               selects[i].style.visibility = "visible";
           }
       }-*/;
}
