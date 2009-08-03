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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class GWTCButton extends Widget implements SourcesMouseEvents, SourcesClickEvents {
    public static int BUTTON_TYPE_1 = 1;
    public static int BUTTON_TYPE_2 = 2;
    
        private FlexTable container = new FlexTable();
        private boolean enabled = true;
    private int contentIndex = 1;
    private String[] styleComponentsType1 = {"x-btn", "left", "center", "right"};
    private String[] styleComponentsType2 = {"my-btn", "l", "ml", "c", "mr", "r"};
    private String[] styleComponents;
    
    public GWTCButton() {
        this(2);
    }
    public GWTCButton(int type) {
        if (type == BUTTON_TYPE_1) {
            contentIndex = 1;
            styleComponents = styleComponentsType1;
        } else {
            contentIndex = 2;
            styleComponents = styleComponentsType2;
        }
        setElement(container.getElement());
        container.setStyleName(styleComponents[0]);
        container.addStyleName("my-no-selection");
        container.setCellSpacing(0);
        container.setCellPadding(0);
        for (int idx = 1; idx < styleComponents.length; idx++) {
            container.setHTML(0, idx -1 , "<i>&nbsp;</i>");
            container.getCellFormatter().setStyleName(0, idx -1, styleComponents[0] + "-" + styleComponents[idx]);
        }
        sinkEvents(Event.MOUSEEVENTS);
        addMouseListener(mouseOverListener);
    }
        public GWTCButton(String html, ClickListener listener) {
                this(html);
                addClickListener(listener);
        }
        public GWTCButton(String html) {
                this();
                setHTML(html);
        }
    public GWTCButton(int type, String html, ClickListener listener) {
        this(type);
        setHTML(html);
        addClickListener(listener);
    }

        
    
    public void setText(String text) {
        setHTML(text);
    }

        public void setHTML(String html) {
                container.setHTML(0, contentIndex, html);
        }
        
        public void click() {
                clickListeners.fireClick(this);
        }

        public void onBrowserEvent(Event event) {
                mouseListeners.fireMouseEvent(this, event);
                if (enabled && DOM.eventGetType(event) == Event.ONCLICK) {
            removeStyleName(UIObject.getStylePrimaryName(this.getElement()) + "-down");
            removeStyleName(UIObject.getStylePrimaryName(this.getElement()) + "-over");
                        clickListeners.fireClick(this);
                }
        }
        
        public void setEnabled(boolean enabled) {
                this.enabled = enabled;
                if (enabled) {
                        this.removeStyleName(UIObject.getStylePrimaryName(getElement()) + "-disabled");
                } else {
                        this.addStyleName(UIObject.getStylePrimaryName(getElement()) + "-disabled");
                }
        }
        

        // SourcesMouseEvents Methods
        private MouseListenerCollection mouseListeners = new MouseListenerCollection();
        public void addMouseListener(MouseListener listener) {
                mouseListeners.add(listener);
        }
        public void removeMouseListener(MouseListener listener) {
                mouseListeners.remove(listener);
        }
        // SourcesClickEvents Methods
        private ClickListenerCollection clickListeners = new ClickListenerCollection();
        public void addClickListener(ClickListener listener) {
                clickListeners.add(listener);
        }
        public void removeClickListener(ClickListener listener) {
                clickListeners.remove(listener);
        }


        // A listener for changing style when the mouse is over
        public static final MouseListener mouseOverListener = new MouseListener() {
                public void onMouseUp(Widget sender, int x, int y) {
                    sender.removeStyleName(UIObject.getStylePrimaryName(sender.getElement()) + "-down");
                }

                public void onMouseMove(Widget sender, int x, int y) {
                }

        public void onMouseEnter(Widget sender) {
            sender.addStyleName(UIObject.getStylePrimaryName(sender.getElement()) + "-over");
        }
        
                public void onMouseLeave(Widget sender) {
            sender.removeStyleName(UIObject.getStylePrimaryName(sender.getElement()) + "-down");
                        sender.removeStyleName(UIObject.getStylePrimaryName(sender.getElement()) + "-over");
                }

                public void onMouseDown(Widget sender, int x, int y) {
            sender.addStyleName(UIObject.getStylePrimaryName(sender.getElement()) + "-down");
                }
        };

}
