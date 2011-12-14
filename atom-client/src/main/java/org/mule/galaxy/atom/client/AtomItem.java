package org.mule.galaxy.atom.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Person;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.impl.workspace.AbstractItem;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;

public class AtomItem extends AbstractItem {

	private final Entry entry;
	private Item parent;
	private String path;

	private Map<String, PropertyInfo> properties;
	private final AtomWorkspaceManager workspaceManager;

	public AtomItem(Item parent, Entry e, AtomWorkspaceManager workspaceManager) {
		super(workspaceManager);
		this.parent = parent;
		this.entry = e;
		this.workspaceManager = workspaceManager;

		path = entry.getLink("edit").getHref().getPath().toString();
		id = EntryUtils.getId(workspaceManager, path);
	}

	public User getAuthor() {
		Person a = entry.getAuthor();
		if (a != null) {
			User user = new User();
			user.setName(a.getName());
			return user;
		}
		return null;
	}

	public TypeManager getTypeManager() {
		return workspaceManager.getTypeManager();
	}

	public boolean isInternal() {
		return false;
	}

	public void setInternal(boolean internal) {
	}

	private void initializeProperties() {
		if (properties != null)
			return;

		properties = new HashMap<String, PropertyInfo>();

		Element metadata = entry.getExtension(new QName(AtomWorkspaceManager.NAMESPACE, "metadata"));
		if (metadata != null) {
			List<ExtensibleElement> propertyElements = metadata.getElements();
			for (ExtensibleElement propertyEl : propertyElements) {
				String elName = propertyEl.getQName().getLocalPart();
				String name;
				Object value;
				if ("property".equals(elName)) {
					value = propertyEl.getAttributeValue("value");
					name = propertyEl.getAttributeValue("name");
					if (value == null) {
						ArrayList<String> values = new ArrayList<String>();
						List<Element> elements = propertyEl.getElements();

						if (elements != null) {
							for (Element e : elements) {
								values.add(e.getText());
							}
						}
						value = values;
					}

				} else if ("artifact".equals(elName)) {
					value = new AtomArtifact(this, propertyEl, (AtomWorkspaceManager) manager);
					name = propertyEl.getAttributeValue("property");
				} else {
					continue;
				}
				AtomPropertyInfo pi = new AtomPropertyInfo(this, name, value, manager.getTypeManager());
				properties.put(pi.getName(), pi);
			}
		}
	}

	public <T> T getInternalProperty(String name) {
		initializeProperties();
		return null;
	}

	public Collection<PropertyInfo> getProperties() {
		initializeProperties();
		return properties.values();
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperty(String name) {
		initializeProperties();
		PropertyInfo pi = getPropertyInfo(name);

		return pi == null ? null : (T) pi.getValue();
	}

	public PropertyDescriptor getPropertyDescriptor(String name) {
		return getTypeManager().getPropertyDescriptorByName(name);
	}

	public PropertyInfo getPropertyInfo(String name) {
		initializeProperties();
		return properties.get(name);
	}

	public boolean hasProperty(String name) {
		initializeProperties();
		return properties.containsKey(name);
	}

	public void setInternalProperty(String name, Object value) throws PropertyException, PolicyException, AccessException {
		initializeProperties();
		throw new UnsupportedOperationException();
	}

	public void setLocked(String name, boolean locked) {
		initializeProperties();
		throw new UnsupportedOperationException();
	}

	public void setProperty(String name, Object value) throws PropertyException, PolicyException, AccessException {
		initializeProperties();
		throw new UnsupportedOperationException();
	}

	public void setVisible(String property, boolean visible) {
		initializeProperties();

	}

	protected String getElementAttribute(String elName, String attName, String defaultValue) {
		return EntryUtils.getElementAttribute(entry, elName, attName, defaultValue);
	}

	public Calendar getCreated() {
		return EntryUtils.getElementAttributeAsCalendar(entry, "item-info", "created", null);
	}

	public String getDescription() {
		return entry.getSummary();
	}

	public String getName() {
		return EntryUtils.getElementAttribute(entry, "item-info", "name", "");
	}

	public Item getParent() {
		if (parent == null) {
			parent = ((AtomWorkspaceManager) manager).getParent(this);
		}
		return parent;
	}

	public Calendar getUpdated() {
		Calendar c = Calendar.getInstance();
		c.setTime(entry.getUpdated());
		return c;
	}

	public void setDescription(String description) {
		entry.setSummary(description);
	}

	public Type getType() {
		Type type = new Type();

		String name = EntryUtils.getElementAttribute(entry, "item-info", "type", null);
		type.setName(name);
		type.setId(name);

		return type;
	}

	public void setType(Type type) {
	}

	public void setName(String name) {
		entry.setTitle(name);
	}

	public Entry getAtomEntry() {
		return entry;
	}

	public String getRemotePath() {
		return path;
	}

	public Item getItem(String name) throws RegistryException, NotFoundException, AccessException {
		return manager.getItem(this, name);
	}

	public CommentManager getCommentManager() {
		return manager.getCommentManager();
	}

	public Lifecycle getDefaultLifecycle() {
		return null;
	}

	public LifecycleManager getLifecycleManager() {
		return null;
	}

	public void setDefaultLifecycle(Lifecycle l) {
		// TODO Auto-generated method stub

	}
}
