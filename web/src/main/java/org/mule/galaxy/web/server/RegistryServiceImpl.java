/*
 * $Id: ContextPathResolver.java 794 2008-04-23 22:23:10Z andrew $
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

package org.mule.galaxy.web.server;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.Links;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.activity.Activity;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.activity.ActivityManager.EventType;
import org.mule.galaxy.artifact.ArtifactType;
import org.mule.galaxy.artifact.ArtifactTypeDao;
import org.mule.galaxy.collab.Comment;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.impl.artifact.ArtifactExtension;
import org.mule.galaxy.impl.artifact.UploadService;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.impl.lifecycle.LifecycleExtension;
import org.mule.galaxy.impl.link.LinkExtension;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.index.IndexManager;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.query.OpRestriction.Operator;
import org.mule.galaxy.render.ItemRenderer;
import org.mule.galaxy.render.RendererManager;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.SecurityUtils;
import org.mule.galaxy.util.UserUtils;
import org.mule.galaxy.view.ArtifactViewManager;
import org.mule.galaxy.view.View;
import org.mule.galaxy.web.GwtFacet;
import org.mule.galaxy.web.WebManager;
import org.mule.galaxy.web.client.RPCException;
import org.mule.galaxy.web.rpc.ApplicationInfo;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.LinkInfo;
import org.mule.galaxy.web.rpc.PluginTabInfo;
import org.mule.galaxy.web.rpc.RegistryService;
import org.mule.galaxy.web.rpc.SearchPredicate;
import org.mule.galaxy.web.rpc.WActivity;
import org.mule.galaxy.web.rpc.WApprovalMessage;
import org.mule.galaxy.web.rpc.WArtifactType;
import org.mule.galaxy.web.rpc.WArtifactView;
import org.mule.galaxy.web.rpc.WComment;
import org.mule.galaxy.web.rpc.WExtensionInfo;
import org.mule.galaxy.web.rpc.WIndex;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WLinks;
import org.mule.galaxy.web.rpc.WPhase;
import org.mule.galaxy.web.rpc.WPolicy;
import org.mule.galaxy.web.rpc.WPolicyException;
import org.mule.galaxy.web.rpc.WProperty;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;
import org.mule.galaxy.web.rpc.WSearchResults;
import org.mule.galaxy.web.rpc.WType;
import org.mule.galaxy.web.rpc.WUser;

public class RegistryServiceImpl implements RegistryService {

    protected static final String DEFAULT_DATETIME_FORMAT = "h:mm a, MMMM d, yyyy";

    private static final String RECENT_VIEWS = "recent.artifactViews";

    private final Log log = LogFactory.getLog(getClass());

    private Registry registry;
    private ArtifactTypeDao artifactTypeDao;
    private RendererManager rendererManager;
    private PolicyManager policyManager;
    private IndexManager indexManager;
    private ActivityManager activityManager;
    private AccessControlManager accessControlManager;
    private ArtifactViewManager artifactViewManager;
    private TypeManager typeManager;
    private UserManager userManager;

    private ContextPathResolver contextPathResolver;

    private LifecycleManager localLifecycleManager;

    private EventManager eventManager;

    private UploadService uploadService;
    
    private WebManager webManager;
    
    
    public ApplicationInfo getApplicationInfo() throws RPCException {
        ApplicationInfo info = new ApplicationInfo();
        info.setPluginTabs(getPluginTabs());
        info.setUser(getUserInfo());
        info.setUserManagementSupported(userManager.isManagementSupported());
        return info;
    }

    protected Collection<PluginTabInfo> getPluginTabs() {
        Collection<GwtFacet> facets = webManager.getGwtFacets();
        ArrayList<PluginTabInfo> wPlugins = new ArrayList<PluginTabInfo>();
        for (GwtFacet p : facets) {
            if (!p.getName().equals("core")) {
                PluginTabInfo wp = new PluginTabInfo();
                wp.setName(p.getName());
                wp.setToken(p.getToken());
                wPlugins.add(wp);
            }
        }
        return wPlugins;
    }

    public List<WExtensionInfo> getExtensions() throws RPCException {
        ArrayList<WExtensionInfo> exts = new ArrayList<WExtensionInfo>();
        for (Extension e : registry.getExtensions()) {
            exts.add(new WExtensionInfo(e.getId(), e.getName(), e.getPropertyDescriptorConfigurationKeys(), e.isMultivalueSupported()));
        }
        return exts;
    }

    public Collection<ItemInfo> getItems(String parentId, boolean traverseUpParents) throws RPCException {
        try {
            if (parentId == null) {
                Collection<Item> items = registry.getItems();
                
                return toWeb(items, false);
            } else {
                Item w = (Item) registry.getItemById(parentId);

                Collection<ItemInfo> workspaces = null;
                if (traverseUpParents) {
                    while (w != null) {
                        Item parent = w.getParent();
                        Collection<ItemInfo> parentWorkspaces;
                        if (parent != null) {
                            parentWorkspaces = toWeb(parent.getItems(), false);
                        } else {
                            parentWorkspaces = toWeb(registry.getItems(), false);
                        }
                        
                        if (workspaces != null) {
                            addWorkspaces(w.getName(), parentWorkspaces, workspaces);
                        }
                        workspaces = parentWorkspaces;
                        w = parent;
                    }
                } else {
                    workspaces = toWeb(w.getItems(), false);
                }
                return workspaces;
            }
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public Collection<ItemInfo> getItemsWithAllChildren(String parentPath) throws RPCException {
        return getItemsInPath(parentPath, true);
    }

    public Collection<ItemInfo> getItemsInPath(String parentPath) throws RPCException {
        return getItemsInPath(parentPath, false);
    }

    private Collection<ItemInfo> getItemsInPath(String parentPath, boolean populateChildren) throws RPCException {
        try {
            if (parentPath == null || "".equals(parentPath) || "/".equals(parentPath)) {
                return toWeb(registry.getItems(), populateChildren);
            } else {
                return toWeb(registry.getItemByPath(parentPath).getItems(), populateChildren);
            }
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    
    private Collection<ItemInfo> toWeb(Collection<Item> workspaces, boolean populateChildren) throws RegistryException {
        if (workspaces == null) {
            return null;
        }
        
        List<ItemInfo> wis = new ArrayList<ItemInfo>();
        for (Item w : workspaces) {
            if (!w.isInternal()) {
                ItemInfo ww = toWeb(w, populateChildren);
                wis.add(ww);
            }
        }
        
        return wis;
    }

    private void addWorkspaces(String name, Collection<ItemInfo> parents, Collection<ItemInfo> children) {
        for (ItemInfo w : parents) {
            if (name.equals(w.getName())) {
                w.setItems(children);
                return;
            }
        }
    }
    
    private ItemInfo toWeb(Item i, boolean populateChildren) throws RegistryException {
        ItemInfo ii = new ItemInfo();
        ii.setId(i.getId());
        ii.setName(i.getName());
        ii.setPath(i.getPath());
        if (i.getParent() != null) {
            ii.setParentPath(i.getParent().getPath());
        }
        ii.setLocal(i.isLocal());
        if (i.getAuthor() != null) {
            ii.setAuthorName(i.getAuthor().getName());
            ii.setAuthorUsername(i.getAuthor().getUsername());
        } else {
            ii.setAuthorName("[Removed]");
            ii.setAuthorName("[Removed]");
        }
        ii.setType(i.getType().getName());
        
        if (populateChildren) {
            Collection<ItemInfo> children = toWeb(i.getItems(), populateChildren);
            ii.setItems(children);
        }
        return ii;
    }

    public String addVersionedItem(String parentPath, 
                                   String name, 
                                   String versionName, 
                                   String lifecycleId,
                                   String typeId, 
                                   String versionTypeId, 
                                   Map<String, Serializable> properties,
                                   Map<String, Serializable> versionProperties) throws RPCException,
        ItemNotFoundException, ItemExistsException, WPolicyException {
        addItem(parentPath, name, lifecycleId, typeId, properties);
        
        if (!parentPath.endsWith("/")) {
            parentPath += "/";
        }
        parentPath += name;
        
        return addItem(parentPath, versionName, lifecycleId, versionTypeId, versionProperties);
    }

    public String addItem(String parentPath, 
                          String itemName, 
                          String lifecycleId, 
                          String typeId, 
                          Map<String, Serializable> properties) 
        throws RPCException, ItemNotFoundException, ItemExistsException, WPolicyException {
        
        // If we uploaded files, lets track them so we can delete them
        ArrayList<String> filesToDelete = new ArrayList<String>();
        
        try {
            Item item;
            Type type = typeManager.getType(typeId);
            Map<String, Object> localProperties = new HashMap<String, Object>();
            if (properties != null) {
                for (Map.Entry<String, Serializable> e : properties.entrySet()) {
                    String name = e.getKey();
                    PropertyDescriptor pd = typeManager.getPropertyDescriptorByName(name);
                    
                    localProperties.put(name, getLocalValue(pd, e.getValue(), null));
                    
                    if (pd != null && pd.getExtension() instanceof ArtifactExtension) {
                        filesToDelete.add((String)e.getValue());
                    }
                }
            }
            
            if (parentPath == null || "".equals(parentPath) || "/".equals(parentPath)) {
                item = registry.newItem(itemName, type, localProperties).getItem();
            } else {
                Item parent = (Item) registry.getItemByPath(parentPath);
                
                if (parent == null) {
                    throw new RPCException("Could not find parent workspace: " + parentPath);
                }
                item = parent.newItem(itemName, type, localProperties).getItem();
            }
            if (lifecycleId != null) {
                item.setDefaultLifecycle(item.getLifecycleManager().getLifecycleById(lifecycleId));
                registry.save(item);
            }
            
            return item.getId();
        } catch (DuplicateItemException e) {
            throw new ItemExistsException();
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (PolicyException e) {
            throw toWeb(e);
        } catch (PropertyException e) {
            throw new RPCException(e.getMessage());
        } finally {
            for (String s : filesToDelete) {
                uploadService.delete(s);
            }
        }
    }

    public Collection<WArtifactType> getArtifactTypes() {
        Collection<ArtifactType> artifactTypes = artifactTypeDao.listAll();
        List<WArtifactType> atis = new ArrayList<WArtifactType>();

        for (ArtifactType a : artifactTypes) {
            WArtifactType at = toWeb(a);
            atis.add(at);
        }
        return atis;
    }

    public WArtifactType getArtifactType(String id) throws RPCException {
        try {
            return toWeb(artifactTypeDao.get(id));
        } catch (Exception e) {
            throw new RPCException(e.getMessage());
        }
    }

    private WArtifactType toWeb(ArtifactType a) {
        Set<QName> docTypes = a.getDocumentTypes();
        List<String> docTypesAsStr = new ArrayList<String>();
        if (docTypes != null) {
            for (QName q : docTypes) {
                docTypesAsStr.add(q.toString());
            }
        }
        return new WArtifactType(a.getId(), a.getContentType(),
                                 a.getDescription(), docTypesAsStr,
                                 a.getFileExtensions());
    }

    public void deleteArtifactType(String id) throws RPCException {
        try {
            artifactTypeDao.delete(id);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }
    }

    public void saveArtifactType(WArtifactType artifactType) throws RPCException, ItemExistsException {
        try {
            ArtifactType at = fromWeb(artifactType);
            artifactTypeDao.save(at);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (DuplicateItemException e) {
            throw new ItemExistsException();
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    private ArtifactType fromWeb(WArtifactType wat) {
        ArtifactType at = new ArtifactType();
        at.setId(wat.getId());
        at.setDescription(wat.getDescription());
        at.setContentType(wat.getMediaType());
        at.setDocumentTypes(fromWeb(wat.getDocumentTypes()));

        HashSet<String> exts = new HashSet<String>();
        exts.addAll(wat.getFileExtensions());
        at.setFileExtensions(exts);

        return at;
    }

    private Set<QName> fromWeb(Collection<String> documentTypes) {
        if (documentTypes == null) return null;

        Set<QName> s = new HashSet<QName>();
        for (Object o : documentTypes) {
            String qn = o.toString();
            if (qn.startsWith("{}")) {
                qn = qn.substring(2);
            }

            s.add(QName.valueOf(qn));
        }
        return s;
    }

    private OpRestriction getRestrictionForPredicate(SearchPredicate pred) {
        String property = pred.getProperty();
        String value = pred.getValue();
        switch (pred.getMatchType()) {
            case SearchPredicate.HAS_VALUE:
                return OpRestriction.eq(property, value);
            case SearchPredicate.LIKE:
                return OpRestriction.like(property, value);
            case SearchPredicate.DOES_NOT_HAVE_VALUE:
                return OpRestriction.not(OpRestriction.eq(property, value));
            default:
                return null;
        }
    }

    public WSearchResults getArtifacts(String workspaceId, 
                                       String workspacePath, 
                                       boolean includeChildWkspcs,
                                       Set<SearchPredicate> searchPredicates, 
                                       String freeformQuery,
                                       int start, int maxResults) throws RPCException {
        Query q = getQuery(searchPredicates, start, maxResults);

        final String context = contextPathResolver.getContextPath();
        
        try {
            if (workspaceId != null) {
                Item workspace = ((Item)registry.getItemById(workspaceId));
                List<Item> items = workspace.getItems();
                List<Item> trimmedItems = new ArrayList<Item>();
                for (int i = start; i < start+maxResults && i < items.size(); i++) {
                    trimmedItems.add(items.get(i));
                }
                WSearchResults results = getSearchResults(null, trimmedItems, items.size());
                results.setQuery("select artifact, entry from '@" + workspaceId + "'");
                results.setFeed(getLink(context + "/api/registry", workspace));
                results.setTotal(items.size());
                return results;
            } else if (workspacePath != null && !"".equals(workspacePath) && !"/".equals(workspacePath)) {
                q.fromPath(workspacePath, includeChildWkspcs);
            }
            
            SearchResults results;
            if (freeformQuery != null && !freeformQuery.equals(""))
                results = registry.search(freeformQuery, start, maxResults);
            else
                results = registry.search(q);

            WSearchResults wr = getSearchResults(null, results.getResults(), results.getTotal());
            wr.setQuery(q.toString());
            wr.setFeed(context + "/api/registry?q=" + UrlEncoding.encode(wr.getQuery(), Profile.PATH.filter()));
            return wr;
        } catch (QueryException e) {
            throw new RPCException(e.getMessage());
        } catch (RegistryException e) {
            log.error("Could not query the registry.", e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public Collection<ItemInfo> suggestItems(String query, boolean recursive, String excludePath, String[] types) throws RPCException {
        try {
            SearchResults results = registry.suggest(query, recursive, 10, excludePath, types);
            
            ArrayList<ItemInfo> entries = new ArrayList<ItemInfo>();
            for (Item i : results.getResults()) {
                entries.add(toWeb(i, false));
            }
            return entries;
        } catch (QueryException e) {
            throw new RPCException(e.getMessage());
        } catch (RegistryException e) {
            log.error("Could not query the registry.", e);
            throw new RPCException(e.getMessage());
        }
    }
    
    private Query getQuery(Set<SearchPredicate> searchPredicates, int start, int maxResults) {
        Query q = new Query().orderBy("name");

        q.setMaxResults(maxResults);
        q.setStart(start);
        // Filter based on our search terms

        if (searchPredicates != null) {
            for (Object predObj : searchPredicates) {
                SearchPredicate pred = (SearchPredicate) predObj;
                q.add(getRestrictionForPredicate(pred));
            }
        }
        return q;
    }

    private WSearchResults getSearchResults(String type, 
                                            Collection<? extends Item> results,
                                            long total) throws RegistryException {

        WSearchResults wsr = new WSearchResults();
        ItemRenderer view;
//        if (type != null) {
//            view = rendererManager.getRenderer(type);
//        } else {
            view = rendererManager.getDefaultRenderer();
//        }


        int col = 0;
        for (String colName : view.getColumnNames()) {
            if (view.isSummary(col)) {
                wsr.getColumns().add(colName);
            }
            col++;
        }
        
        for (Item i : results) {
            wsr.getRows().add(toWeb(i, false));
        }

        wsr.setTotal(total);
        return wsr;
    }

    public WSearchResults getArtifactsForView(String viewId,
                                              int resultStart,
                                              int maxResults)
            throws RPCException {
        try {
            View view = artifactViewManager.getArtifactView(viewId);
            SearchResults result = registry.search(view.getQuery(), resultStart, maxResults);
            return getSearchResults(null, result.getResults(), result.getTotal());
        } catch (QueryException e) {
            throw new RPCException(e.getMessage());
        } catch (RegistryException e) {
            log.error("Could not query the registry.", e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void deleteArtifactView(String id) throws RPCException {
        artifactViewManager.delete(id);
        // clean up recent views too
        User user = getCurrentUser();
        List<String> views = (List<String>) user.getProperties().get(RECENT_VIEWS);
        boolean wasFound = views.remove(id);

        assert wasFound : "View deleted, but no corresponding Recent Views entry found for " + id;
    }

    public WArtifactView getArtifactView(String id) throws RPCException, ItemExistsException, ItemNotFoundException {
        User user = getCurrentUser();
        try {
            WArtifactView view = toWeb(artifactViewManager.getArtifactView(id));
            updateRecentArtifactViews(user, id);

            return view;
        } catch (DuplicateItemException e) {
            throw new ItemExistsException();
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw new ItemNotFoundException();
        }
    }

    private void updateRecentArtifactViews(User user, String id) throws DuplicateItemException, NotFoundException {
        List<String> recent = getRecentArtifactViewIds(user);

        // remove this id if it alread exists
        recent.remove(id);

        // add the view to the top of the list
        recent.add(0, id);

        while (recent.size() > 5) {
            recent.remove(recent.size() - 1);
        }

        userManager.save(user);
    }

    @SuppressWarnings("unchecked")
    private List<String> getRecentArtifactViewIds(User user) {
        if (user.getProperties() == null) {
            user.setProperties(new HashMap<String, Object>());
        }
        List<String> recent = (List<String>) user.getProperties().get(RECENT_VIEWS);

        if (recent == null) {
            recent = new ArrayList<String>();
            user.getProperties().put(RECENT_VIEWS, recent);
        }
        return recent;
    }

    public Collection<WArtifactView> getArtifactViews() throws RPCException {
        List<WArtifactView> views = new ArrayList<WArtifactView>();
        User currentUser = getCurrentUser();
        for (View v : artifactViewManager.getArtifactViews(currentUser)) {
            views.add(toWeb(v));
        }

        Collections.sort(views, new Comparator<WArtifactView>() {
            public int compare(WArtifactView v1, WArtifactView v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });
        return views;
    }

    public Collection<WArtifactView> getRecentArtifactViews() throws RPCException {
        List<WArtifactView> views = new ArrayList<WArtifactView>();
        User currentUser = getCurrentUser();
        List<String> ids = getRecentArtifactViewIds(currentUser);
        if (ids != null) {
            for (String id : ids) {
                try {
                    views.add(toWeb(artifactViewManager.getArtifactView(id)));
                } catch (NotFoundException e) {
                }
            }
        }
        return views;
    }

    private WArtifactView toWeb(View v) throws RPCException {
        WArtifactView wv = new WArtifactView();
        if (v == null) {
            return wv;
        }
        wv.setName(v.getName());
        wv.setId(v.getId());

        try {
            if (v.isFreeform()) {
                wv.setQueryString(v.getQuery());
            } else {
                Query q = Query.fromString(v.getQuery());
    
                wv.setPredicates(getPredicates(q));
                wv.setWorkspace(q.getFromPath());
                wv.setWorkspaceSearchRecursive(q.isFromRecursive());
            }
            
            wv.setShared(v.getUser() == null);
        } catch (QueryException e) {
            log.error("Could not parse query. " + e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }
        return wv;
    }

    /**
     * Convert a string query to a set of search predicates for the SearchForm.
     * You'll see that we do not have full fidelity yet between text queries and
     * the actual form. However, that is not a problem as we'll only ever encounter
     * queries which were created with the form. So there are some cases here
     * that we don't have to worry about.
     *
     * @param q
     * @return
     * @throws RPCException
     */
    public Set<SearchPredicate> getPredicates(Query q) throws RPCException {
        Set<SearchPredicate> predicates = new HashSet<SearchPredicate>();

        for (Restriction r : q.getRestrictions()) {
            if (r instanceof OpRestriction) {
                OpRestriction op = (OpRestriction) r;

                Object left = op.getLeft();
                Object right = op.getRight();
                Operator operator = op.getOperator();

                if (operator.equals(Operator.NOT)) {
                    if (right instanceof OpRestriction) {
                        OpRestriction op2 = (OpRestriction) right;

                        predicates.add(new SearchPredicate(op2.getLeft().toString(),
                                                           SearchPredicate.DOES_NOT_HAVE_VALUE,
                                                           op2.getRight().toString()));
                    } else {
                        throw new RPCException("Query could not be converted.");
                    }
                } else if (operator.equals(Operator.EQUALS)) {
                    predicates.add(new SearchPredicate(left.toString(), SearchPredicate.HAS_VALUE, right.toString()));
                } else if (operator.equals(Operator.LIKE)) {
                    predicates.add(new SearchPredicate(left.toString(), SearchPredicate.LIKE, right.toString()));
                }
            }
        }
        return predicates;
    }

    public String saveArtifactView(WArtifactView wv) throws RPCException {
        View v = fromWeb(wv);

        try {
            artifactViewManager.save(v);
            return v.getId();
        } catch (DuplicateItemException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("Couldn't save view.");
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("The view being saved has been deleted.");
        }
    }

    private View fromWeb(WArtifactView wv) throws RPCException {
        View v = new View();
        v.setId(wv.getId());
        v.setName(wv.getName());
        if (!wv.isShared()) {
            v.setUser(getCurrentUser());
        }
        
        if (wv.getQueryString() != null && !"".equals(wv.getQueryString())) {
            v.setQuery(wv.getQueryString());
            v.setFreeform(true);
        } else {
            Query query = getQuery(wv.getPredicates(), 0, 0);
            query.fromPath(wv.getWorkspace(), wv.isWorkspaceSearchRecursive());
    
            v.setQuery(query.toString());
            v.setFreeform(false);
        }
        
        return v;
    }

    public Collection<WIndex> getIndexes() {
        ArrayList<WIndex> windexes = new ArrayList<WIndex>();

        Collection<Index> indices = indexManager.getIndexes();
        for (Index idx : indices) {
            windexes.add(toWeb(idx));
        }

        return windexes;
    }

    private WIndex toWeb(Index idx) {

        ArrayList<String> docTypes = new ArrayList<String>();
        if (idx.getDocumentTypes() != null) {
            for (QName q : idx.getDocumentTypes()) {
                docTypes.add(q.toString());
            }
        }
        String qt;
        if (String.class.equals(idx.getQueryType())) {
            qt = "String";
        } else {
            qt = "QName";
        }

        return new WIndex(idx.getId(),
                          idx.getDescription(),
                          idx.getMediaType(),
                          idx.getConfiguration().get("property"),
                          idx.getConfiguration().get("expression"),
                          idx.getIndexer(),
                          qt,
                          docTypes);
    }

    public WIndex getIndex(String id) throws RPCException {
        try {
            Index idx = indexManager.getIndex(id);
            if (idx == null) {
                return null;
            }
            return toWeb(idx);
        } catch (Exception e) {
            throw new RPCException("Could not find index " + id);
        }
    }

    public void saveIndex(WIndex wi) throws RPCException {
        try {
            Index idx = fromWeb(wi);

            indexManager.save(idx);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RPCException("Couldn't save index.");
        }
    }

    public void deleteIndex(String id, boolean removeArtifactMetadata) throws RPCException {
        try {
            indexManager.delete(id, removeArtifactMetadata);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RPCException("Couldn't save index.");
        }
    }

    private Index fromWeb(WIndex wi) throws RPCException {
        Index idx = new Index();
        idx.setId(wi.getId());
        idx.setConfiguration(new HashMap<String, String>());
        idx.setIndexer(wi.getIndexer());

        if (idx.getIndexer().contains("Groovy")) {
            idx.getConfiguration().put("script", wi.getExpression());
        } else {
            idx.getConfiguration().put("property", wi.getProperty());
            idx.getConfiguration().put("expression", wi.getExpression());
        }
        idx.setIndexer(wi.getIndexer());
        idx.setDescription(wi.getDescription());
        idx.setMediaType(wi.getMediaType());

        if (wi.getResultType().equals("String")) {
            idx.setQueryType(String.class);
        } else {
            idx.setQueryType(QName.class);
        }

        Set<QName> docTypes = new HashSet<QName>();
        for (Object o : wi.getDocumentTypes()) {
            try {
                docTypes.add(QName.valueOf(o.toString()));
            } catch (IllegalArgumentException e) {
                throw new RPCException("QName was formatted incorrectly: " + o.toString());
            }
        }
        idx.setDocumentTypes(docTypes);
        return idx;
    }

    public boolean itemExists(String path) throws RPCException {
        try {
            registry.getItemByPath(path);
            return true;
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            return false;
        } catch (AccessException e) {
            // Not sure if this is the right thing to do, but
            // I think we should err on the side of not showing what
            // is in the registry
            return false;
        }
            
    }

    public WLinks getLinks(String itemId, String property) throws RPCException {
        try {
            Item item = registry.getItemById(itemId);
            Links links = item.getProperty(property);
            PropertyDescriptor pd = typeManager.getPropertyDescriptor(property);
            
            return toWeb(links, pd);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }

    }

    private WLinks toWeb(Links links, PropertyDescriptor pd) {
        if (links == null) {
            return null;
        }
        
        WLinks wlinks = new WLinks();
        
        List<LinkInfo> deps = new ArrayList<LinkInfo>();
        
        for (Link l : links.getLinks()) {
            deps.add(toWeb(l, false));
        }
        wlinks.setLinks(deps);
        
        deps = new ArrayList<LinkInfo>();
        for (Link l : links.getReciprocalLinks()) {
            deps.add(toWeb(l, true));
        }
        wlinks.setReciprocal(deps);
        
        
        wlinks.setReciprocalName(pd.getConfiguration().values().iterator().next());
        
        return wlinks;
    }
    
    private LinkInfo toWeb(Link l, boolean recip) {
        Item i = recip ? l.getItem() : l.getLinkedTo();
        String name;
        int itemType;
        String id = null;

        if (i != null) {
            itemType = LinkInfo.TYPE_ENTRY;
            name = i.getPath();
            id = i.getId();
        } else if (i == null) {
            itemType = LinkInfo.TYPE_NOT_FOUND;
            name = l.getLinkedToPath();
        } else {
            throw new UnsupportedOperationException();
        }
        return new LinkInfo(l.getId(),
                            l.isAutoDetected(),
                            id,
                            name,
                            itemType,
                            recip);
    }

    private ItemInfo toWebExtended(Item e, boolean showProperties) throws RegistryException {
        ItemInfo info = toWeb(e, false);
        
        Set<Permission> permissions = accessControlManager.getPermissions(SecurityUtils.getCurrentUser(), e);
        info.setModifiable(permissions.contains(Permission.MODIFY_ITEM));
        info.setDeletable(permissions.contains(Permission.DELETE_ITEM));

        if (e.isLocal()) {
            info.setDefaultLifecycleId(e.getDefaultLifecycle().getId());
        }

        final String context = contextPathResolver.getContextPath();
        info.setArtifactFeedLink(getLink(context + "/api/registry", e) + ";history");
        info.setCommentsFeedLink(context + "/api/comments");
        
        List<WComment> wcs = info.getComments();

        CommentManager commentManager = e.getCommentManager();
        if (commentManager != null) {
            List<Comment> comments = commentManager.getComments(e.getId());
            for (Comment c : comments) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
                WComment wc = new WComment(c.getId(), 
                                           UserUtils.getUsername(c.getUser()), 
                                           dateFormat.format(c
                        .getDate().getTime()), c.getText());
                wcs.add(wc);
    
                Set<Comment> children = c.getComments();
                if (children != null && children.size() > 0) {
                    addComments(wc, children);
                }
            }
        }

        populateProperties(e, info, showProperties);
        
        return info;
    }

    public ItemInfo getItemInfo(String itemId, boolean showHidden) throws RPCException, ItemNotFoundException {
        try {
            Item item = registry.getItemById(itemId);

            return toWebExtended(item, showHidden);
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public ItemInfo getItemByPath(String path) throws RPCException, ItemNotFoundException {
        try {
            Item item = registry.getItemByPath(path);
            return toWeb(item, false);
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void populateProperties(Item item, ItemInfo vi, boolean showHidden) {
        for (PropertyInfo p : item.getProperties()) {
            if (!showHidden && !p.isVisible()) {
                continue;
            }

            PropertyDescriptor pd = p.getPropertyDescriptor();
            Extension ext = pd != null ? pd.getExtension() : null;
            
            Object val = toWeb(item, p, ext);

            String desc = p.getDescription();
            if (desc == null) {
                desc = p.getName();
            }
            
            String extId = ext != null ? ext.getId() : null;
            
            vi.getProperties().add(new WProperty(p.getName(), 
                                                 desc, 
                                                 (Serializable) val, 
                                                 extId, 
                                                 p.isLocked()));
        }

        Collections.sort(vi.getProperties(), new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((WProperty) o1).getDescription().compareTo(((WProperty) o2).getDescription());
            }

        });
    }

    private Object toWeb(Item item, PropertyInfo p, Extension ext) {
        if (ext instanceof LinkExtension) {
            Links links = p.getValue();
            
            return toWeb(links, p.getPropertyDescriptor());
        } else if (ext instanceof ArtifactExtension) {
            return getLink(contextPathResolver.getContextPath() + "/api/registry", item.getParent()) 
                + "?version=" + item.getName();
        } else {
            Object internalValue = p.getInternalValue();
            
            return convertQNames(internalValue);
        }
    }

    /**
     * This method is here temporarily until we can serialize qnames remotely
     * @param val
     */
    private Object convertQNames(Object val) {
        if (val instanceof Collection) {
            List<String> objs = new ArrayList<String>();
            for (Object o : (Collection) val) {
                objs.add(o.toString());
            }
            return objs;
        }
        return val;
    }

    private String getLink(String base, Item a) {
        StringBuilder sb = new StringBuilder();
        sb.append(base).append(a.getPath());
        return sb.toString();
    }

    public WComment addComment(String entryId, String parentComment, String text) throws RPCException, ItemNotFoundException {
        try {
            Item item = registry.getItemById(entryId);

            Comment comment = new Comment();
            comment.setText(text);

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            comment.setDate(cal);

            comment.setUser(getCurrentUser());

            Item w = (Item)item.getParent();
            CommentManager commentManager = w.getCommentManager();
            if (parentComment != null) {
                Comment c = commentManager.getComment(parentComment);
                if (c == null) {
                    throw new RPCException("Invalid parent comment");
                }
                comment.setParent(c);
            } else {
                comment.setItem(item);
            }
            commentManager.addComment(comment);

            SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
            return new WComment(comment.getId(), 
                                UserUtils.getUsername(comment.getUser()), 
                                dateFormat.format(comment.getDate().getTime()), 
                                comment.getText());
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
    }

    private User getCurrentUser() throws RPCException {
        UserDetailsWrapper wrapper = (UserDetailsWrapper) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (wrapper == null) {
            throw new RPCException("No user is logged in!");
        }
        return wrapper.getUser();
    }

    private void addComments(WComment parent, Set<Comment> comments) {
        for (Comment c : comments) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
            WComment child = new WComment(c.getId(), UserUtils.getUsername(c.getUser()), dateFormat.format(c.getDate()
                    .getTime()), c.getText());
            parent.getComments().add(child);

            Set<Comment> children = c.getComments();
            if (children != null && children.size() > 0) {
                addComments(child, children);
            }
        }
    }

    public void setProperty(String itemId, String propertyName, Serializable propertyValue) throws RPCException, ItemNotFoundException, WPolicyException {
        try {
            Item item = registry.getItemById(itemId);

            PropertyDescriptor pd = typeManager.getPropertyDescriptorByName(propertyName);
            
            Object value = getLocalValue(pd, propertyValue, item);
            
            item.setProperty(pd.getProperty(), value);
            
            registry.save(item);
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (PropertyException e) {
            // occurs if property name is formatted wrong
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (PolicyException e) {
            throw toWeb(e);
        }
    }

    public Object getLocalValue(PropertyDescriptor pd, 
                                Serializable s, 
                                Item item) 
        throws NotFoundException, RegistryException, AccessException, RPCException {
        if (pd != null && pd.getExtension() != null) {
            Extension ext = pd.getExtension();
            if (ext instanceof LinkExtension) {
                Links links = item.getProperty(pd.getProperty());
                WLinks wlinks = (WLinks) s;
                
                Collection<Link> linksToRemove = new ArrayList<Link>();
                Collection<Link> linksToAdd = new ArrayList<Link>();
                if (links != null) {
                    linksToRemove.addAll(links.getLinks());
                }
                for (Iterator<LinkInfo> itr = wlinks.getLinks().iterator(); itr.hasNext();) {
                    LinkInfo wl = itr.next();
                    Link l = getLink(linksToRemove, wl);
                    
                    if (l != null) {
                        linksToRemove.remove(l);
                    } else {
                        Item linkTo = registry.getItemByPath(wl.getItemName());
                        
                        Link link = new Link(item, linkTo, null, false);
                        linksToAdd.add(link);
                    }
                }
                
                if (links != null) {
                    for (Link l : linksToRemove) {
                        links.removeLinks(l);
                    }
                    for (Link l : linksToAdd) {
                        links.addLinks(l);
                    }
                }
                
                return linksToAdd;
            } else if (ext instanceof ArtifactExtension) {
                try {
                    return new Object[] { uploadService.getFile(s.toString()),
                                          "application/octet-stream" };
                } catch (FileNotFoundException e) {
                    throw new RPCException("An error occurred processing the file upload. Please try again.");
                }
            } else if (ext instanceof LifecycleExtension) {
                List ids = (List) s;
                
                if (ids.size() != 2) {
                    throw new RPCException("Lifecycle metadata is wrong length!");
                }
                
                LifecycleManager lifecycleManager;
                if (item != null) {
                    lifecycleManager = item.getLifecycleManager();
                } else {
                    lifecycleManager = localLifecycleManager;
                }
                
                return (Phase) lifecycleManager.getPhaseById((String)ids.get(1));
            }
        } 
        return s;
    }
    
    private Link getLink(Collection<Link> linkList, LinkInfo l) {
        for (Link link : linkList) {
            String path = link.getLinkedToPath();
            if (link.getId().equals(l.getLinkId())
                || (path != null && path.equals(l.getItemName()))) {
                return link;
            }
        }
        return null;
    }

    public void setPropertyForQuery(String query,
                                    String propertyName, 
                                    Serializable propertyValue)
        throws RPCException, ItemNotFoundException, WPolicyException {
        try {
            SearchResults results = registry.search(query, 0, -1);
            List<Item> items = new ArrayList<Item>();
            for (Item item : results.getResults()) {
                PropertyDescriptor pd = typeManager.getPropertyDescriptorByName(propertyName);
                Extension ext = pd != null ? pd.getExtension() : null;
                
                setProperty(item, pd, propertyValue, ext, items);
            }
            
            // don't save until we actually manage to set everything
            for (Item i : items) {
                registry.save(i);
            }
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (PropertyException e) {
            // occurs if property name is formatted wrong
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (PolicyException e) {
            throw toWeb(e);
        }
    }

    public void setProperty(Collection<String> entryIds,
                            String propertyName, 
                            Serializable propertyValue)
        throws RPCException, ItemNotFoundException, WPolicyException {
        try {
            List<Item> items = new ArrayList<Item>();
            for (String itemId : entryIds) {
                Item item = registry.getItemById(itemId);

                PropertyDescriptor pd = typeManager.getPropertyDescriptorByName(propertyName);
                Extension ext = pd != null ? pd.getExtension() : null;
                
                setProperty(item, pd, propertyValue, ext, items);
            }
            
            // don't save until we actually manage to set everything
            for (Item i : items) {
                registry.save(i);
            }
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (PropertyException e) {
            // occurs if property name is formatted wrong
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (PolicyException e) {
            throw toWeb(e);
        }
    }

    private void setProperty(Item item, 
                             PropertyDescriptor pd, 
                             Serializable propertyValue, 
                             Extension ext,
                             List<Item> items) throws PropertyException, PolicyException, NotFoundException,
        RegistryException, AccessException, RPCException {
        Object value = getLocalValue(pd, propertyValue, item);
        
        item.setProperty(pd.getProperty(), value);
        items.add(item);
    }

    public void deleteProperty(String itemId, String propertyName) throws RPCException, ItemNotFoundException {
        try {
            Item item = registry.getItemById(itemId);
            item.setProperty(propertyName, null);
            registry.save(item);
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (PropertyException e) {
            // occurs if property name is formatted wrong
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (PolicyException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public void deleteProperty(Collection<String> entryIds, String propertyName) throws RPCException, ItemNotFoundException {
        try {
            List<Item> items = new ArrayList<Item>();
            for (String itemId : entryIds) {
                Item item = registry.getItemById(itemId);
                
                item.setProperty(propertyName, null);
                items.add(item);
            }
            // don't save until we actually delete everything
            for (Item i : items) {
                registry.save(i);
            }
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (PropertyException e) {
            // occurs if property name is formatted wrong
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (PolicyException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public void deletePropertyForQuery(String query, String propertyName) throws RPCException, ItemNotFoundException {
        try {
            SearchResults results = registry.search(query, 0, -1);
            List<Item> items = new ArrayList<Item>();
            for (Item item : results.getResults()) {
                item.setProperty(propertyName, null);
            }
            // don't save until we actually delete everything
            for (Item i : items) {
                registry.save(i);
            }
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (PropertyException e) {
            // occurs if property name is formatted wrong
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (PolicyException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public void deletePropertyDescriptor(String id) throws RPCException {
        typeManager.deletePropertyDescriptor(id);
    }

    public WPropertyDescriptor getPropertyDescriptor(String id) throws RPCException, ItemNotFoundException {
        try {
            return toWeb(typeManager.getPropertyDescriptor(id));
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        }
    }

    public List<WPropertyDescriptor> getPropertyDescriptors(boolean includeIndex) throws RPCException {
        List<WPropertyDescriptor> pds = new ArrayList<WPropertyDescriptor>();
        for (PropertyDescriptor pd : typeManager.getGlobalPropertyDescriptors(includeIndex)) {
            pds.add(toWeb(pd));
        }
        return pds;
    }

    private WPropertyDescriptor toWeb(PropertyDescriptor pd) {
        String ext = pd.getExtension() != null ? pd.getExtension().getId() : null;
        
        WPropertyDescriptor wpd = new WPropertyDescriptor(pd.getId(), pd.getProperty(), pd.getDescription(), ext, pd.isMultivalued(), pd.getConfiguration());
        if (pd.getType() != null) {
            wpd.setTypeId(pd.getType().getId());
        }
        return wpd;
    }

    public void savePropertyDescriptor(WPropertyDescriptor wpd) throws RPCException, ItemNotFoundException, ItemExistsException {
        try {
            PropertyDescriptor pd;

            if (wpd.getId() == null) {
                pd = new PropertyDescriptor();
            } else {
                pd = typeManager.getPropertyDescriptor(wpd.getId());
            }

            pd.setProperty(wpd.getName());
            pd.setDescription(wpd.getDescription());
            pd.setMultivalued(wpd.isMultiValued());
            pd.setConfiguration(wpd.getConfiguration());
            pd.setExtension(registry.getExtension(wpd.getExtension()));
            
            typeManager.savePropertyDescriptor(pd);

            wpd.setId(pd.getId());
        } catch (DuplicateItemException e) {
            throw new ItemExistsException();
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
    }

    
    public List<WType> getTypes() throws RPCException {
        ArrayList<WType> types = new ArrayList<WType>();
        for (Type type : typeManager.getTypes()) {
            types.add(toWeb(type));
        }
        return types;
    }

    public WType getType(String id) throws RPCException {
        try {
            return toWeb(typeManager.getType(id));
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public void saveType(WType wt) throws RPCException, ItemExistsException {
        try {
            Type type = fromWeb(wt);
            
            // have to do two phases of saving. First, get a type ID
            // then use that type ID on any property that was created
            List<PropertyDescriptor> props = type.getProperties();
            type.setProperties(null);
            
            typeManager.saveType(type);
            
            for (PropertyDescriptor pd : props) {
                typeManager.savePropertyDescriptor(pd);
            }
            
            type.setProperties(props);
            typeManager.saveType(type);
            
            wt.setId(type.getId());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (DuplicateItemException e) {
            throw new ItemExistsException();
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    private Type fromWeb(WType wt) throws NotFoundException {
        Type type = new Type();
        type.setId(wt.getId());
        type.setName(wt.getName());
        type.setAllowedChildren(fromWebToTypes(wt.getAllowedChildrenIds()));
        type.setMixins(fromWebToTypes(wt.getMixinIds()));
        
        List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
        for (WPropertyDescriptor wpd : wt.getProperties()) {
            PropertyDescriptor pd;
            if (wpd.getId() != null  && !"new".equals(wpd.getId())) {
                pd = typeManager.getPropertyDescriptor(wpd.getId());
            } else {
                pd = new PropertyDescriptor();
                pd.setMultivalued(wpd.isMultiValued());
                pd.setConfiguration(wpd.getConfiguration());
                pd.setExtension(registry.getExtension(wpd.getExtension()));
            }
            
            pd.setProperty(wpd.getName());
            pd.setDescription(wpd.getDescription());
            
            properties.add(pd);
        }
        type.setProperties(properties);
        
        return type;
    }

    private List<Type> fromWebToTypes(List<String> allowedChildrenIds) 
        throws NotFoundException {
        List<Type> types = new ArrayList<Type>();
        for (String id : allowedChildrenIds) {
            types.add(typeManager.getType(id));
        }
        return types;
    }
    
    private List<String> toWeb(List<Type> children) {
        if (children == null) return Collections.emptyList();
        
        List<String> types = new ArrayList<String>();
        for (Type t : children) {
            types.add(t.getId());
        }
        return types;
    }

    private WType toWeb(Type type) {
        WType wt = new WType();
        wt.setId(type.getId());
        wt.setName(type.getName());
        ArrayList<WPropertyDescriptor> pds = new ArrayList<WPropertyDescriptor>();
        if (type.getProperties() != null) {
            for (PropertyDescriptor pd : type.getProperties()) {
                pds.add(toWeb(pd));
            }
        }
        wt.setProperties(pds);
        wt.setAllowedChildrenIds(toWeb(type.getAllowedChildren()));
        wt.setMixinIds(toWeb(type.getMixins()));
        wt.getMixinIds().remove(type.getId());
        wt.setSystem(type.isSystemType());
        
        return wt;
    }

    public Map<String, String> getQueryProperties() throws RPCException {
        return registry.getQueryProperties();
    }
    
    public void move(String itemId, String workspacePath, String name) 
        throws RPCException, ItemNotFoundException, WPolicyException {
        try {
            Item i = registry.getItemById(itemId);
            
            registry.move(i, workspacePath, name);
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (PolicyException e) {
            throw toWeb(e);
        } catch (PropertyException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public void delete(String itemId) throws RPCException, ItemNotFoundException {
        try {
            Item item = registry.getItemById(itemId);

            item.delete();
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public void delete(List<String> ids) throws RPCException, ItemNotFoundException {
        try {
            for (String id : ids) {
                Item item = registry.getItemById(id);
    
                item.delete();
            }
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public void transition(Collection<String> entryIds, String lifecycle, String phase) throws RPCException, ItemNotFoundException {
        
    }

    public Collection<WLifecycle> getLifecycles() throws RPCException {
        Collection<Lifecycle> lifecycles = localLifecycleManager.getLifecycles();
        Lifecycle defaultLifecycle = localLifecycleManager.getDefaultLifecycle();

        ArrayList<WLifecycle> wls = new ArrayList<WLifecycle>();
        for (Lifecycle l : lifecycles) {
            WLifecycle lifecycle = toWeb(l, defaultLifecycle.equals(l));
            wls.add(lifecycle);
        }

        return wls;
    }

    public WLifecycle getLifecycle(String id) throws RPCException {
        try {
            Lifecycle defaultLifecycle = localLifecycleManager.getDefaultLifecycle();

            Lifecycle l = localLifecycleManager.getLifecycleById(id);

            return toWeb(l, defaultLifecycle.equals(l));
        } catch (Exception e) {
            throw new RPCException(e.getMessage());
        }
    }

    private WLifecycle toWeb(Lifecycle l, boolean defaultLifecycle) {
        WLifecycle lifecycle = new WLifecycle(l.getId(), l.getName(), defaultLifecycle);

        List<WPhase> wphases = new ArrayList<WPhase>();
        lifecycle.setPhases(wphases);

        for (Phase p : l.getPhases().values()) {
            WPhase wp = toWeb(p);
            wphases.add(wp);

            if (p.equals(l.getInitialPhase())) {
                lifecycle.setInitialPhase(wp);
            }
        }

        for (Phase p : l.getPhases().values()) {
            WPhase wp = lifecycle.getPhase(p.getName());
            List<WPhase> nextPhases = new ArrayList<WPhase>();

            for (Phase next : p.getNextPhases()) {
                WPhase wnext = lifecycle.getPhase(next.getName());

                nextPhases.add(wnext);
            }
            wp.setNextPhases(nextPhases);
        }

        Collections.sort(wphases, new Comparator<WPhase>() {

            public int compare(WPhase o1, WPhase o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
        return lifecycle;
    }

    private WPhase toWeb(Phase p) {
        WPhase wp = new WPhase(p.getId(), p.getName());
        return wp;
    }

    public Collection<String> getActivePoliciesForLifecycle(String lifecycleName, String workspaceId)
            throws RPCException {
        Collection<Policy> pols;
        Lifecycle lifecycle = localLifecycleManager.getLifecycle(lifecycleName);
        try {
            if (workspaceId != null) {
                Item w = (Item) registry.getItemById(workspaceId);
                pols = policyManager.getActivePolicies(w, lifecycle);
            } else {
                pols = policyManager.getActivePolicies(lifecycle);
            }
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
        return getArtifactPolicyIds(pols);
    }

    public Collection<String> getActivePoliciesForPhase(String lifecycle, 
                                                        String phaseName, 
                                                        String itemId)
            throws RPCException {
        Collection<Policy> pols;
        Phase phase = localLifecycleManager.getLifecycle(lifecycle).getPhase(phaseName);
        try {
            if (itemId != null) {
                Item w = (Item) registry.getItemById(itemId);
                pols = policyManager.getActivePolicies(w, phase);
            } else {
                pols = policyManager.getActivePolicies(phase);
            }
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }

        return getArtifactPolicyIds(pols);
    }

    public void setActivePolicies(String itemId, 
                                  String lifecycle, 
                                  String phase, Collection<String> ids)
            throws RPCException, WPolicyException, ItemNotFoundException {
        Lifecycle l = localLifecycleManager.getLifecycle(lifecycle);
        List<Policy> policies = getArtifactPolicies(ids);

        try {
            if (phase != null) {
                Phase p = l.getPhase(phase);

                if (p == null) {
                    throw new RPCException("Invalid phase: " + phase);
                }

                List<Phase> phases = Arrays.asList(p);

                if (itemId == null || "".equals(itemId)) {
                    policyManager.setActivePolicies(phases, policies.toArray(new Policy[policies
                            .size()]));
                } else {
                    Item w = (Item) registry.getItemById(itemId);
                    policyManager.setActivePolicies(w, phases, policies.toArray(new Policy[policies
                            .size()]));
                }
            } else {
                if (itemId == null || "".equals(itemId)) {
                    policyManager.setActivePolicies(l, policies.toArray(new Policy[policies.size()]));
                } else {
                    Item w = (Item) registry.getItemById(itemId);
                    policyManager.setActivePolicies(w, l, policies
                            .toArray(new Policy[policies.size()]));
                }
            }
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        } catch (PolicyException e) {
            throw toWeb(e);
        } catch (NotFoundException e) {
            throw new ItemNotFoundException();
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }
    }

    private WPolicyException toWeb(PolicyException e) throws RPCException {
        Map<ItemInfo, Collection<WApprovalMessage>> failures = new HashMap<ItemInfo, Collection<WApprovalMessage>>();
        for (Map.Entry<Item, List<ApprovalMessage>> entry : e.getPolicyFailures().entrySet()) {
            Item i = entry.getKey();
            List<ApprovalMessage> approvals = entry.getValue();

            try {
                ItemInfo info = toWeb(i, false);
    
                ArrayList<WApprovalMessage> wapprovals = new ArrayList<WApprovalMessage>();
                for (ApprovalMessage app : approvals) {
                    wapprovals.add(new WApprovalMessage(app.getMessage(), app.isWarning()));
                }
    
                failures.put(info, wapprovals);
            } catch (RegistryException ex) {
                log.error(ex.getMessage(), ex);
                throw new RPCException(ex.getMessage());
            }
        }
        return new WPolicyException(failures);
    }

    private List<Policy> getArtifactPolicies(Collection ids) {
        List<Policy> policies = new ArrayList<Policy>();
        for (Iterator itr = ids.iterator(); itr.hasNext();) {
            String id = (String) itr.next();

            Policy policy = policyManager.getPolicy(id);
            policies.add(policy);
        }
        return policies;
    }

    private Collection<String> getArtifactPolicyIds(Collection<Policy> pols) {
        ArrayList<String> polNames = new ArrayList<String>();
        for (Policy ap : pols) {
            polNames.add(ap.getId());
        }
        return polNames;
    }

    public Collection<WPolicy> getPolicies() throws RPCException {
        Collection<Policy> policies = policyManager.getPolicies();
        List<WPolicy> gwtPolicies = new ArrayList<WPolicy>();
        for (Policy p : policies) {
            gwtPolicies.add(toWeb(p));
        }
        Collections.sort(gwtPolicies, new Comparator<WPolicy>() {

            public int compare(WPolicy o1, WPolicy o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });

        return gwtPolicies;
    }

    public void saveLifecycle(WLifecycle wl) throws RPCException, ItemExistsException {
        Lifecycle l = fromWeb(wl);

        try {
            localLifecycleManager.save(l);

            if (wl.isDefaultLifecycle()) {
                Lifecycle defaultLifecycle = localLifecycleManager.getDefaultLifecycle();

                if (!defaultLifecycle.equals(l)) {
                    localLifecycleManager.setDefaultLifecycle(l);
                }
            }
        } catch (DuplicateItemException e) {
            throw new ItemExistsException();
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public void deleteLifecycle(String id) throws RPCException {
        try {
            String fallback = localLifecycleManager.getDefaultLifecycle().getId();

            if (id.equals(fallback)) {
                throw new RPCException("The default lifecycle cannot be deleted. Please assign " +
                        "another lifecycle to be the default before deleting this one.");
            }

            localLifecycleManager.delete(id, fallback);
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    private Lifecycle fromWeb(WLifecycle wl) throws RPCException {
        Lifecycle l = new Lifecycle();
        l.setPhases(new HashMap<String, Phase>());
        l.setName(wl.getName());
        l.setId(wl.getId());

        for (Object o : wl.getPhases()) {
            WPhase wp = (WPhase) o;

            Phase p = new Phase(l);
            p.setId(wp.getId());
            p.setName(wp.getName());
            l.getPhases().put(p.getName(), p);
        }

        for (Object o : wl.getPhases()) {
            WPhase wp = (WPhase) o;
            Phase p = l.getPhase(wp.getName());
            p.setNextPhases(new HashSet<Phase>());

            if (wp.getNextPhases() != null) {
                for (Object oNext : wp.getNextPhases()) {
                    WPhase wNext = (WPhase) oNext;
                    Phase next = l.getPhase(wNext.getName());

                    p.getNextPhases().add(next);
                }
            }
        }

        if (wl.getInitialPhase() == null) {
            throw new RPCException("You must set a phase as the initial phase.");
        }

        l.setInitialPhase(l.getPhase(wl.getInitialPhase().getName()));
        return l;
    }

    private WPolicy toWeb(Policy p) {
        WPolicy wap = new WPolicy();
        wap.setId(p.getId());
        wap.setDescription(p.getDescription());
        wap.setName(p.getName());

        return wap;
    }

    public Collection<WActivity> getActivities(Date from, Date to, 
                                               String user,
                                               String itemPath,
                                               String text,
                                               String eventTypeStr, int start,
                                    int results, boolean ascending) throws RPCException {

        if ("All".equals(user)) {
            user = null;
        }

        if (from != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(from);
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            from = c.getTime();
        }

        if (to != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(to);
            c.set(Calendar.HOUR, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            to = c.getTime();
        }

        EventType eventType = null;
        if ("Info".equals(eventTypeStr)) {
            eventType = EventType.INFO;
        } else if ("Warning".equals(eventTypeStr)) {
            eventType = EventType.WARNING;
        } else if ("Error".equals(eventTypeStr)) {
            eventType = EventType.ERROR;
        }

        
        try {
            String itemId = null;
            if (itemPath != null && !"[All Items]".equals(itemPath)) {
                try {
                    itemId = registry.getItemByPath(itemPath).getId();
                } catch (NotFoundException e) {
                    throw new RPCException("You do not have sufficient permissions to view activities relating to that item.");
                }
            }
            
            Collection<Activity> activities = activityManager.getActivities(from, to, user, 
                                                                            itemId, text,
                                                                            eventType, start, 
                                                                            results, ascending);

            ArrayList<WActivity> wactivities = new ArrayList<WActivity>();

            for (Activity a : activities) {
                wactivities.add(createWActivity(a));
            }
            return wactivities;
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            throw new RPCException(e.getMessage());
        }
    }

    protected WActivity createWActivity(Activity a) {
        WActivity wa = new WActivity();
        wa.setId(a.getId());
        wa.setEventType(a.getEventType().getText());
        if (a.getUser() != null) {
            wa.setUsername(a.getUser().getUsername());
            wa.setName(a.getUser().getName());
        }
        wa.setMessage(a.getMessage());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        wa.setDate(dateFormat.format(a.getDate().getTime()));
        return wa;
    }

    public WUser getUserInfo() throws RPCException {
        User user = getCurrentUser();
        WUser w = SecurityServiceImpl.createWUser(user);

        List<String> perms = new ArrayList<String>();

        for (Permission p : accessControlManager.getGrantedPermissions(user)) {
            perms.add(p.toString());
        }
        w.setPermissions(perms);

        return w;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.localLifecycleManager = lifecycleManager;
    }

    public void setArtifactTypeDao(ArtifactTypeDao artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public void setRendererManager(RendererManager viewManager) {
        this.rendererManager = viewManager;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public void setArtifactViewManager(ArtifactViewManager artifactViewManager) {
        this.artifactViewManager = artifactViewManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public ContextPathResolver getContextPathResolver() {
        return contextPathResolver;
    }

    public void setContextPathResolver(final ContextPathResolver contextPathResolver) {
        this.contextPathResolver = contextPathResolver;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(final EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    public void setWebManager(WebManager webManager) {
        this.webManager = webManager;
    }
    
}
