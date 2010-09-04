package org.mule.galaxy.impl.security;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.mule.galaxy.util.SecurityUtils;
import org.springmodules.cache.key.CacheKeyGenerator;
import org.springmodules.cache.key.HashCodeCacheKey;
import org.springmodules.cache.key.HashCodeCalculator;
import org.springmodules.cache.util.Reflections;
import org.springmodules.util.Objects;

/**
 * A modified version of the spring modules class that takes into account the 
 * current user who is invoking the method.
 * <p>
 * Generates the key for a cache entry using the hashCode of the intercepted
 * method and its arguments.
 * </p>
 * 
 * @author Alex Ruiz
 */
public class UserHashCodeCacheKeyGenerator implements CacheKeyGenerator {

    /**
     * Flag that indicates if this generator should generate the hash code of
     * the arguments passed to the method to apply caching to. If
     * <code>false</code>, this generator uses the default hash code of the
     * arguments.
     */
    private boolean generateArgumentHashCode;

    /**
     * Construct a <code>HashCodeCacheKeyGenerator</code>.
     */
    public UserHashCodeCacheKeyGenerator() {
        super();
    }

    /**
     * Construct a <code>HashCodeCacheKeyGenerator</code>.
     * 
     * @param generateArgumentHashCode
     *            the new value for the flag that indicates if this generator
     *            should generate the hash code of the arguments passed to the
     *            method to apply caching to. If <code>false</code>, this
     *            generator uses the default hash code of the arguments.
     */
    public UserHashCodeCacheKeyGenerator(boolean generateArgumentHashCode) {
        this();
        setGenerateArgumentHashCode(generateArgumentHashCode);
    }

    /**
     * @see CacheKeyGenerator#generateKey(MethodInvocation)
     */
    public final Serializable generateKey(MethodInvocation methodInvocation) {
        HashCodeCalculator hashCodeCalculator = new HashCodeCalculator();

        Method method = methodInvocation.getMethod();
        hashCodeCalculator.append(System.identityHashCode(method));

        Object[] methodArguments = methodInvocation.getArguments();
        if (methodArguments != null) {
            int methodArgumentCount = methodArguments.length;

            for (int i = 0; i < methodArgumentCount; i++) {
                Object methodArgument = methodArguments[i];
                int hash = 0;

                if (generateArgumentHashCode) {
                    hash = Reflections.reflectionHashCode(methodArgument);
                } else {
                    hash = Objects.nullSafeHashCode(methodArgument);
                }

                hashCodeCalculator.append(hash);
            }
        }
        
        // append the logged in user so we perms don't screw things up
        hashCodeCalculator.append(SecurityUtils.getCurrentUser().hashCode());

        long checkSum = hashCodeCalculator.getCheckSum();
        int hashCode = hashCodeCalculator.getHashCode();

        Serializable cacheKey = new HashCodeCacheKey(checkSum, hashCode);
        return cacheKey;
    }

    /**
     * Sets the flag that indicates if this generator should generate the hash
     * code of the arguments passed to the method to apply caching to. If
     * <code>false</code>, this generator uses the default hash code of the
     * arguments.
     * 
     * @param newGenerateArgumentHashCode
     *            the new value of the flag
     */
    public final void setGenerateArgumentHashCode(boolean newGenerateArgumentHashCode) {
        generateArgumentHashCode = newGenerateArgumentHashCode;
    }
}
