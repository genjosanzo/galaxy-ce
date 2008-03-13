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

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.index.IndexException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class GroovyIndexer extends AbstractIndexer
{

    private final Log log = LogFactory.getLog(getClass());

    public void index(final ArtifactVersion artifact, final ContentHandler contentHandler, final Index index) throws IOException, IndexException
    {
        Map<String, String> config = index.getConfiguration();
        String scriptSource = config.get("scriptSource");

        if (scriptSource == null)
        {
            // TODO proper message after GALAXY-147 is done
            throw new IndexException(new Exception("scriptSource is not specified"));
        }

        final ResourceLoader loader = new DefaultResourceLoader();
        InputStream is = loader.getResource(scriptSource).getInputStream();

        Binding b = new Binding();
        b.setVariable("artifact", artifact);
        b.setVariable("config", config);
        b.setVariable("contentHandler", contentHandler);
        b.setVariable("index", index);
        b.setVariable("log", LogFactory.getLog(getClass() + "." + index.getId()));

        
        GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), b);

        Script script;
        try
        {
            script = shell.parse(is);
            script.run();
        }
        catch (Exception ex)
        {
            throw new IndexException(ex);
        }
        finally
        {
            is.close();
        }
    }
}
