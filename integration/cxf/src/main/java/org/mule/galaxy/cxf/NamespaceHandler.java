/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.mule.galaxy.cxf;

import org.apache.cxf.configuration.spring.SimpleBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;

import org.w3c.dom.Element;

public class NamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("galaxy", new SimpleBeanDefinitionParser(GalaxyFeature.class) {

            @Override
            protected void mapElement(ParserContext ctx, BeanDefinitionBuilder bean, Element e, String name) {
                if (name.equals("policyQuery")) {
                    bean.addPropertyValue("policyQueries", org.apache.cxf.helpers.DOMUtils.getContent(e));
                } else {
                    super.mapElement(ctx, bean, e, name);
                }
            }

            @Override
            protected boolean shouldGenerateId() {
                return true;
            }
            
        });
    }
}
