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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * A popup panel that grays out the rest of the page.
 * <p>The image used to gray the page out is "images/lightbox.png"</p>
 * @author BrianG
 */
public class LightBox implements PopupListener
{
    private PNGImage png;
    private PopupPanel child;
    private PopupPanel background;
    private WindowResizeListener windowResizeListener;


    public LightBox (PopupPanel child)
    {
        background = new PopupPanel();
        
        windowResizeListener = new WindowResizeListener()
        {
            public void onWindowResized (int width, int height)
            {
                background.setWidth(Integer.toString(width));
                background.setHeight(Integer.toString(height));
                png.setPixelSize(width, height);
                background.setPopupPosition(0, 0);
            }
        };
        Window.addWindowResizeListener(windowResizeListener);

        this.child = child;
        this.child.addPopupListener(this);
    }


    private native void backgroundFixup (Element e)
    /*-{
        // fixes issue with GWT 1.1.10 by hiding the iframe
        if (e.__frame) {
            e.__frame.style.visibility = 'hidden';
        }
    }-*/;


    public void onPopupClosed (PopupPanel sender, boolean autoClosed)
    {
        if (png != null) {
            this.hide();
        }
    }
        

        public void show ()
    {
                int w = getWidth();
                int h = getHeight();
                
                background.setWidth(Integer.toString(w));
                background.setHeight(Integer.toString(h));
                background.setWidget(png = new PNGImage("images/lightbox.png", w, h));
                background.setPopupPosition(0, 0);
                hideSelects();
                
        background.show();
        backgroundFixup(background.getElement());

        child.show();
                center();
        }

    
        private void center ()
    {
                //TODO figure out how to center inner popup
                // http://groups.google.com/group/Google-Web-Toolkit/browse_frm/thread/83a4400f9eb93621/69d05316c5891057?lnk=gst&q=offsetwidth&rnum=1#69d05316c5891057
                int left = (getWidth() - child.getOffsetWidth()) / 2;
                int top = (getHeight() - child.getOffsetHeight()) / 2;
                child.setPopupPosition(left, top);
        }

    
        public void hide ()
    {
        png.removeFromParent();
        png = null;
        showSelects();
        child.hide();
        background.hide();
        Window.removeWindowResizeListener(windowResizeListener);
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
        

        private native int getHeight() /*-{
        var yScroll;
        
        if ($wnd.innerHeight && $wnd.scrollMaxY) {      
            yScroll = $wnd.innerHeight + $wnd.scrollMaxY;
        }
        else if ($doc.body.scrollHeight > $doc.body.offsetHeight){ // all but Explorer Mac
            yScroll = $doc.body.scrollHeight;
        }
        else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
        yScroll = $doc.body.offsetHeight;
        }
        
        var windowHeight;
        if (self.innerHeight) { // all except Explorer
            windowHeight = self.innerHeight;
        }
        else if ($doc.documentElement && $doc.documentElement.clientHeight) { // Explorer 6 Strict Mode
            windowHeight = $doc.documentElement.clientHeight;
        }
        else if ($doc.body) { // other Explorers
            windowHeight = $doc.body.clientHeight;
        }       
        
        // for small pages with total height less then height of the viewport
        if(yScroll < windowHeight){
            pageHeight = windowHeight;
        }
        else { 
            pageHeight = yScroll;
        }
        return pageHeight;
    }-*/;
        
    
        private native int getWidth() /*-{
        var xScroll;
        
        if ($wnd.innerHeight && $wnd.scrollMaxY) {      
            xScroll = $doc.body.scrollWidth;
        }
        else if ($doc.body.scrollHeight > $doc.body.offsetHeight){ // all but Explorer Mac
            xScroll = $doc.body.scrollWidth;
        }
        else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
            xScroll = $doc.body.offsetWidth;
        }
        
        var windowHeight;
        if (self.innerHeight) { // all except Explorer
            windowWidth = self.innerWidth;
        }
        else if ($doc.documentElement && $doc.documentElement.clientHeight) { // Explorer 6 Strict Mode
            windowWidth = $doc.documentElement.clientWidth;
        }
        else if ($doc.body) { // other Explorers
            windowWidth = $doc.body.clientWidth;
        }       
        
        // for small pages with total width less then width of the viewport
        if(xScroll < windowWidth){      
            pageWidth = windowWidth;
        }
        else {
            pageWidth = xScroll;
        }
        return pageWidth;
    }-*/;
        
}
