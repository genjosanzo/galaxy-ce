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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.workspace.AbstractWorkspaceManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.workspace.WorkspaceManager;

public final class DummyWorkspaceManager extends AbstractWorkspaceManager implements WorkspaceManager {
    private String id = "test";
    private Workspace attachedWorkspace;
    
    public DummyWorkspaceManager() {
        super();
    }

    public void validate() throws RegistryException {
    }

    public void attachTo(Workspace workspace) {
        this.attachedWorkspace = workspace;
    }

    public Workspace getAttachedToWorkspace() {
        return attachedWorkspace;
    }

    public void save(Item item) throws RegistryException, AccessException {
    }

    public EntryResult createArtifact(Workspace workspace, Object data, String versionLabel)
        throws RegistryException, PolicyException, MimeTypeParseException, DuplicateItemException,
        AccessException {
        return null;
    }

    public EntryResult createArtifact(Workspace workspace, String contentType, String name,
                                      String versionLabel, InputStream inputStream)
        throws RegistryException, PolicyException, IOException, MimeTypeParseException,
        DuplicateItemException, AccessException {
        return null;
    }

    public Workspace newWorkspace(Workspace parent, String name) throws DuplicateItemException,
        RegistryException, AccessException {
        return null;
    }

    public Workspace newWorkspace(String name) throws DuplicateItemException, RegistryException,
        AccessException {
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

    public List<Item> getItems(Workspace w) {
        return new ArrayList<Item>();
    }

    public Workspace getWorkspace(String id) throws RegistryException, NotFoundException, AccessException {
        return null;
    }

    public Collection<Workspace> getWorkspaces() throws AccessException {
        return null;
    }

    public Collection<Workspace> getWorkspaces(Workspace workspace) {
        return null;
    }

    public EntryResult newEntry(Workspace workspace, String name, String versionLabel)
        throws DuplicateItemException, RegistryException, PolicyException, AccessException {
        return null;
    }

    public EntryResult newVersion(Artifact artifact, InputStream inputStream, String versionLabel) throws RegistryException, PolicyException, IOException,
        DuplicateItemException, AccessException {
        return null;
    }

    public EntryResult newVersion(Artifact artifact, Object data, String versionLabel)
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException {
        return null;
    }

    public EntryResult newVersion(Entry jcrEntry, String versionLabel) throws DuplicateItemException,
        RegistryException, PolicyException, AccessException {
        return null;
    }

    public void setEnabled(EntryVersion version, boolean enabled) throws RegistryException,
        PolicyException {    
    }
    
}