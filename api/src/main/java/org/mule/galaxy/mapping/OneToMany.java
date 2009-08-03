package org.mule.galaxy.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
    String mappedBy() default "";
    Class componentType() default Object.class;
    boolean treatAsField() default false;
}
