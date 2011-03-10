/*
 * Copyright 2007 Google Inc.
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
 * Mozilla implementation of {@link GlassPanelImpl}.
 */
public class GlassPanelImplMozilla extends GlassPanelImplStandard {
  public void matchDocumentSize(GlassPanel glassPanel, boolean dueToResize) {
    super.matchDocumentSize(glassPanel, dueToResize);
    matchMargins(glassPanel.getElement());
  }

  private native void matchMargins(Element elem)
  /*-{
    // TODO Verify 'negative HTML element margins' case
    var computedStyle = $doc.defaultView.getComputedStyle($doc.documentElement, null);
    elem.style.marginLeft = ("-" + computedStyle.getPropertyValue("margin-left")).replace("--", "");
    elem.style.marginTop = ("-" + computedStyle.getPropertyValue("margin-top")).replace("--", "");
  }-*/;
}
