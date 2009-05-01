package org.mule.galaxy.mule2.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.RegistrationException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.AccessException;
import org.mule.registry.AbstractRegistry;

/**
 * A Mule {@link org.mule.api.registry.Registry} implementation which builds
 * objects from metadata inside the Galaxy {@link Registry}.
 */
public class GalaxyRegistry extends AbstractRegistry {
    private Registry registry;
    private Map<String, ObjectFactory> factories;
    
    public GalaxyRegistry(String id) {
        super(id);
    }

    public Object lookupObject(String key) {
        Item item = getItem(key);
        
        if (item == null) {
            return null;
        }
        
        ObjectFactory objectFactory = factories.get(item.getType());
        
        return objectFactory.create(item);
    }

    public Collection lookupObjects(Class type) {
        return Collections.emptyList();
    }
    
    protected Item getItem(String key) {
        try {
            return registry.getItemByPath(key);
        } catch (NotFoundException e) {
            return null;
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDispose() {
    }

    @Override
    protected void doInitialise() throws InitialisationException {
    }

    public boolean isReadOnly() {
        return true;
    }

    public boolean isRemote() {
        return true;
    }

    public void registerObject(String key, Object value, Object metadata) throws RegistrationException {
        throw new UnsupportedOperationException();
    }

    public void registerObject(String key, Object value) throws RegistrationException {
        throw new UnsupportedOperationException();
    }

    public void registerObjects(Map objects) throws RegistrationException {
        throw new UnsupportedOperationException();
    }

    public void unregisterObject(String key, Object metadata) throws RegistrationException {
        throw new UnsupportedOperationException();
    }

    public void unregisterObject(String key) throws RegistrationException {
        throw new UnsupportedOperationException();
    }

    public Map<String, ObjectFactory> getFactories() {
        return factories;
    }

    public void setFactories(Map<String, ObjectFactory> factories) {
        this.factories = factories;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
}
