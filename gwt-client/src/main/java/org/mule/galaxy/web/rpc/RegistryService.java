/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.RPCException;

public interface RegistryService extends RemoteService {

    public enum ApplyTo implements IsSerializable {
        ENTRY,
        DEFAULT_VERSION,
        ALL_VERSIONS
    }
    
    Collection<WWorkspace> getWorkspaces(String parentId) throws RPCException;
    
    WWorkspace getWorkspace(String id) throws RPCException;
    
    void addWorkspace(String parentWorkspaceId, String workspaceName, String lifecycleId) throws RPCException, ItemNotFoundException, ItemExistsException;

    void updateWorkspace(String workspaceId, 
                         String parentWorkspaceId, 
                         String workspaceName,
                         String lifecycleId) throws RPCException, ItemNotFoundException;

    WArtifactType getArtifactType(String id) throws RPCException;
    

    Collection<WArtifactType> getArtifactTypes();

    void saveArtifactType(WArtifactType artifactType) throws RPCException, ItemExistsException;
    
    void deleteArtifactType(String id) throws RPCException;
    
    String newEntry(String workspaceId, String name, String version) throws RPCException, ItemExistsException, ItemNotFoundException, WPolicyException;
    
    String newEntryVersion(String entryId, String version) throws RPCException, ItemExistsException, ItemNotFoundException, WPolicyException;
    
    WSearchResults getArtifacts(String workspaceId, String workspacePath, 
                                boolean includeChildWkspcs, Set<String> artifactTypes, 
                                Set<SearchPredicate> searchPredicates, String freeformQuery, int start, int maxResults) throws RPCException;
    
    WSearchResults getArtifactsForView(String viewId, int resultStart, int maxResults) throws RPCException;

    public Collection<WArtifactView> getArtifactViews() throws RPCException;

    public Collection<WArtifactView> getRecentArtifactViews() throws RPCException;
    
    public WArtifactView getArtifactView(String id) throws RPCException, ItemExistsException, ItemNotFoundException;
 
    public String saveArtifactView(WArtifactView view) throws RPCException;
    
    public void deleteArtifactView(String id) throws RPCException;

    Collection<WIndex> getIndexes();
    
    WIndex getIndex(String id) throws RPCException;
    
    void saveIndex(WIndex index) throws RPCException;
    
    void deleteIndex(String id, boolean removeArtifactMetadata) throws RPCException;

    
    boolean itemExists(String path) throws RPCException;
    
    ExtendedEntryInfo getEntry(String entryId) throws RPCException, ItemNotFoundException;
    
    ExtendedEntryInfo getArtifactByVersionId(String artifactVersionId) throws RPCException, ItemNotFoundException;
    
    ItemInfo getItemInfo(String artifactVersionId, boolean showHidden) throws RPCException, ItemNotFoundException;

    Collection<EntryInfo> suggestEntries(String query, String exclude) throws RPCException;

    Collection<String> suggestWorkspaces(String query, String exclude) throws RPCException;

    void setProperty(Collection<String> entryIds,
                     String propertyName,
                     Serializable propertyValue, ApplyTo applyTo) throws RPCException, WPolicyException, ItemNotFoundException;

    void setProperty(String entryId,
                     String propertyName,
                     Serializable propertyValue) throws RPCException, WPolicyException, ItemNotFoundException;

    void deleteProperty(String entryId, 
                        String propertyName) throws RPCException, ItemNotFoundException;
    
    void deleteProperty(Collection<String> entryIds, 
                        String propertyName, 
                        ApplyTo applyTo) throws RPCException, ItemNotFoundException;

    void savePropertyDescriptor(WPropertyDescriptor property) throws RPCException, ItemNotFoundException, ItemExistsException;
    
    void deletePropertyDescriptor(String id) throws RPCException;

    WPropertyDescriptor getPropertyDescriptor(String id) throws RPCException, ItemNotFoundException;
    
    List<WPropertyDescriptor> getPropertyDescriptors(boolean includeIndex) throws RPCException;
    
    Map<String, String> getQueryProperties() throws RPCException;
    
    List<WExtensionInfo> getExtensions() throws RPCException;
    
    
    void move(String entryId, String workspaceId, String name, String newVersion) throws RPCException, ItemNotFoundException;
    
    void delete(String entryId) throws RPCException, ItemNotFoundException;

    boolean deleteArtifactVersion(String artifactVersionId) throws RPCException, ItemNotFoundException;

    WComment addComment(String entryId, String parentCommentId, String text) throws RPCException, ItemNotFoundException;
    
    void setDescription(String entryId, String description) throws RPCException, ItemNotFoundException;

    void setDefault(String artifactVersionId) throws RPCException, WPolicyException, ItemNotFoundException;

    void setEnabled(String artifactVersionId, boolean enabled) throws RPCException, WPolicyException, ItemNotFoundException;

    void transition(Collection<String> entryIds, String lifecycle, String phase) throws RPCException, WPolicyException, ItemNotFoundException;

    Collection<WPolicy> getPolicies() throws RPCException;
    
    WLifecycle getLifecycle(String id) throws RPCException;

    Collection<WLifecycle> getLifecycles() throws RPCException;

    void saveLifecycle(WLifecycle l) throws RPCException, ItemExistsException;
    
    void deleteLifecycle(String id) throws RPCException;
    
    Collection<String> getActivePoliciesForLifecycle(String lifecycle, String workspaceId) throws RPCException;

    Collection<String> getActivePoliciesForPhase(String lifecycle, String phase, String workspaceId) throws RPCException;

    void setActivePolicies(String workspace, String lifecycle, String phase, Collection<String> ids) throws RPCException, WPolicyException, ItemNotFoundException;

    Collection<WActivity> getActivities(Date from, Date to, String user, String itemId, String text, 
                                        String eventType, int start, int results, boolean ascending) throws RPCException;
    
    WUser getUserInfo() throws RPCException;
}
