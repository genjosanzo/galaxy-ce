/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.api;

/**
 * TODO
 */

public class PropertyDescriptor implements Identifiable
{
    private String property;
        private String description;
        private boolean multivalued;

        public PropertyDescriptor(String property, String name, boolean multivalued) {
            super();
            this.property = property;
            this.description = name;
            this.multivalued = multivalued;
        }
        public PropertyDescriptor() {
            super();
        }

        public String getId() {
            return getProperty();
        }
        public void setId(String id) {
            setProperty(id);
        }

        public String getProperty() {
            return property;
        }
        public void setProperty(String property) {
            this.property = property;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String name) {
            this.description = name;
        }
        public boolean isMultivalued() {
            return multivalued;
        }
        public void setMultivalued(boolean multivalued) {
            this.multivalued = multivalued;
        }

    }
