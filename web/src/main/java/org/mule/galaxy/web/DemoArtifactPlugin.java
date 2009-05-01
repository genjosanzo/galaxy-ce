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

package org.mule.galaxy.web;

import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.impl.plugin.AbstractArtifactPlugin;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;

public class DemoArtifactPlugin extends AbstractArtifactPlugin {
    @Override
    public void doInstall() throws Exception {
        Item w = registry.getItems().iterator().next();
        
        add(w, "applicationContext.xml", "/spring/test-applicationContext.xml");
        add(w, "hello-config.xml", "/mule2/hello-config.xml");
        add(w, "hello.xsd", "/wsdl/imports/hello.xsd");
        add(w, "hello-portType.wsdl", "/wsdl/imports/hello-portType.wsdl");
        add(w, "hello.wsdl", "/wsdl/imports/hello.wsdl");   
        
        PropertyDescriptor pd = new PropertyDescriptor("location", "Location", false, false);
        typeManager.savePropertyDescriptor(pd);
        
        pd = new PropertyDescriptor("business.group", "Business Groups", true, false);
        typeManager.savePropertyDescriptor(pd);
        
        pd = new PropertyDescriptor("url", "URL", false, false);
        typeManager.savePropertyDescriptor(pd);

        pd = new PropertyDescriptor("maven.project.id", "Maven Project Id", false, false);
        typeManager.savePropertyDescriptor(pd);

        pd = new PropertyDescriptor("maven.artifact.id", "Maven Artifact Id", false, false);
        typeManager.savePropertyDescriptor(pd);

        pd = new PropertyDescriptor("issue.tracker", "Issue Tracker", false, false);
        typeManager.savePropertyDescriptor(pd);

        pd = new PropertyDescriptor("ci.server", "CI Server", false, false);
        typeManager.savePropertyDescriptor(pd);

        pd = new PropertyDescriptor("scm", "Source Control", false, false);
        typeManager.savePropertyDescriptor(pd);
        
        pd = new PropertyDescriptor("contacts", "Contacts", false, false);
        pd.setExtension(registry.getExtension("userExtension"));
        typeManager.savePropertyDescriptor(pd);
    }

    private void add(Item workspace, String name, String resource) 
        throws Exception {
        NewItemResult result = workspace.newItem(name, typeManager.getType(TypeManager.ARTIFACT));
        Item artifact = (Item) result.getItem();

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("artifact", new Object[] { getClass().getResourceAsStream(resource), "application/xml" });
        artifact.newItem("0.1", typeManager.getType(TypeManager.ARTIFACT_VERSION), props);
    }
    
    public int getVersion() {
        return 1;
    }

}
