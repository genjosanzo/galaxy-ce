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

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.RPCException;

public interface RegistryService extends RemoteService {
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WWorkspace>
     * @return
     * @throws RPCException 
     */
    Collection getWorkspaces() throws RPCException;
    
    void addWorkspace(String parentWorkspaceId, String workspaceName, String lifecycleId) throws RPCException, ItemNotFoundException, ItemExistsException;

    void updateWorkspace(String workspaceId, 
                         String parentWorkspaceId, 
                         String workspaceName,
                         String lifecycleId) throws RPCException, ItemNotFoundException;
    
    void deleteWorkspace(String workspaceId) throws RPCException, ItemNotFoundException;
    
    WArtifactType getArtifactType(String id) throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WArtifactType>
     * @return
     */
    Collection getArtifactTypes();

    void saveArtifactType(WArtifactType artifactType) throws RPCException, ItemExistsException;
    
    void deleteArtifactType(String id) throws RPCException;
    
    /**
     * @gwt.typeArgs searchPredicates <org.mule.galaxy.web.rpc.SearchPredicate>
     * @param start TODO
     * @param maxResults TODO
     * @return 
     * @throws RPCException 
     */
    WSearchResults getArtifacts(String workspace, Set artifactTypes, 
                                Set searchPredicates, String freeformQuery, 
                                int start, int maxResults) throws RPCException;
    
    WSearchResults getArtifactsForView(String viewId, int resultStart, int maxResults) throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WArtifactView>
     * @return
     * @throws RPCException 
     */
    public Collection getArtifactViews() throws RPCException;
    
    public WArtifactView getArtifactView(String id) throws RPCException;
    
    /**
     * Save an artifact view and return the id.
     * @param view
     * @return
     * @throws RPCException
     */
    public String saveArtifactView(WArtifactView view) throws RPCException;
    
    public void deleteArtifactView(String id) throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WIndex>
     * @return
     */
    public Collection getIndexes();
    
    public WIndex getIndex(String id) throws RPCException;
    
    public void saveIndex(WIndex index) throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.DependencyInfo>
     * @return
     * @throws Exception 
     */
    Collection getDependencyInfo(String artifactId) throws RPCException;
    
    ArtifactGroup getArtifact(String artifactId) throws RPCException, ItemNotFoundException;
    
    ArtifactVersionInfo getArtifactVersionInfo(String artifactVersionId,
                                               boolean showHidden) throws RPCException, ItemNotFoundException;
    
    void newPropertyDescriptor(String name, 
                               String description, 
                               boolean multivalued) throws RPCException, ItemExistsException;
    
    void setProperty(String artifactId, 
                     String propertyName, 
                     String propertyValue) throws RPCException, ItemNotFoundException;
    

    void deleteProperty(String artifactId, 
                        String propertyName) throws RPCException, ItemNotFoundException;
    
    void savePropertyDescriptor(WPropertyDescriptor property) throws RPCException, ItemNotFoundException, ItemExistsException;
    
    void deletePropertyDescriptor(String id) throws RPCException;

    WPropertyDescriptor getPropertyDescriptor(String id) throws RPCException, ItemNotFoundException;
    
    Collection getPropertyDescriptors() throws RPCException;
    
    void move(String artifactId, String workspaceId, String name) throws RPCException, ItemNotFoundException;
    
    void delete(String artifactId) throws RPCException, ItemNotFoundException;
    
    Map getPropertyList() throws RPCException;
    
    /**
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     * @return
     * @throws Exception 
     */
    Map getProperties() throws RPCException;

    WComment addComment(String artifactId, String parentCommentId, String text) throws RPCException, ItemNotFoundException;
    
    void setDescription(String artifactId, String description) throws RPCException, ItemNotFoundException;

    WGovernanceInfo getGovernanceInfo(String artifactVersionId) throws RPCException, ItemNotFoundException;

    TransitionResponse setDefault(String artifactVersionId) throws RPCException, ItemNotFoundException;

    TransitionResponse setEnabled(String artifactVersionId, boolean enabled) throws RPCException, ItemNotFoundException;
    
    TransitionResponse transition(String artifactVersionId, String nextPhase) throws RPCException, ItemNotFoundException;


    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WArtifactPolicy>
     * @return
     * @throws Exception 
     */
    Collection getPolicies() throws RPCException;
    
    WLifecycle getLifecycle(String id) throws RPCException;
    
    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WArtifactPolicy>
     * @return
     * @throws Exception 
     */
    Collection getLifecycles() throws RPCException;

    void saveLifecycle(WLifecycle l) throws RPCException, ItemExistsException;
    
    void deleteLifecycle(String id) throws RPCException;
    
    /**
     * @gwt.typeArgs <java.lang.String>
     * @return
     * @throws RPCException 
     */
    Collection getActivePoliciesForLifecycle(String lifecycle, String workspaceId) throws RPCException;
    
    /**
     * @gwt.typeArgs <java.lang.String>
     * @return
     * @throws RPCException 
     */
    Collection getActivePoliciesForPhase(String lifecycle, String phase, String workspaceId) throws RPCException;

    /**
     * @throws ApplyPolicyException 
     * @throws ItemNotFoundException 
     * @gwt.typeArgs ids <java.lang.String>
     */
    void setActivePolicies(String workspace, String lifecycle, String phase, Collection ids) throws RPCException, ApplyPolicyException, ItemNotFoundException;

    /**
     * @gwt.typeArgs <org.mule.galaxy.web.rpc.WActivity>
     */
    Collection getActivities(Date from, Date to, String user, String eventType, int start, int results, boolean ascending) throws RPCException;
    
    WUser getUserInfo() throws RPCException;
}
