package org.mule.galaxy.util;

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

import java.util.logging.Logger;

public class LogUtils {
    private static final String MESSAGE_BUNDLE = ".Messages";
    
    public static Logger getL7dLogger(Class<?> cls) {
        return Logger.getLogger(cls.getName(), 
                                getBundleName(cls));
    }
    
    public static String getBundleName(Class<?> cls) {
        // Class.getPackage() can return null, so change to another way to get Package Name
        return getPackageName(cls) + MESSAGE_BUNDLE;
        
    }

    public static String getPackageName(Class<?> clazz) {
        String className = clazz.getName();
        if (className.startsWith("[L")) {
            className = className.substring(2);
        }
        return getPackageName(className);
    }

    static String getPackageName(String className) {
        int pos = className.lastIndexOf('.');
        if (pos != -1) {
            return className.substring(0, pos);
        } else {
            return "";
        }
    }
}
