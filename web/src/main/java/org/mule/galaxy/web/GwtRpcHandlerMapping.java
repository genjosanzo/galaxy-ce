package org.mule.galaxy.web;

import org.springframework.beans.BeansException;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

/**
 * The sole function of this class is to make registerHandler a public method.
 */
public class GwtRpcHandlerMapping extends AbstractUrlHandlerMapping {

    @Override
    public void registerHandler(String urlPath, Object handler) throws BeansException,
        IllegalStateException {
        super.registerHandler(urlPath, handler);
    }

}
