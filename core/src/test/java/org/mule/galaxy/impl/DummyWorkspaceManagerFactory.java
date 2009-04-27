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

package org.mule.galaxy.impl;

import java.util.Map;

import org.mule.galaxy.workspace.WorkspaceManager;
import org.mule.galaxy.workspace.WorkspaceManagerFactory;

public class DummyWorkspaceManagerFactory extends WorkspaceManagerFactory {

    @Override
    public WorkspaceManager createWorkspaceManager(Map<String, String> configuration) {
        return new DummyWorkspaceManager();
    }

    @Override
    public String getName() {
        return "Dummy";
    }
    
}