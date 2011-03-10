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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.client.GlassPanel;

/**
 * IE6 implementation of {@link GlassPanelImpl}.
 */
public class GlassPanelImplIE6 extends GlassPanelImpl {
  private int lastDocumentClientWidth = -1;
  private int lastDocumentClientHeight = -1;

  @Override
  public void matchDocumentSize(GlassPanel glassPanel, boolean dueToResize) {
    int clientWidth = Window.getClientWidth();
    int clientHeight = Window.getClientHeight();

    // Workaround for issue 1934
    // IE fires Window onresize events when the size of the body changes
    if (!dueToResize || clientWidth != lastDocumentClientWidth
        || clientHeight != lastDocumentClientHeight) {
      int offsetWidth = RootPanel.get().getOffsetWidth();
      int offsetHeight = RootPanel.get().getOffsetHeight();

      int scrollWidth = getWindowScrollWidth();
      int scrollHeight = getWindowScrollHeight();

      int width = Math.max(clientWidth, Math.max(offsetWidth, scrollWidth));
      int height = Math.max(clientHeight, Math.max(offsetHeight, scrollHeight));
      glassPanel.setPixelSize(width, height);

      lastDocumentClientWidth = clientWidth;
      lastDocumentClientHeight = clientHeight;
    }
  }

  @Override
  public final void matchParentSize(final GlassPanel glassPanel, final AbsolutePanel parent) {
    if (isCSS1Compat()) {
      super.matchParentSize(glassPanel, parent);
    } else {
      // less than perfect due to parent borders, but works around other buggy size issues
      glassPanel.setPixelSize(parent.getOffsetWidth(), parent.getOffsetHeight());
    }
  }
}
