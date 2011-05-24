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

package org.mule.galaxy.atom;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.DefaultWorkspaceManager;
import org.apache.abdera.protocol.server.impl.SimpleTarget;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.TypeManager;

public class ItemResolver implements Resolver<Target> {

    public static final String ITEM = "entry";
    public static final String COLLECTION_HREF = "collectionHref";
    
    private Registry registry;
    private ItemCollection itemCollection;
    private CommentCollectionProvider commentCollection;
    
    public Target resolve(Request request) {
        RequestContext context = (RequestContext) request;
        String path = context.getTargetPath();
        
        if (path.startsWith("/api")) {
            path = path.substring(4);
        }
        
        // trim query part
        int qIdx = path.lastIndexOf('?');
        if (qIdx != -1) {
            path = path.substring(0, qIdx);
        }

        // Find the resource representation type
        String classifier = null;
        int cIdx = path.lastIndexOf(';');
        if (cIdx != -1) {
            classifier = path.substring(cIdx+1);
            path = path.substring(0, cIdx);
        }
        
        // remove front slash
        if (path.startsWith("/")) {
            path =  path.substring(1);
        }
        // remove end slash
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        
        // find where this is going to - comments, registry, etc
        int sIdx = path.indexOf('/');
        if (sIdx == -1 && path.length() == 0) {
            return new SimpleTarget(TargetType.TYPE_SERVICE, context);
        }
        
        String atomWorkspace = null;
        if (sIdx != -1) {
            atomWorkspace = path.substring(0, sIdx);
            path = path.substring(sIdx);
        } else {
            atomWorkspace = path;
            path = "";
        }
        
        if ("comments".equals(atomWorkspace)) {
            context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, commentCollection);
            sIdx = path.indexOf('/');
            if (sIdx != -1 && sIdx != path.length()) {
                return new SimpleTarget(TargetType.TYPE_ENTRY, context);
            } else {
                return new SimpleTarget(TargetType.TYPE_COLLECTION, context);
            }
        }
        
        if (!"registry".equals(atomWorkspace)) {
            return returnUnknownLocation(context);
        }
        
        // Somebody hit /api/registry
        if (path.length() == 0) {
            context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, itemCollection);
            return new SimpleTarget(TargetType.TYPE_COLLECTION, context);
        }

        // somebody hit /api/registry/foo....
        path = UrlEncoding.decode(path);
        
        try {
            Item item = registry.getItemByPath(path);
            
            if (item != null) {
                return resolveItem(item, classifier, context);
            } else {
                return returnUnknownLocation(context);
            }
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            return returnUnknownLocation(context);
        } catch (NotFoundException e) {
            return returnUnknownLocation(context);
        } 

    }

    private Target returnUnknownLocation(RequestContext context) {
        return new SimpleTarget(TargetType.TYPE_NOT_FOUND, context);
    }

    private Target resolveItem(Item item, String classifier, RequestContext context)
        throws RegistryException, NotFoundException, AccessException {

        String method = context.getMethod();
        boolean isArtifact = item.getType().inheritsFrom(TypeManager.ARTIFACT);
        // Send PUT and GET requests to /workspace/artifact to the version, not the artifact
        if (isArtifact && (classifier != null || (!"POST".equals(method) && !"DELETE".equals(method)))) {
            Item child = selectVersion(item, context);
            
            // this is when we refer to an artifact without a ?version=foo
            if (item == child && classifier == null) {
                item = getLatestOrDefault(item);
            } else {
                item = child;
            }
            
            if (item == null) {
                return returnUnknownLocation(context);
            }
        }
        
        context.setAttribute(ITEM, item);
        context.setAttribute(COLLECTION_HREF, getPathWithoutArtifact(context));
        context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, itemCollection);

        if ("atom".equals(classifier)) {
            return new SimpleTarget(TargetType.TYPE_ENTRY, context);
        } else if ("categories".equals(classifier)) {
            return new SimpleTarget(TargetType.TYPE_CATEGORIES, context);
        }  else if ("history".equals(classifier) || "items".equals(classifier)) {
            return new SimpleTarget(TargetType.TYPE_COLLECTION, context);
        } else if (classifier == null) {
            if (!"POST".equals(method) && isArtifact) {
                return new SimpleTarget(TargetType.TYPE_MEDIA, context);
            } else if ("HEAD".equals(method) || "OPTIONS".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                return new SimpleTarget(TargetType.TYPE_ENTRY, context);
            }  else  {
                return new SimpleTarget(TargetType.TYPE_COLLECTION, context);
            }
        }
        return returnUnknownLocation(context);
    }

    protected Item selectVersion(Item i, RequestContext context) 
        throws RegistryException, NotFoundException, AccessException {
//        String[] params = context.getParameterNames();
//        Query query = new Query(OpRestriction.eq("name", i.getName()));
//        query.fromPath(i.getParent().getPath());
//
//        for (int c = 0; c < params.length; c++) {
//            String param = params[c];
//
//            if (!"showHiddenProperties".equals(param) && !"version".equals(param)) {
//                query.add(OpRestriction.eq(param, context.getParameter(param)));
//            }
//        }
//
//        if (query.getRestrictions().size() > 1) {
//            SearchResults results = registry.search(query);
//
//            if (results.getTotal() == 0) {
//                return null;
//            }
//
//            i = results.getResults().iterator().next();
//        }
        
        String version = context.getParameter("version");
        if (version != null && !"".equals(version)) {
            return i.getItem(version);
        } 
        return i;
    }

    private Item getLatestOrDefault(Item i) throws RegistryException {
        Item def = i.getProperty(TypeManager.DEFAULT_VERSION);
        if (def == null) {
            return i.getLatestItem();
        }
        return def;
    }
    private String getPathWithoutArtifact(RequestContext context) {
        String s = context.getTargetPath();
        
        int idx = s.lastIndexOf('/');
        if (idx != -1) {
            s = s.substring(0, idx);
        }
        return s;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setItemCollection(ItemCollection itemCollection) {
        this.itemCollection = itemCollection;
    }

    public void setCommentCollection(CommentCollectionProvider commentCollection) {
        this.commentCollection = commentCollection;
    }

}
