/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Hyperlink;
import org.mule.galaxy.web.client.admin.AdministrationPanel;

public abstract class MenuPanelPageInfo extends PageInfo {
    private AbstractMenuPanel menuPanel;
    
    public MenuPanelPageInfo(String name, AbstractMenuPanel menuPanel) {
        super(name);
        this.menuPanel = menuPanel;
    }

    public MenuPanelPageInfo(Hyperlink hyperlink, AbstractMenuPanel menuPanel) {
        this(hyperlink.getTargetHistoryToken(), menuPanel);
    }

    public int getTabIndex() {
        return menuPanel.getTabIndex();
    }

    public void show() {
        menuPanel.setMain(getInstance());
    }
    
}