package com.asm.util;

import com.asm.plugin.PluginLoader;

import java.lang.reflect.Method;


import static com.asm.Consts.DEBUG;
import static com.asm.Consts.DEBUG_DOTHROW;


/**
 * This is the utility class to avoid reflections.
 */
public final class Reflection
{
	/**
	 * This returns information about what class is callig this.
	 * 1 depth to return your method's StackTrace, 0 depth to this
	 * self function.
	 */
	public static StackTraceElement getCallingStackTrace(int depth) {
		StackTraceElement[] el = new Throwable().getStackTrace();
		if(el.length < depth)
			throw new IllegalArgumentException("StackTraceElement count < depth");
		
		return el[depth];
	}
	
	
	public static boolean isClassNameMatch(int depth, String name) {
		return getCallingStackTrace(depth + 1).getClassName().equals(name);
	}
	
	@SuppressWarnings("unsafe")
	public static void checkProtected(int depth, String className, boolean isRead) {
		boolean requiresGetClass = true;
		boolean requiresAnnotation = true;
		ProtectedValuePolicy policy = null;
		
		try {
			// get annotation
			Class clazz = PluginLoader.getClassLoader().loadClass(className);
			policy = (ProtectedValuePolicy) clazz.getAnnotation(ProtectedValuePolicy.class);
			if(policy != null) {
				requiresGetClass = policy.requiresGetClass();
				requiresAnnotation = policy.requiresAnnotation();
			}
		} catch(Exception e) {
			//
		}
		
		// if not requires permission on read
		if(!requiresGetClass && isRead)
			return;
		
		try {
			// get information about caller
			StackTraceElement callerStack = getCallingStackTrace(2);
			String callerName = callerStack.getClassName();
			
			// check caller class
			if(!className.equals(callerName))
				throw new SecurityException("caller class is different from created class");
			
			// from here, security is provided
			
			// not need to check annotation
			if(!requiresAnnotation)
				return;
			
			Class callerClass = PluginLoader.getClassLoader().loadClass(callerName);
			String callerMethodName = callerStack.getMethodName();
			
			// find matching method
			
			// do not check if !DEBUG
			if(!DEBUG) return;
			
			// WARNING: if there is methods whose name is same and arguments are different,
			// and put annotation on only only one method, it affects to other method.
			Method[] methods = callerClass.getDeclaredMethods();
			for(Method method : methods) {
				if(callerMethodName.equals(method.getName()) && method.isAnnotationPresent(ProtectedAccess.class)) {
					return;
				}
			}
			
			// no method with @ProtectedAccess
			Log.get("ProtectedValue").e("Security warning: " + callerMethodName + "(...) uses ProtectedValue but didn't put @ProtectedAccess");
			
			if(DEBUG_DOTHROW)
				throw new SecurityException(callerMethodName + "(...) uses ProtectedValue but didn't put @ProtectedAccess");
		} catch(Exception e) {
			
		}
	}
	
	
	/**
	 * Ensures that calling class matches.
	 * If not match, throws an {@link SecurityException}.
	 */
	public static void ensureCallingClass(int depth, String... classes) {
		// depth + 1; because ensureCallingClass() is also included in stack trace
		// Oops; I might missed it
		String calling = getCallingStackTrace(depth + 1).getClassName();
		
		if(calling == null) // in this case, ???
							// 이게 뭐꼬? -AppMagnisium
			return; // just return; this will be never happen
		
		for(String clazz : classes)
			if(calling.equals(clazz)) return;
		
		// not match; did you dares to break the jail??
		throw new SecurityException();
	}
}
