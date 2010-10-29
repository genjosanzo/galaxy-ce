package org.mule.galaxy.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
    String mappedBy() default "";

    /**
     * Whether or not the items in this collection should be dereferenced and stored outside
     * the parent node.
     * @return
     */
    boolean deref() default true;
}
