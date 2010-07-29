package org.mule.galaxy.impl.security;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.security.AccessControlManager;
import org.springframework.beans.factory.FactoryBean;

public class PermissionsEnforcingProxy implements FactoryBean {
    private Map<String, String> methodPermissions = new HashMap<String, String>();
    private AccessControlManager accessControlManager;
    private Class proxyClass;
    private Object target;
    
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), 
                                      new Class[] { proxyClass }, 
                                      new PermissionInvocationHandler());
    }

    public Class getObjectType() {
        return proxyClass;
    }

    public boolean isSingleton() {
        return true;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Map<String, String> getMethodPermissions() {
        return methodPermissions;
    }

    public void setMethodPermissions(Map<String, String> permissionsToMethod) {
        this.methodPermissions = permissionsToMethod;
    }

    public AccessControlManager getAccessControlManager() {
        return accessControlManager;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public Class getProxyClass() {
        return proxyClass;
    }

    public void setProxyClass(Class proxyClass) {
        this.proxyClass = proxyClass;
    }
    
    private final class PermissionInvocationHandler implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String perm = methodPermissions.get(method.getName());
            
            if (perm != null) {
                accessControlManager.assertAccess(perm);
            }
            return method.invoke(target, args);
        }
        
    }
}
