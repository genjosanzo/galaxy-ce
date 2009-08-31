/*
 * $Id$
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

package org.mule.galaxy.impl.index;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.index.IndexException;
import org.mule.galaxy.type.TypeManager;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class GroovyIndexer extends AbstractIndexer
{
    public static final String NAME = "groovy";
    protected static final ConcurrentMap<String, GroovyScriptEngine> scriptCache = new ConcurrentHashMap<String, GroovyScriptEngine>();

    private final Log log = LogFactory.getLog(getClass());

    private TypeManager typeManager;
    
    private Registry registry;
    
    public void index(final Item item, final PropertyInfo property, final Index index) throws IOException,
        IndexException {
        Map<String, String> config = index.getConfiguration();
        Artifact artifact = property.getValue();
        if (artifact == null) {
            return;
        }

        if (log.isDebugEnabled())
        {
            log.debug("Processing: " + index);
        }

        String scriptSource = config.get("scriptSource");

        if (scriptSource == null)
        {
            // TODO proper message after GALAXY-147 is done
            throw new IndexException(new Exception("scriptSource is not specified"));
        }

        Binding b = new Binding();
        b.setVariable("item", item);
        b.setVariable("artifact", artifact);
        b.setVariable("config", config);
        b.setVariable("contentHandler", artifact.getContentHandler());
        b.setVariable("index", index);
        b.setVariable("log", LogFactory.getLog(getClass().getName()));
        b.setVariable("typeManager", typeManager);
        b.setVariable("registry", registry);

        final ResourceLoader loader = new DefaultResourceLoader();
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
        if (!runCompiledScript(scriptSource, b, cl)) {
            runScript(scriptSource, b, loader, cl);
        }
    }

    private void runScript(String scriptSource, Binding b, final ResourceLoader loader, final ClassLoader cl)
        throws IndexException {
        try
        {
            // scriptSource is the key
            GroovyScriptEngine engine = scriptCache.get(scriptSource);
            if (engine == null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Cache miss for " + scriptSource);
                }
                engine = new GroovyScriptEngine(new URL[] {loader.getResource(scriptSource).getURL()}, cl);

                // in case processed it already, use the cached version instead
                final GroovyScriptEngine alreadyCachedEngine = scriptCache.putIfAbsent(scriptSource, engine);
                if (alreadyCachedEngine != null)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Someone was faster, use their's result for " + scriptSource);
                    }
                    engine = alreadyCachedEngine;
                }
            }
            else if (log.isDebugEnabled())
            {
                log.debug("Using a cached engine for " + scriptSource);
            }

            engine.run(scriptSource, b);
        }
        catch (Throwable ex)
        {
            throw new IndexException(ex);
        }
    }

    private boolean runCompiledScript(String scriptSource, Binding b, final ClassLoader cl) {
        int idx = scriptSource.indexOf(".groovy");
        String clsName = scriptSource;
        if (idx != -1) {
            clsName = scriptSource.substring(0, idx);
        }
        clsName = clsName.replace("/", ".");
        
        try {
            Class<?> cls = cl.loadClass(clsName);
            Constructor<?> constructor = cls.getConstructor(Binding.class);
            
            Object instance = constructor.newInstance(b);
            
            Method runMethod = cls.getMethod("run");
            runMethod.invoke(instance);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (Exception e) {
            log.error("Could not invoke indexing script", e);
            return true;
        }
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
}
