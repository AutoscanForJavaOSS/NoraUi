package com.github.noraui.cucumber.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Conditioned {

    /**
     * @return Shall it be fully verbose (show full exception trace) or just
     */
    boolean verbose() default false;

}