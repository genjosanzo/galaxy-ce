package org.mule.galaxy.config;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Bean definition parser for the &lt;g:custom-listeners> configuration element in Galaxy.
 */
public class CustomListenersBeanDefinitionParser extends AbstractBeanDefinitionParser {

    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        // this is the bean peforming the registration of custom listeners
        BeanDefinitionBuilder listenerRegBean = BeanDefinitionBuilder.rootBeanDefinition(CustomListenersBean.class);
        Element emElement = DomUtils.getChildElementByTagName(element, "eventManager");
        if (emElement != null) {
            // if the user overrides the default eventManager
            final String beanName = emElement.getAttribute("ref");
            listenerRegBean.addDependsOn(beanName);
            listenerRegBean.addPropertyReference("eventManager", beanName);
        }

        List<Element> listenerElements =  DomUtils.getChildElementsByTagName(element, "listener");
        ManagedList listeners = new ManagedList(listenerElements.size());
        for (Element listenerElement : listenerElements) {
            // parse nested spring bean definitions
            Element bean = DomUtils.getChildElementByTagName(listenerElement, "bean");
            final BeanDefinitionParserDelegate beanParserDelegate = new BeanDefinitionParserDelegate(parserContext.getReaderContext());
            // need to init defaults
            beanParserDelegate.initDefaults(bean);
            BeanDefinitionHolder listener = beanParserDelegate.parseBeanDefinitionElement(bean);
            listeners.add(listener);
        }

        listenerRegBean.addPropertyValue("customListeners", listeners);

        return listenerRegBean.getBeanDefinition();
    }

    @Override
    protected boolean shouldGenerateId() {
        // auto-generate a unique bean id
        return true;
    }
}
