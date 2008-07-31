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
import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.RPCException;

public interface RegistryService extends RemoteService {

    Collection<WWorkspace> getWorkspaces() throws RPCException;
    
    void addWorkspace(String parentWorkspaceId, String workspaceName, String lifecycleId) throws RPCException, ItemNotFoundException, ItemExistsException;

    void updateWorkspace(String workspaceId, 
                         String parentWorkspaceId, 
                         String workspaceName,
                         String lifecycleId) throws RPCException, ItemNotFoundException;
    
    void deleteWorkspace(String workspaceId) throws RPCException, ItemNotFoundException;
    
    WArtifactType getArtifactType(String id) throws RPCException;
    

    Collection<WArtifactType> getArtifactTypes();

    void saveArtifactType(WArtifactType artifactType) throws RPCException, ItemExistsException;
    
    void deleteArtifactType(String id) throws RPCException;
    
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

    Collection<LinkInfo> getLinks(String itemId, String property) throws RPCException;
    
    ArtifactGroup getArtifact(String artifactId) throws RPCException, ItemNotFoundException;
    
    ArtifactGroup getArtifactByVersionId(String artifactVersionId) throws RPCException, ItemNotFoundException;
    
    ArtifactVersionInfo getArtifactVersionInfo(String artifactVersionId,
                                               boolean showHidden) throws RPCException, ItemNotFoundException;

    void setProperty(String artifactId, 
                     String propertyName, 
                     String propertyValue) throws RPCException, ItemNotFoundException, WPolicyException;
    
    void setProperty(String artifactId, 
                     String propertyName, 
                     Collection<String> propertyValue) throws RPCException, WPolicyException, ItemNotFoundException;

    void setProperty(Collection<String> artifactIds,
                     String propertyName,
                     String propertyValue) throws RPCException, WPolicyException, ItemNotFoundException;
    

    void setProperty(Collection<String> artifactIds,
                     String propertyName,
                     Collection<String> propertyValue) throws RPCException, WPolicyException, ItemNotFoundException;

    void deleteProperty(String artifactId, 
                        String propertyName) throws RPCException, ItemNotFoundException;
    
    void deleteProperty(Collection<String> artifactIds, 
                        String propertyName) throws RPCException, ItemNotFoundException;

    void savePropertyDescriptor(WPropertyDescriptor property) throws RPCException, ItemNotFoundException, ItemExistsException;
    
    void deletePropertyDescriptor(String id) throws RPCException;

    WPropertyDescriptor getPropertyDescriptor(String id) throws RPCException, ItemNotFoundException;
    
    List<WPropertyDescriptor> getPropertyDescriptors(boolean includeIndex) throws RPCException;
    
    List<WExtensionInfo> getExtensions() throws RPCException;
    
    void move(String artifactId, String workspaceId, String name) throws RPCException, ItemNotFoundException;
    
    void delete(String artifactId) throws RPCException, ItemNotFoundException;

    boolean deleteArtifactVersion(String artifactVersionId) throws RPCException, ItemNotFoundException;

    WComment addComment(String artifactId, String parentCommentId, String text) throws RPCException, ItemNotFoundException;
    
    void setDescription(String artifactId, String description) throws RPCException, ItemNotFoundException;

    void setDefault(String artifactVersionId) throws RPCException, WPolicyException, ItemNotFoundException;

    void setEnabled(String artifactVersionId, boolean enabled) throws RPCException, WPolicyException, ItemNotFoundException;

    void transition(Collection<String> artifactIds, String lifecycle, String phase) throws RPCException, WPolicyException, ItemNotFoundException;

    Collection<WArtifactPolicy> getPolicies() throws RPCException;
    
    WLifecycle getLifecycle(String id) throws RPCException;

    Collection<WLifecycle> getLifecycles() throws RPCException;

    void saveLifecycle(WLifecycle l) throws RPCException, ItemExistsException;
    
    void deleteLifecycle(String id) throws RPCException;
    
    Collection<String> getActivePoliciesForLifecycle(String lifecycle, String workspaceId) throws RPCException;

    Collection<String> getActivePoliciesForPhase(String lifecycle, String phase, String workspaceId) throws RPCException;

    void setActivePolicies(String workspace, String lifecycle, String phase, Collection<String> ids) throws RPCException, WPolicyException, ItemNotFoundException;

    Collection<WActivity> getActivities(Date from, Date to, String user, String eventType, int start, int results, boolean ascending) throws RPCException;
    
    WUser getUserInfo() throws RPCException;
}
