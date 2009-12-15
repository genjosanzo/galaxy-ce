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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.RPCException;

import com.google.gwt.user.client.rpc.RemoteService;

public interface RegistryService extends RemoteService {

    ApplicationInfo getApplicationInfo() throws RPCException;
    
    Collection<ItemInfo> getItems(String parentId, boolean traverseUpParents) throws RPCException;
    
    Collection<ItemInfo> getItemsWithAllChildren(String parentPath) throws RPCException;

    Collection<ItemInfo> getItemsInPath(String parentPath) throws RPCException;

    String addItem(String parentPath, 
                   String workspaceName,
                   String lifecycleId, 
                   String typeId, 
                   Map<String, Serializable> properties) 
        throws RPCException, ItemNotFoundException, ItemExistsException, WPolicyException;

    String addVersionedItem(String parentPath, 
                            String name,
                            String versionName,
                            String lifecycleId, 
                            String typeId, 
                            String versionTypeId, 
                            Map<String, Serializable> properties, 
                            Map<String, Serializable> versionProperties) 
        throws RPCException, ItemNotFoundException, ItemExistsException, WPolicyException;

    WArtifactType getArtifactType(String id) throws RPCException;
    

    Collection<WArtifactType> getArtifactTypes();

    void saveArtifactType(WArtifactType artifactType) throws RPCException, ItemExistsException;
    
    void deleteArtifactType(String id) throws RPCException;

    WSearchResults getArtifacts(String workspaceId, 
                                String workspacePath, 
                                boolean includeChildWkspcs,
                                Set<SearchPredicate> searchPredicates, 
                                String freeformQuery, 
                                int start, 
                                int maxResults) throws RPCException;
    
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
    
    ItemInfo getItemInfo(String itemId, boolean showHidden) throws RPCException, ItemNotFoundException;

    ItemInfo getItemByPath(String path) throws RPCException, ItemNotFoundException;

    Collection<ItemInfo> suggestItems(String query, boolean recursive, String exclude, String[] types) throws RPCException;
    
    void setPropertyForQuery(String query,
                             String propertyName,
                             Serializable propertyValue) throws RPCException, WPolicyException, ItemNotFoundException;

    void setProperty(String entryId,
                     String propertyName,
                     Serializable propertyValue) throws RPCException, WPolicyException, ItemNotFoundException;

    void setProperty(Collection<String> itemIds,
                     String propertyName,
                     Serializable propertyValue) throws RPCException, WPolicyException, ItemNotFoundException;

    void deleteProperty(String entryId, 
                        String propertyName) throws RPCException, ItemNotFoundException;
    
    void deletePropertyForQuery(String query,
                                String propertyName) throws RPCException, ItemNotFoundException;

    void deleteProperty(Collection<String> itemIds,
                        String propertyName) throws RPCException, ItemNotFoundException;
    
    void savePropertyDescriptor(WPropertyDescriptor property) throws RPCException, ItemNotFoundException, ItemExistsException;
    
    void deletePropertyDescriptor(String id) throws RPCException;

    WPropertyDescriptor getPropertyDescriptor(String id) throws RPCException, ItemNotFoundException;
    
    List<WPropertyDescriptor> getPropertyDescriptors(boolean includeIndex) throws RPCException;
    
    Map<String, String> getQueryProperties() throws RPCException;

    List<WType> getTypes() throws RPCException;
    
    void move(String entryId, String workspaceId, String name) throws RPCException, ItemNotFoundException, WPolicyException;
    
    void delete(String entryId) throws RPCException, ItemNotFoundException;

    void delete(List<String> ids) throws RPCException, ItemNotFoundException;
    
    WComment addComment(String entryId, String parentCommentId, String text) throws RPCException, ItemNotFoundException;

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
    
    void saveType(WType type) throws RPCException, ItemExistsException;
    
    WType getType(String id) throws RPCException;
}
