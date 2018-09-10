package com.asm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;


/**
 * This class, constructor, or method requires specific permission.
 * To use this, your plugin should declare permissions in plugin.xml.
 */
@Documented
@Retention(CLASS)
@Target(TYPE, CONSTRUCTOR, METHOD)
public @interface RequiresPermission
{
	public String[] value();
}
