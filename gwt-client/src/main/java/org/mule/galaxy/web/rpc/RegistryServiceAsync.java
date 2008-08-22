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

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.RPCException;

public interface RegistryServiceAsync {
    void getWorkspaces(String parentId, AsyncCallback callback);
    
    void addWorkspace(String parentWorkspaceId, 
                      String workspaceName,
                      String lifecycleId,
                      AsyncCallback callback);
    
    void updateWorkspace(String workspaceId, 
                         String parentWorkspacePath, 
                         String workspaceName,
                         String lifecycleId,
                         AsyncCallback callback);

    void getArtifactType(String id, AsyncCallback c);
    
    void getArtifactTypes(AsyncCallback callback);
    
    void saveArtifactType(WArtifactType artifactType, 
                      AsyncCallback callback);
    
    void deleteArtifactType(String id, 
                            AsyncCallback callback);
    
    void newEntry(String workspacePath, String name, String version, AsyncCallback callback);

    void newEntryVersion(String entryId, String version, AsyncCallback callback);
    
    void getArtifacts(String workspace, String workspacePath, 
                      boolean includeChildWkspcs, Set<String> artifactTypes, 
                      Set<SearchPredicate> searchPredicates, String freeformQuery, 
                      int start, int maxResults, 
                      AsyncCallback callback);
    
    void suggestEntries(String query, String exclude, AsyncCallback<Collection<EntryInfo>> callback);

    void suggestWorkspaces(String query, String exclude, AsyncCallback<Collection<String>> callback);
    
    void getIndexes(AsyncCallback callback);

    void getIndex(String id, AsyncCallback c);
    
    void saveIndex(WIndex index, AsyncCallback callback);
    
    void deleteIndex(String id, boolean removeArtifactMetadata, AsyncCallback callback);
    
    void getExtensions(AsyncCallback callback);
    
    
    void itemExists(String path, AsyncCallback<Boolean> callback);

    void addLink(String itemId, String property, String path, AsyncCallback<LinkInfo> callback);
    
    void removeLink(String itemId, String property, String linkId, AsyncCallback callback);
    
    void getLinks(String artifactId, String property, AsyncCallback<WLinks> callback);


    void getEntry(String artifactId, AsyncCallback callback);
    
    void getArtifactByVersionId(String artifactVersionId, AsyncCallback callback);
    
    void getItemInfo(String entryVersionId, boolean showHidden, AsyncCallback<ItemInfo> callback);
    
    void setProperty(String artifactId, 
                     String propertyName, 
                     String propertyValue,
                     AsyncCallback callback);
    
    void setProperty(Collection artifactIds, 
                     String propertyName,
                     String propertyValue,
                     AsyncCallback callback);

    void setProperty(String artifactId, 
                     String propertyName, 
                     Collection propertyValue,
                     AsyncCallback callback);
    
    void setProperty(Collection artifactIds, 
                     String propertyName,
                     Collection propertyValue,
                     AsyncCallback callback);

    void deleteProperty(String artifactId,
                        String propertyName, 
                        AsyncCallback callback);
    
    void deleteProperty(Collection artifactIds,
                        String propertyName,
                        AsyncCallback callback);

    void deleteProperty(Collection artifactIds,
                        String propertyName,
                        String propertyValue,
                        AsyncCallback callback);

    void addComment(String artifactId, String parentCommentId, String text, AsyncCallback callback);
    
    void setDescription(String artifactId, String description, AsyncCallback callback);
    
    void savePropertyDescriptor(WPropertyDescriptor property, AsyncCallback c);
    
    void deletePropertyDescriptor(String id, AsyncCallback c);

    void getPropertyDescriptors(boolean includeIndexes, AsyncCallback abstractCallback);
    
    void getQueryProperties(AsyncCallback<Map<String, String>> callback);
    
    void transition(Collection artifactIds, String lifecycle, String phase, AsyncCallback c);

    void setDefault(String artifactVersionId, AsyncCallback c);
    
    void move(String artifactId, String parentPath, String name, String newVersion, AsyncCallback c);
    
    void delete(String artifactId, AsyncCallback c);

    void deleteArtifactVersion(String artifactId, AsyncCallback c);
    
    void getPolicies(AsyncCallback c);

    void getLifecycle(String id, AsyncCallback c);
    
    void getLifecycles(AsyncCallback c);

    void saveLifecycle(WLifecycle l, AsyncCallback c);
    
    void getActivePoliciesForLifecycle(String name, String workspaceId, AsyncCallback c);
    
    void getActivePoliciesForPhase(String lifecycle, String phase, String workspaceId, AsyncCallback c);

    void setActivePolicies(String workspace, String lifecycle, String phase, Collection<String> ids, AsyncCallback c);
    
    void getActivities(Date from, Date to, String user, String eventType, int start, int results, boolean ascending, AsyncCallback c);
    
    void getUserInfo(AsyncCallback c);

    void deleteLifecycle(String id, AsyncCallback abstractCallback);

    void setEnabled(String versionId, boolean enabled, AsyncCallback callback);

    void getPropertyDescriptor(String id, AsyncCallback fetchCallback);

    void getArtifactsForView(String viewId, int resultStart, int maxResults, AsyncCallback callback);
    
    void getArtifactViews(AsyncCallback callback);
    
    void getArtifactView(String id, AsyncCallback callback);
    
    void saveArtifactView(WArtifactView view, AsyncCallback callback);
    
    void deleteArtifactView(String id, AsyncCallback callback);
    
    void getRecentArtifactViews(AsyncCallback callback);

    void getWorkspace(String workspaceId, AsyncCallback<WWorkspace> callback);
}