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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class GroovyIndexer extends AbstractIndexer
{

    public void index(final ArtifactVersion artifact, final ContentHandler contentHandler, final Index index) throws IOException, IndexException
    {
        Map<String, String> config = index.getConfiguration();
        String scriptSource = config.get("scriptSource");

        if (scriptSource == null)
        {
            // TODO misconfig, log or throw an error
            return;
        }

        Binding b = new Binding();
        b.setVariable("artifact", artifact);
        b.setVariable("config", config);
        b.setVariable("contentHandler", contentHandler);
        b.setVariable("index", index);
        
        GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), b);
        // TODO check it exists first
        Script script = shell.parse(new File(scriptSource));
        // TODO not optimal, cache script
        script.run();
    }
}
