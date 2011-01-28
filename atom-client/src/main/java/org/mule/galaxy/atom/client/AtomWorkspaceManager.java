package org.mule.galaxy.atom.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.axiom.om.util.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.AttachedItem;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.atom.ItemCollection;
import org.mule.galaxy.impl.workspace.AbstractWorkspaceManager;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.workspace.WorkspaceManager;

/**
 * Handles all the operations for manipulating remote workspaces via the Atom API.
 */
public class AtomWorkspaceManager extends AbstractWorkspaceManager implements WorkspaceManager {

    public static final String NAMESPACE = "http://galaxy.mule.org/2.0";
    
    private final Log log = LogFactory.getLog(getClass());
    
    private ResourceBundle bundle = BundleUtils.getBundle(this.getClass());
    
    private String url;
    private String urlWithoutPath;
    protected String username;
    protected String password;
    protected AbderaClient client;
    protected RequestOptions defaultOpts;
    private String attachedToId;
    private String attachedToPath;
    private Registry registry;
    private Factory factory;
    private TypeManager typeManager;
    private String id;
    
    public void initialize() {
        client = new AbderaClient();
        factory = Abdera.getInstance().getFactory();
        
        defaultOpts = createDefaultRequestOptions();
    }

    public void validate() throws RegistryException {
        getItems(null);
    }

    protected RequestOptions createDefaultRequestOptions() {
        RequestOptions opts =  client.getDefaultRequestOptions();
        opts.setAuthorization("Basic " + Base64.encode((getUsername() + ":" + getPassword()).getBytes()));
        return opts;
    };
    
    public void save(Item item) throws RegistryException, AccessException {
        String url = getUrl(item);
        ClientResponse res = client.put(url + ";atom", ((AtomItem) item).getAtomEntry(), defaultOpts);
        try {
            handleError(res, false);
        } finally {
            res.release();
        }
    }

    public Item getItem(Item w, String name) throws RegistryException, NotFoundException, AccessException {
        for (Item i : w.getItems()) {
            if (i.getName().equals(name)) {
                return i;
            }
        }
        throw new NotFoundException(w.getPath() + name);
    }


    public Collection<Item> getWorkspaces() throws AccessException, RegistryException {
        return getItems(null);
    }

    public NewItemResult newItem(Item parent, String name, Type type, Map<String, Object> initialProperties)
            throws DuplicateItemException, RegistryException, PolicyException, AccessException,
            PropertyException {
        if (initialProperties != null && initialProperties.containsKey("artifact")) {
            return newArtifactVersion(parent, name, type, initialProperties);
        } else {
            return newNonArtifact(parent, name, type, initialProperties);
        }
    }

    private NewItemResult newNonArtifact(Item parent, String name, Type type,
                                         Map<String, Object> initialProperties) 
        throws RegistryException, AccessException, DuplicateItemException {
        Entry entry = factory.newEntry();
        entry.setTitle(name);
        entry.setUpdated(new Date());
        entry.addAuthor("Ignored");
        entry.setId(factory.newUuidUri());
        entry.setContent("");
        
        Element versionEl = factory.newExtensionElement(new QName(ItemCollection.NAMESPACE, "item-info"), entry);
        versionEl.setAttributeValue("name", name);
        versionEl.setAttributeValue("type", type.getName());
        
        String postUrl = parent != null ? getUrl(parent) : url;
        
        ClientResponse res = client.post(postUrl, entry, defaultOpts);
        try {
            handleItemErrors(postUrl, res);
            Element el = handleError(res);
            
            return new NewItemResult(asItem(res, el, postUrl), new HashMap<Item, List<ApprovalMessage>>());
        } finally {
            res.release();
        }
    }

    private NewItemResult newArtifactVersion(Item parent, 
                                             String name, 
                                             Type type,
                                             Map<String, Object> initialProperties) 
        throws RegistryException, DuplicateItemException, AccessException {
        
        Object o = initialProperties.get("artifact");
        InputStream stream;
        String contentType;
        if (o instanceof Artifact) {
            Artifact a = (Artifact) o;
            try {
                stream = a.getInputStream();
            } catch (IOException e) {
                throw new RegistryException(e);
            }
            contentType = a.getContentType().toString();
        } else if (o instanceof Object[]){
            stream = (InputStream)((Object[]) o)[0];
            contentType = (String)((Object[]) o)[1];
        } else {
            throw new RegistryException(new Message("UNRECOGNIZED_ARTIFACT_TYPE", bundle, o.getClass()));
        }
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType(contentType + "; charset=utf-8");
        opts.setSlug(name);
        opts.setHeader("X-Artifact-Version", name);
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        String uri = getUrl(parent);
        ClientResponse res = client.post(uri, stream, opts);
        
        try {
            handleItemErrors(uri, res);
            Element el = handleError(res);
            
            AtomItem artifact = new AtomItem(parent, (Entry) el, this);
            
            return new NewItemResult(artifact, new HashMap<Item, List<ApprovalMessage>>());
        } finally {
            res.release();
        }
    }

    private String getUrl(Item item) {
        if (item instanceof AttachedItem) {
            return url;
        }
        
        if (item == null) {
            return url;
        }
        
        String url = urlWithoutPath + ((AtomItem) item).getRemotePath();
        
        int semi = url.indexOf(';');
        
        if (semi != -1) {
            url = url.substring(0, semi);
        }
        return url;
    }

    private void handleItemErrors(String uri, ClientResponse res) throws DuplicateItemException,
        AccessException {
        int status = res.getStatus();
        if (status == 409) {
            throw new DuplicateItemException(uri);
        } else if (status == 401) {
            throw new AccessException();
        }
    }

    public String getId() {
        return id;
    }

