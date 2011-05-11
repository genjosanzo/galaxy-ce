package org.mule.galaxy.impl.security;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.security.AccessControlManager;
import org.springframework.beans.factory.FactoryBean;

public class PermissionsEnforcingProxy implements FactoryBean<Object> {

    private Map<String, String> methodPermissions = new HashMap<String, String>();
    private AccessControlManager accessControlManager;
    private Class<?> proxyClass;
    private Object target;

    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), 
                                      new Class[] { proxyClass }, 
                                      new PermissionInvocationHandler());
    }

    public Class<?> getObjectType() {
        return proxyClass;
    }

    public boolean isSingleton() {
        return true;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(final Object target) {
        this.target = target;
    }

    public Map<String, String> getMethodPermissions() {
        return methodPermissions;
    }

    public void setMethodPermissions(final Map<String, String> permissionsToMethod) {
        this.methodPermissions = permissionsToMethod;
    }

    public AccessControlManager getAccessControlManager() {
        return accessControlManager;
    }

    public void setAccessControlManager(final AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public Class<?> getProxyClass() {
        return proxyClass;
    }

    public void setProxyClass(final Class<?> proxyClass) {
        this.proxyClass = proxyClass;
    }
    
    protected void checkPermission(final String perm) {
        accessControlManager.assertAccess(perm);
    }

    private final class PermissionInvocationHandler implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final String perm = methodPermissions.get(method.getName());
            if (perm != null) {
                checkPermission(perm);
            }
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

}