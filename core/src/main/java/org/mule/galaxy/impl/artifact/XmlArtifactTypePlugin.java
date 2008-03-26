/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.impl.view.CustomArtifactTypeView;
import org.mule.galaxy.impl.view.MvelColumn;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.plugins.config.jaxb.ColumnType;
import org.mule.galaxy.plugins.config.jaxb.ConfigurationType;
import org.mule.galaxy.plugins.config.jaxb.GalaxyArtifactType;
import org.mule.galaxy.plugins.config.jaxb.IndexType;
import org.mule.galaxy.plugins.config.jaxb.NamespaceType;
import org.mule.galaxy.plugins.config.jaxb.ViewType;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.util.TemplateParser;
import org.mule.galaxy.view.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Node;

/**
 * TODO
 */
public class XmlArtifactTypePlugin extends AbstractArtifactPlugin
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(XmlArtifactTypePlugin.class);

    protected PolicyManager policyManager;

    private GalaxyArtifactType pluginXml;
    private List<QName> pluginQNames;
    private TemplateParser parser = TemplateParser.createAntStyleParser();
    
    public XmlArtifactTypePlugin(GalaxyArtifactType pluginXml)
    {
        this.pluginXml = pluginXml;
    }

    public void setPolicyManager(PolicyManager policyManager)
    {
        this.policyManager = policyManager;
    }

    @Override
    public String getName() {
        return super.getName() + "-" + pluginXml.getName();
    }

    @Override
    public void doInstall() throws Exception
    {
        // Is there is no namespace we can assume that this is just a
        // placeholder pluging for a generic
        // type of artifact
        if (pluginXml.getNamespace().size() == 0)
        {
            artifactTypeDao.save(new ArtifactType(pluginXml.getName(), pluginXml.getContentType()));
            return;
        }

        pluginQNames = new ArrayList<QName>(pluginXml.getNamespace().size());
        int i = 0;
        for (Iterator<NamespaceType> iterator = pluginXml.getNamespace().iterator(); iterator.hasNext(); i++)
        {
            NamespaceType ns = iterator.next();
            pluginQNames.add(getQName(ns));
        }

        artifactTypeDao.save(new ArtifactType(pluginXml.getName(), pluginXml.getContentType(), pluginQNames));

        Properties props = new Properties(System.getProperties());
        String prefix = "";
        i = 2;
        for (Iterator<QName> iterator = pluginQNames.iterator(); iterator.hasNext(); i++)
        {
            QName qName = iterator.next();
            props.setProperty("namespace.uri" + prefix, qName.getNamespaceURI());
            props.setProperty("namespace.local-name" + prefix, qName.getLocalPart());
            props.setProperty("namespace.prefix" + prefix, qName.getPrefix());
            prefix = "." + i;
        }

        if (pluginXml.getIndexes() != null)
        {
            List<IndexType> indexes = pluginXml.getIndexes().getIndex();
            for (IndexType indexType : indexes)
            {
                ConfigurationType configType = indexType.getConfiguration();
                HashMap<String, String> config = new HashMap<String, String>();
                if (configType != null)
                {
                    populateConfiguration(configType, config, props);
                }
                Index idx = new Index(indexType.getFieldName(),
                                      indexType.getDisplayName(),
                                      pluginXml.getContentType(),
                                      getQName(indexType.getNamespace()),
                                      ClassUtils.resolveClassName(indexType.getSearchInputType(),
                                                                  getClass().getClassLoader()),
                                      indexType.getIndexer(),
                                      config);

                indexManager.save(idx, true);

                if (logger.isDebugEnabled())
                {
                    logger.debug("Created index: " + idx);
                }
            }
        }
    }

    private void populateConfiguration(ConfigurationType configType, HashMap<String, String> config, Properties templateProps) {
        if (configType.getAny() == null) return;
        
        for (Object o : configType.getAny()) {
            Node n = (Node) o;
            String val = org.mule.galaxy.util.DOMUtils.getContent(n);
            
            if (val != null) {
                val = parser.parse(templateProps, val);
                config.put(n.getLocalName(), val.trim());
            }
        }
    }

    protected QName getQName(NamespaceType namespaceType)
    {
        if (namespaceType == null)
        {
            if (pluginQNames.size() == 1)
            {
                return pluginQNames.get(0);
            }
            else if (pluginQNames.size() == 0)
            {
                throw new IllegalArgumentException("No plugin namespace set");
            }
            else
            {
                throw new IllegalArgumentException(
                        "There is more than one Namespace associated with this plugin, you cannot inherit the namespace.");
            }
        }

        if (namespaceType.getUri() != null)
        {
            return new QName(namespaceType.getUri(), namespaceType.getLocalName());
        }
        else
        {
            return new QName(namespaceType.getLocalName());
        }
    }

    @Override
    protected void doInitialize() throws Exception
    {
        if (pluginXml.getViews() == null || pluginQNames == null)
        {
            return;
        }

        List<ViewType> views = pluginXml.getViews().getView();

        for (ViewType viewType : views)
        {
            CustomArtifactTypeView view = new CustomArtifactTypeView();

            List<ColumnType> columns = viewType.getColumn();
            for (final ColumnType column : columns)
            {
                // Create a custom view

                Integer colNumber = column.getColumn();
                Column c = new Column(column.getName(), column.isSummary(), column.isDetail(),
                                      new MvelColumn(column.getExpression()));

                if (colNumber == null)
                {
                    view.getColumns().add(c);
                }
                else
                {
                    view.getColumns().add(colNumber, c);
                }
            }

            if (pluginQNames.size() == 0)
            {
                throw new IllegalArgumentException(
                        "Unabled to select Namespace for view, there is either none or more than one namespace set on the plugin");
            }
            viewManager.addView(view, pluginQNames);


        }
    }

    public int getVersion() {
        return 1;
    }
}
