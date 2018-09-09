package com.asm.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * This class is related to {@link ProtectedValue} and if you
 * want to deal with some class or method with this annotation,
 * some class created that class and some class that using that
 * class should be same, likely {@link ProtectedValue}.
 * <p>
 * If this applied to whole class, all method of it requires
 * same class. However, calling constructor do not requires 
 * class, of course.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE, METHOD)
public @interface RequiresClass
{
}
