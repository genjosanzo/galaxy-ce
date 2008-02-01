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

package org.mule.galaxy.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * A container for static utility methods related to resource bundle
 * naming conventons.
 */
public final class BundleUtils {
    /**
     * The default resource bundle naming convention for class is a.b.c is a.b.Messages
     */
    private static final String MESSAGE_BUNDLE = ".Messages";

    /**
     * Prevents instantiation.
     */
    private BundleUtils() {
    }

    /**
     * Encapsulates the logic related to naming the default resource bundle
     * for a class. 
     *
     * @param cls the Class requiring the bundle
     * @return an appropriate ResourceBundle name
     */
    public static String getBundleName(Class<?> cls) {
        // Class.getPackage() can return null, so change to another way to get Package Name
        return PackageUtils.getPackageName(cls) + MESSAGE_BUNDLE;
        
    }
    
    /**
     * Encapsulates the logic related to naming the resource bundle
     * with the given relative name for a class. 
     *
     * @param cls the Class requiring the bundle
     * @return an appropriate ResourceBundle name
     */
    public static String getBundleName(Class<?> cls, String name) {
        return PackageUtils.getPackageName(cls) + "." + name;
    }

    /**
     * Encapsulates the logic related to locating the default resource bundle
     * for a class. 
     *
     * @param cls the Class requiring the bundle
     * @return an appropriate ResourceBundle
     */
    public static ResourceBundle getBundle(Class<?> cls) {
        
        try {
            return ResourceBundle.getBundle(getBundleName(cls),
                                        Locale.getDefault(),
                                        cls.getClassLoader());
        } catch (MissingResourceException ex) {
            return ResourceBundle.getBundle(getBundleName(cls),
                                            Locale.getDefault(),
                                            Thread.currentThread().getContextClassLoader());
            
        }
    }
    
    /**
     * Encapsulates the logic related to locating the resource bundle with the given 
     * relative name for a class.
     *
     * @param cls the Class requiring the bundle
     * @param name the name of the resource
     * @return an appropriate ResourceBundle
     */
    public static ResourceBundle getBundle(Class<?> cls, String name) {
        try {
            return ResourceBundle.getBundle(getBundleName(cls, name),
                                            Locale.getDefault(),
                                            cls.getClassLoader());
        } catch (MissingResourceException ex) {
            return ResourceBundle.getBundle(getBundleName(cls, name),
                                            Locale.getDefault(),
                                            Thread.currentThread().getContextClassLoader());
            
        }
    }
}
