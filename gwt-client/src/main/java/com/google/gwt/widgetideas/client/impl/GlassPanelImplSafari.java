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

import com.google.gwt.user.client.Element;
import com.google.gwt.widgetideas.client.GlassPanel;

/**
 * Safari implementation of {@link GlassPanelImpl}.
 */
public class GlassPanelImplSafari extends GlassPanelImplStandard {
  private static native void implMatchDocumentSize(Element elem)
  /*-{
    var scrollWidth = @com.google.gwt.widgetideas.client.impl.GlassPanelImpl::getWindowScrollWidth()();
    var scrollHeight = @com.google.gwt.widgetideas.client.impl.GlassPanelImpl::getWindowScrollHeight()();

    var width = Math.max($wnd.innerWidth, scrollWidth);
    var height = Math.max($wnd.innerHeight, scrollHeight);

    var computedStyle = $doc.defaultView.getComputedStyle($doc.documentElement, null);
    var marginLeft = parseInt(computedStyle.getPropertyValue("margin-left"));
    var marginTop = parseInt(computedStyle.getPropertyValue("margin-top"));
    
    // TODO Verify 'negative HTML element margins' case
    elem.style.marginLeft = -marginLeft + "px";
    elem.style.marginTop = -marginTop + "px";
    
    elem.style.width = (width + marginLeft) + "px";
    elem.style.height = (height + marginTop) + "px";
  }-*/;

  // TODO remove once GWT issue 1981 is fixed
  // http://code.google.com/p/google-web-toolkit/issues/detail?id=1981
  @Override
  public native boolean isCSS1Compat()
  /*-{
    // Safari does not implement $doc.compatMode.
    // Use a CSS test to determine rendering mode.
    var elem = $doc.createElement('div');
    elem.style.cssText = "width:0px;width:1";
    return parseInt(elem.style.width) != 1;
  }-*/;

  @Override
  public void matchDocumentSize(GlassPanel glassPanel, boolean dueToResize) {
    implMatchDocumentSize(glassPanel.getElement());
  }
}
