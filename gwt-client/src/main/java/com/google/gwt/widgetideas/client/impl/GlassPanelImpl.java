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
package com.google.gwt.widgetideas.client.impl;


import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.widgetideas.client.GlassPanel;

/**
 * Deferred binding implementation of {@link GlassPanel}.
 */
public abstract class GlassPanelImpl {
  /**
   * TODO Replace with Window.getScrollHeight() when GWT issue 2068 is addressed
   */
  protected static native int getWindowScrollHeight()
  /*-{
    return @com.google.gwt.user.client.impl.DocumentRootImpl::documentRoot.scrollHeight;
  }-*/;

  /**
   * TODO Replace with Window.getScrollWidth() when GWT issue 2068 is addressed
   */
  protected static native int getWindowScrollWidth()
  /*-{
    return @com.google.gwt.user.client.impl.DocumentRootImpl::documentRoot.scrollWidth;
  }-*/;

  // TODO remove once GWT issue 1981 is fixed
  // http://code.google.com/p/google-web-toolkit/issues/detail?id=1981
  public native boolean isCSS1Compat()
  /*-{
    return $doc.compatMode == 'CSS1Compat';
  }-*/;

  public abstract void matchDocumentSize(GlassPanel glassPanel, boolean dueToResize);

  public void matchParentSize(GlassPanel glassPanel, AbsolutePanel parent) {
    glassPanel.getElement().getStyle().setProperty("bottom", "0px");
    glassPanel.getElement().getStyle().setProperty("right", "0px");
  }
}
