package com.asm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;


/**
 * This class, constructor, or method requires specific module.
 * Your plugin cannot use this.
 */
@Documented
@Retention(CLASS)
@Target(TYPE, CONSTRUCTOR, METHOD)
public @interface RequiresModule
{}
