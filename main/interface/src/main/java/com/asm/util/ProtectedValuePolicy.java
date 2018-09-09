package com.asm.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Annotation used how much security needed.
 * <p>
 * set {@code requiresAnnotation=true} to enforce only method with
 * {@code @ProtectedAccess} can be set value.
 * <br>
 * set {@code requiresGetClass=true} to enforce only class what
 * instantiated this class can get. (but still other class can
 * access by using reflection)
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE, CONSTRUCTOR, METHOD)
public @interface ProtectedValuePolicy
{
	/**
	 * This declares whether when calls some permission needed functions
	 * ({@link ProtectedValue#set()} or {@link ProtectedValue#get()} if
	 * {@link #requiresGetClass()} is true), that function needs to put
	 * \@{@link ProtectedAccess}.
	 */
	public boolean requiresAnnotation() default true;
	
	/**
	 * This declares whether {@link ProtectedValue#get()} needs to called
	 * the same class that created {@link ProtectedValue} instance.
	 */
	public boolean requiresGetClass() default true;
}
