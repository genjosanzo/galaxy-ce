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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RegistryServiceAsync {
    void getItems(String parentId, AsyncCallback callback);
    
    void addItem(String parentId, 
                 String workspaceName,
                 String lifecycleId,
                 String type,
                 Map<String, Serializable> properties,
                 AsyncCallback callback);

    void getArtifactType(String id, AsyncCallback c);
    
    void getArtifactTypes(AsyncCallback callback);
    
    void saveArtifactType(WArtifactType artifactType, 
                      AsyncCallback callback);
    
    void deleteArtifactType(String id, 
                            AsyncCallback callback);

    void getArtifacts(String workspace, 
                      String workspacePath, 
                      boolean includeChildWkspcs,
                      Set<SearchPredicate> searchPredicates, 
                      String freeformQuery, 
                      int start, 
                      int maxResults, 
                      AsyncCallback callback);
    
    void suggestEntries(String query, String exclude, String[] type, AsyncCallback<Collection<ItemInfo>> callback);
    
    void getIndexes(AsyncCallback callback);

    void getIndex(String id, AsyncCallback c);
    
    void saveIndex(WIndex index, AsyncCallback callback);
    
    void deleteIndex(String id, boolean removeArtifactMetadata, AsyncCallback callback);
    
    void getExtensions(AsyncCallback callback);

    void itemExists(String path, AsyncCallback<Boolean> callback);

    void getItemInfo(String itemId, boolean showHidden, AsyncCallback<ItemInfo> callback);
    
    // item operations
    
    void setProperty(String itemId, 
                     String propertyName, 
                     Serializable propertyValue,
                     AsyncCallback callback);
    
    void deleteProperty(String itemId,
                        String propertyName, 
                        AsyncCallback callback);

    // bulk operations
    
    void setProperty(Collection itemIds, 
                     String propertyName, 
                     Serializable propertyValue,
                     AsyncCallback callback);

    void setPropertyForQuery(String query,
                     String propertyName, 
                     Serializable propertyValue,
                     AsyncCallback callback);
 
    void deleteProperty(Collection itemIds,
                        String propertyName,
                        AsyncCallback callback);
 
    void deletePropertyForQuery(String query,
                                String propertyName,
                                AsyncCallback callback);
    
    void addComment(String itemId, String parentCommentId, String text, AsyncCallback callback);
    
    void savePropertyDescriptor(WPropertyDescriptor property, AsyncCallback c);
    
    void saveType(WType type, AsyncCallback c);
    
    void getType(String id, AsyncCallback<WType> c);
    
    void deletePropertyDescriptor(String id, AsyncCallback c);

    void getPropertyDescriptors(boolean includeIndexes, AsyncCallback abstractCallback);
    
    void getTypes(AsyncCallback<List<WType>> callback);
    
    void getQueryProperties(AsyncCallback<Map<String, String>> callback);
    
    void transition(Collection itemIds, String lifecycle, String phase, AsyncCallback c);

    void move(String itemId, String parentPath, String name, AsyncCallback c);
    
    void delete(String itemId, AsyncCallback c);

    void getPolicies(AsyncCallback c);

    void getLifecycle(String id, AsyncCallback c);
    
    void getLifecycles(AsyncCallback c);

    void saveLifecycle(WLifecycle l, AsyncCallback c);
    
    void getActivePoliciesForLifecycle(String name, String workspaceId, AsyncCallback c);
    
    void getActivePoliciesForPhase(String lifecycle, String phase, String workspaceId, AsyncCallback c);

    void setActivePolicies(String workspace, String lifecycle, String phase, Collection<String> ids, AsyncCallback c);
    
    void getActivities(Date from, Date to, String user, String itemId, String text,
                       String eventType, int start, int results, boolean ascending, AsyncCallback c);
    
    void getUserInfo(AsyncCallback c);

    void deleteLifecycle(String id, AsyncCallback abstractCallback);

    void getPropertyDescriptor(String id, AsyncCallback fetchCallback);

    void getArtifactsForView(String viewId, int resultStart, int maxResults, AsyncCallback callback);
    
    void getArtifactViews(AsyncCallback callback);
    
    void getArtifactView(String id, AsyncCallback callback);
    
    void saveArtifactView(WArtifactView view, AsyncCallback callback);
    
    void deleteArtifactView(String id, AsyncCallback callback);
    
    void getRecentArtifactViews(AsyncCallback callback);
}