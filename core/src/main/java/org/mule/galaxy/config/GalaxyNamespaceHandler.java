package org.mule.galaxy.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class GalaxyNamespaceHandler extends NamespaceHandlerSupport{

    public void init() {
        registerBeanDefinitionParser("custom-listeners", new CustomListenersBeanDefinitionParser());
    }
}
