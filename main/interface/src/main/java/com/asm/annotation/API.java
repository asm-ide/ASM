package com.asm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;


/**
 * Shows that class is included in API.
 * This API is part of ASM, and can access by application or plugin.
 */
@Documented
@Retention(CLASS)
@Target(TYPE)
public @interface API
{}
