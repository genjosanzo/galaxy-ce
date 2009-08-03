/*
 * Copyright 2007 Manuel Carrasco Moñino. (manuel_carrasco at users.sourceforge.net) 
 * http://code.google.com/p/gwtchismes
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Manuel Carrasco Moñino
 * <h3>Class description</h3>
 * <p>
 * Utility class for the gwtchismes library
 * </p> 
 */
public class GWTCHelper {

    /**
     * Detection of Internet Explorer 6.x 
     * @return true if the browser is ie6
     */
    public static native boolean isIE6() /*-{
      return (window.XMLHttpRequest)? false: true;
    }-*/;
    
    /**
     * This method move the panel near the widget provided.
     * If widget is null, the panel is centered into the visible area of the browser.
     *  
     * Hack: PopupPanel.center() does not work because it centers the panel in the  window.
     * 
     * @param panel
     * @param widget
     */
    public static void positionPopupPanel(PopupPanel panel, Widget widget) {
        if (panel==null) return;

        int visibleW = getVisibleWidth();
        int visibleH = getVisibleHeight();
        int windowW = Window.getClientWidth();
        int windowH = Window.getClientHeight();
        int scrollLeft = Window.getScrollLeft();
        int scrollTop = Window.getScrollTop();
        int objectW = panel.getOffsetWidth();
        int objectH = panel.getOffsetHeight();
        
        if (widget != null ) {
            // Put the panel near the widget
            int left = widget.getAbsoluteLeft() - 20;
            int top = widget.getAbsoluteTop() + 10;
            panel.setPopupPosition(left, top);
            // If part of the panel is not visible, move the scrollbars
            int xDiff =  objectW + left - visibleW - scrollLeft;
            int yDiff =  objectH + top - visibleH - scrollTop;
            if (xDiff < 0 ) {
                xDiff = left - scrollLeft;
                if (xDiff > 0) xDiff = 0;
            }
            if (yDiff < 0 ) {
                yDiff = top - scrollTop;
                if (yDiff > 0) yDiff = 0;
            }
            
            // scrollTo(scrollLeft + xDiff, scrollTop + yDiff);
        } else {
            // Center the panel into the visible part of the document
            if (visibleW == 0 || visibleH == 0) {
                //Window.alert("center 1");
                panel.center();
            } else if ( visibleH > windowH){
                //Window.alert("center 2");
                panel.center();
            } else {
                //Window.alert("center 3");
                int left = scrollLeft + ((visibleW + objectW) / 2) - objectW;
                int top = scrollTop + ((visibleH + objectH) / 2) - objectH;
                panel.setPopupPosition(left, top);
            }
        }
    }
    public static void centerPopupPanel(PopupPanel panel) {
        positionPopupPanel(panel, null);
    }
    
    public static void maximizeWidget(Widget widget) {
        if (widget==null) return;
        int w = Math.max(getVisibleWidth(), Window.getClientWidth());
        int h = Math.max(getVisibleHeight(), Window.getClientHeight());
        widget.setSize(w + "px", h + "px");
    }
    public static native void scrollTo(int x, int y) /*-{
       $wnd.scrollTo(x,y);
    }-*/;
    public static native int getVisibleWidth() /*-{
       return $wnd.document.documentElement.clientWidth;
    }-*/;
    public static native int getVisibleHeight() /*-{
       return $wnd.document.documentElement.clientHeight;
    }-*/;

    public static String internationalize(String s, Object[] os) {
        for (int i = 0; i < os.length; i++) {
            String o = "" + (os[i] != null ? os[i] : "");
            String c = "{" + i + "}";
            for (;;) {
                int pos = s.indexOf(c);
                if (pos < 0)
                    break;
                String trail = "";
                if (pos + c.length() < s.length())
                    trail = s.substring(pos + c.length());
                s = s.substring(0, pos) + o + trail;
            }
        }
        return s;
    }

    public static String internationalize(String s, String o) {
        Object[] os = { o };
        return internationalize(s, os);
    }
}
