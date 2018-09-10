package com.asm.annotation;

import com.asm.plugin.Plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Shows that class is included in API and can be loaded as plugin.
 * If you put this annotation on {@link Loadable} class, you don't
 * need to declare {@code Loader&lt;<b>your class</b>&rt; getLoader()}.
 * When you use {@link Loadable} class with this annotation, put 
 * {@link LoadConfig#}
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface PluginAPI
{
	/** module name. */
	public Class<? extends Plugin> value();
}
