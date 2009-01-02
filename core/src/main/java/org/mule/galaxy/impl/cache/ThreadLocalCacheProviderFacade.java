package org.mule.galaxy.impl.cache;

import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springmodules.cache.CacheException;
import org.springmodules.cache.CachingModel;
import org.springmodules.cache.FatalCacheException;
import org.springmodules.cache.FlushingModel;
import org.springmodules.cache.provider.AbstractCacheProviderFacade;
import org.springmodules.cache.provider.CacheModelValidator;
import org.springmodules.cache.provider.InvalidCacheModelException;
import org.springmodules.cache.provider.ReflectionCacheModelEditor;

/**
 * A cache which stores objects in a ThreadLocal object. This must be cleared
 * after the thread is done executing!
 * 
 * @author Dan
 */
public class ThreadLocalCacheProviderFacade extends AbstractCacheProviderFacade {

    private static ThreadLocal<Map<Serializable,Object>> cache = new ThreadLocal<Map<Serializable,Object>>();

    public static void enableCache() {
        cache.set(new HashMap<Serializable, Object>());
    }
    
    public static void clearCache() {
        cache.set(null);
    }

    @Override
    protected boolean isSerializableCacheElementRequired() {
        return false;
    }

    @Override
    protected void onFlushCache(FlushingModel model) throws CacheException {
    }

    @Override
    protected Object onGetFromCache(Serializable key, CachingModel arg1) throws CacheException {
        Map<Serializable, Object> map = cache.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    @Override
    protected void onPutInCache(Serializable key, CachingModel arg1, Object object) throws CacheException {
        Map<Serializable, Object> map = cache.get();
        if (map == null) {
            return;
        }
        
        map.put(key, object);
    }

    @Override
    protected void onRemoveFromCache(Serializable key, CachingModel arg1) throws CacheException {
        Map<Serializable, Object> map = cache.get();
        
        if (map == null) {
            return;
        }
        
        map.clear();        
    }

    @Override
    protected void validateCacheManager() throws FatalCacheException {
    }

    public PropertyEditor getCachingModelEditor() {
        ReflectionCacheModelEditor editor = new ReflectionCacheModelEditor();
        editor.setCacheModelClass(ThreadLocalCachingModel.class);
        return editor;
    }

    public PropertyEditor getFlushingModelEditor() {
        ReflectionCacheModelEditor editor = new ReflectionCacheModelEditor();
        editor.setCacheModelClass(ThreadLocalFlushingModel.class);
        return editor;
    }

    public CacheModelValidator modelValidator() {
        return new CacheModelValidator() {

            public void validateCachingModel(Object arg0) throws InvalidCacheModelException {
            }

            public void validateFlushingModel(Object arg0) throws InvalidCacheModelException {
            }
            
        };
    }

}
