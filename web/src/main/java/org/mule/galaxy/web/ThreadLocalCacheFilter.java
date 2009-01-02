package org.mule.galaxy.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.mule.galaxy.impl.cache.ThreadLocalCacheProviderFacade;

/**
 * Clears the thread local cache after the servlet is done executing.
 */
public class ThreadLocalCacheFilter implements Filter {

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
        throws ServletException, IOException {
        ThreadLocalCacheProviderFacade.enableCache();
        
        chain.doFilter(req, resp);
        
        ThreadLocalCacheProviderFacade.clearCache();
    }

    public void init(FilterConfig arg0) throws ServletException {
    }

}
