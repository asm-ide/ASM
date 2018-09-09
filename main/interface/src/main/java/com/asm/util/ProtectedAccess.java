package com.asm.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * This annotation is put to some methods(constructors).
 * @see ProtectedValuePolicy
 * If requiresAnnotation=true, all method which access to
 * {@link ProtectedValue} should put {@code @ProtectedAccess}.
 */
@Documented
@Retention(RUNTIME)
@Target(CONSTRUCTOR, METHOD)
public @interface ProtectedAccess
{
}
