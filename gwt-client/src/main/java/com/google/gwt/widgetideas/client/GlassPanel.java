/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.widgetideas.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.impl.GlassPanelImpl;

/**
 * Semi-transparent panel which can be attached to any AbsolutePanel, although
 * typically a RootPanel via
 * {@link AbsolutePanel#add(com.google.gwt.user.client.ui.Widget, int, int) parent.add(glassPanel, 0, 0)}.
 * Note that <a
 * href="http://code.google.com/p/google-web-toolkit/issues/detail?id=1813">GWT
 * issue 1813</a> needs to be considered in order to properly use
 * <code>RootPanel.get(id)</code> as the parent panel in the general case.
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-GlassPanel { }</li>
 * </ul>
 * 
 * <p>
 * <h3>Example</h3>
 * {@example com.google.gwt.examples.GlassPanelExample}
 * </p>
 */
public class GlassPanel extends Composite implements EventPreview {
  /**
   * A FocusPanel which automatically focuses itself when
   * attached (in order to blur any currently focused widget)
   * and then removes itself.
   */
  private static class FocusPanelImpl extends FocusPanel {
    public FocusPanelImpl() {
      addFocusListener(new FocusListener() {

        public void onFocus(Widget sender) {
          FocusPanelImpl.this.removeFromParent();
        }

        public void onLostFocus(Widget sender) {
        }
      });
    }

    @Override
    protected void onLoad() {
      super.onLoad();
      /**
       * Removed DeferredCommand if/when GWT issue 1849 is implemented
       * http://code.google.com/p/google-web-toolkit/issues/detail?id=1849
       */
      DeferredCommand.addCommand(new Command() {
        public void execute() {
          setFocus(true);
        }
      });
    }
  }

  private static final GlassPanelImpl impl = GWT.create(GlassPanelImpl.class);

  private final boolean autoHide;

  private final SimplePanel mySimplePanel;

  private WindowResizeListener resizeListener;

  private final Timer timer = new Timer() {
    @Override
    public void run() {
      impl.matchDocumentSize(GlassPanel.this, false);
    }
  };

  /**
   * Create a glass panel widget that can be attached to an AbsolutePanel via
   * {@link AbsolutePanel#add(com.google.gwt.user.client.ui.Widget, int, int) absolutePanel.add(glassPanel, 0, 0)}.
   * 
   * @param autoHide <code>true</code> if the glass panel should be
   *          automatically hidden when the user clicks on it or presses
   *          <code>ESC</code>.
   */
  public GlassPanel(boolean autoHide) {
    this.autoHide = autoHide;
    mySimplePanel = new SimplePanel();
    initWidget(mySimplePanel);
    Style style = getElement().getStyle();
    style.setProperty("backgroundColor", "#000");
    style.setProperty("filter", "alpha(opacity=50)");
    style.setProperty("opacity", "0.5");
    setStyleName("gwt-GlassPanel");
  }

  public boolean onEventPreview(Event event) {
    int type = DOM.eventGetType(event);
    switch (type) {
      case Event.ONKEYPRESS: {
        if (DOM.eventGetKeyCode(event) == KeyboardListener.KEY_ESCAPE) {
          removeFromParent();
          return false;
        }
      }
      case Event.ONCLICK: {
        if (DOM.isOrHasChild(getElement(), DOM.eventGetTarget(event))) {
          removeFromParent();
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void setWidget(Widget widget) {
    mySimplePanel.setWidget(widget);
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    AbsolutePanel parent;
    try {
      parent = (AbsolutePanel) getParent();
    } catch (RuntimeException e) {
      throw new IllegalStateException("Parent widget must be an instance of AbsolutePanel");
    }

    if (parent == RootPanel.get()) {
      impl.matchDocumentSize(this, false);
      timer.scheduleRepeating(100);
      resizeListener = new WindowResizeListener() {
        public void onWindowResized(int width, int height) {
          impl.matchDocumentSize(GlassPanel.this, true);
        }
      };
      Window.addWindowResizeListener(resizeListener);
    } else {
      impl.matchParentSize(this, parent);
    }
    if (autoHide) {
      DOM.addEventPreview(this);
    }

    RootPanel.get().add(new FocusPanelImpl(), Window.getScrollLeft(), Window.getScrollTop());
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    timer.cancel();
    if (resizeListener != null) {
      Window.removeWindowResizeListener(resizeListener);
      resizeListener = null;
    }
    if (autoHide) {
      DOM.removeEventPreview(this);
    }
  }

  @Override
  protected void onLoad() {
    super.onLoad();
    if (!GWT.isScript()) {
      // In Webkit quirks mode table cells refuse to take on
      // 'display: block', using 'display: table-cell' instead.
      if (!impl.isCSS1Compat()) {
        String nodeName = getParent().getElement().getNodeName();
        if (nodeName.matches("^(TD|TH)$")) {
          String msg = "SORRY: GlassPanel cannot properly cover a " + nodeName
              + " element in quirks mode due to CSS restrictions in certain browsers."
              + " Please consider using standards mode or try using a different type of element.";
          GWT.log(msg, new UnsupportedOperationException());
          Window.alert(msg);
          assert false : msg;
        }
      }
    }
  }
}
