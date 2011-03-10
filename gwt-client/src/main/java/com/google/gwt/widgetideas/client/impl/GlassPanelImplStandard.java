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

import com.google.gwt.user.client.Window;
import com.google.gwt.widgetideas.client.GlassPanel;

/**
 * Base implementation of {@link GlassPanelImpl} for browsers implementing web
 * standards.
 */
public abstract class GlassPanelImplStandard extends GlassPanelImpl {
  @Override
  public void matchDocumentSize(GlassPanel glassPanel, boolean dueToResize) {
    int clientWidth = Window.getClientWidth();
    int clientHeight = Window.getClientHeight();

    int scrollWidth = getWindowScrollWidth();
    int scrollHeight = getWindowScrollHeight();

    int width = Math.max(clientWidth, scrollWidth);
    int height = Math.max(clientHeight, scrollHeight);

    glassPanel.setPixelSize(width - 1, height - 1);
  }

}