    public Item getItemById(String id) throws NotFoundException, RegistryException, AccessException {
        String path = trimWorkspaceManagerId(id);
        path = UrlEncoding.decode(path);
        path = UrlEncoding.encode(path, Profile.PATH.filter());
        
        // hack because encode thinks ? should be encoded and I didn't want to write a new filter
        path = path.replaceAll("%3F", "?");
        return getItemByRemotePath(path);
    }

    public Item getItemByPath(String path) throws NotFoundException, RegistryException, AccessException {
        String url = getRemoteUrl(path);
        
        return getItemByRemoteUrl(url);
    }

    protected Item getItemByRemotePath(String path) throws RegistryException {
        return getItemByRemoteUrl(urlWithoutPath + path);
    }
    
    protected Item getItemByRemoteUrl(String url) throws RegistryException {
        log.info("getItem on " + url);
        ClientResponse res = client.get(url, defaultOpts);
        try {
            Element el = handleError(res);
        
            return asItem(res, el, url);
        } finally {
            res.release();
        }
    }

    private Item asItem(ClientResponse res, Element el, String url) throws RegistryException {
        if (el instanceof Feed) {
            // hack to get workspaces...
            return getItemByRemoteUrl(url + ";atom");
        } else if (el instanceof Entry) {
            return asItem((Item)null, (Entry) el, url);
        }
        
        throw new RegistryException(new Message("INVALID_RESPONSE_TYPE", bundle, res.getContentType()));
    }

    protected String getRemoteUrl(String localPath) {
        String addPath = localPath.substring(attachedToPath.length());
        
        return url + addPath;
    }

    public InputStream getStream(IRI src) throws IOException {
        ClientResponse res = client.get(url, defaultOpts);
        return res.getInputStream();
    }

    public List<Item> getItems(Item parent) throws RegistryException {
        String url = getUrl(parent);
        
        if (url.endsWith(";atom")) {
            url = url.substring(0, url.length() - 5);
        }
        url = url + ";items";
        
        ClientResponse res = client.get(url, defaultOpts);
        
        try {
            handleError(res);
            
            List<Item> items = new ArrayList<Item>();
            
            return getItems(res, url, parent, items);
        } finally {
            res.release();
        }
    }

    private List<Item> getItems(ClientResponse res, String url, Item parent, List<Item> items) {
        Document<Feed> document = res.getDocument();
        Feed feed = document.getRoot();

        for (Entry e : feed.getEntries()) {
            items.add(asItem(parent, e, url));
        }
        
        return items;
    }

    /**
     * Converts an Atom Entry into an {@link Item}.
     * @param workspace
     * @param e
     * @return
     */
    private Item asItem(Item parent, Entry e, String url) {
        return new AtomItem(parent, e, this);
    }

    protected Element handleError(ClientResponse res) throws RegistryException {
        return handleError(res, true);
    }
    
    protected Element handleError(ClientResponse res, boolean hasResponse) throws RegistryException {
        if (res.getStatus() >= 300) {
            try {
                IOUtils.copy(res.getInputStream(), System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new RegistryException(new Message("Could not access remote workspace. Got status " + res.getStatus() + ": " + res.getStatusText()
                                                    + " - for " + res.getUri(), 
                                                    (ResourceBundle)null));
        }
        
        if (hasResponse) {
            Document<Element> doc = res.getDocument();
            Element root = doc.getRoot();

            if (!(root instanceof Feed) && !(root instanceof Entry)) {
                throw new RegistryException(new Message("Could not find an Atom Feed or Entry. Found: " + res.getContentType(), 
                                                        (ResourceBundle)null));
            }
    
            return root;
        }
        return null;
    }

    protected void prettyPrint(Base doc) throws IOException {
        WriterFactory writerFactory = Abdera.getInstance().getWriterFactory();
        Writer writer = writerFactory.getWriter("prettyxml");
        writer.writeTo(doc, System.out);
        System.out.println();
    }
    
    public void attachTo(Item workspace) {
        this.attachedToId = workspace.getId();
        this.attachedToPath = workspace.getPath();
        
        this.id = attachedToId.substring(attachedToId.indexOf('$') + 1);
    }

    public void delete(Item i) throws RegistryException {
        ClientResponse res = client.delete(getUrl(i), defaultOpts);
        try {
            if (res.getStatus() > 300) {
                throw new RegistryException(new Message("UNABLE_TO_DELETE", bundle, i.getPath(), res.getStatus()));
            }
        } finally {
            res.release();
        }        
    }

    protected String getWorkspacesUrl() {
        return getUrl() + ";workspaces";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        
        int i = url.indexOf("//");
        if (i != -1) {
            int slash = url.indexOf('/', i+2);
            if (slash != -1) {
                urlWithoutPath = url.substring(0, slash);
            } else {
                urlWithoutPath = url;
            }
        }
    }

    public Item getParent(AtomItem item) {
        String path = item.getRemotePath();
        path = UrlEncoding.decode(path);
        
        try {
            // it's an Entry/Artifact/Workspace asking for a workspace parent
            int semi = path.lastIndexOf(";");
            if (semi != -1) {
                path = path.substring(0, semi);
            }
            
            if (path.endsWith("/")) {
                path.substring(0, path.length());
            }
            
            int slash = path.lastIndexOf("/");
            if (slash != -1) {
                path = path.substring(0, slash);
            }
            
            path = UrlEncoding.encode(path, Profile.PATH.filter());
            if (url.equals(urlWithoutPath + path)) {
                return registry.getItemById(attachedToId);
            }
            
            return getItemByRemotePath(path + ";atom");
        } catch (NotFoundException e) {
            return null;
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            // Shouldn't happen?
            throw new RuntimeException(e);
        }
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public TypeManager getTypeManager() {
        return typeManager;
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

}
