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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.impl.workspace.AbstractWorkspaceManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.workspace.WorkspaceManager;

public class DummyWorkspaceManager extends AbstractWorkspaceManager implements WorkspaceManager {
    private String id = "test";
    private Item attachedWorkspace;
    
    public DummyWorkspaceManager() {
        super();
    }

    public void validate() throws RegistryException {
    }

    public void attachTo(Item workspace) {
        this.attachedWorkspace = workspace;
    }

    public Item getAttachedToWorkspace() {
        return attachedWorkspace;
    }

    public void save(Item item) throws RegistryException, AccessException {
    }

    public NewItemResult newItem(Item parent, String name, Type type,
			Map<String, Object> initialProperties)
			throws DuplicateItemException, RegistryException, PolicyException,
			AccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(Item item) throws RegistryException, AccessException {
    }

    public String getId() {
        return id;
    }

    public Item getItemById(String id) throws NotFoundException, RegistryException, AccessException {
        return null;
    }

    public Item getItemByPath(String id) throws NotFoundException, RegistryException, AccessException {
        return null;
    }

    public List<Item> getItems(Item w) {
        return new ArrayList<Item>();
    }

    public Item getItem(Item w, String name) throws RegistryException, NotFoundException {
        return null;
    }

    public Item getWorkspace(String id) throws RegistryException, NotFoundException, AccessException {
        return null;
    }

    public Collection<Item> getWorkspaces() throws AccessException {
        return null;
    }

    public Collection<Item> getWorkspaces(Item workspace) {
        return null;
    }

    public TypeManager getTypeManager() {
        return null;
    }

}