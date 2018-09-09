package com.asm;

import com.asm.util.PermissionChecker;
import com.asm.util.Reflection;

import java.lang.reflect.Method;


public final class ModulePermission
{
	public static void enforcePermission(int depth) {
		enforcePermission(depth + 1, "Module permission not granted");
	}
	
	public static void enforcePermission(int depth, String msg) {
		if(!hasPermission(depth + 1))
			throw new SecurityException(msg);
	}
	
	public static boolean hasPermission(int depth) {
		return hasPermission(Reflection.getCallingStackTrace(depth));
	}
	
	public static boolean hasPermission(StackTraceElement el) {
		// test #1: is granted?
		
		// test #2: is external module?
		if(isExternalModule(el)) return false;
		
		
		
		return true;
	}
	
	private static boolean isExternalModule(StackTraceElement el) {
		try {
			// because, plugins cannot be loaded by system class loader.
			// TODO: test it
			ClassLoader.getSystemClassLoader()
				.loadClass(el.getClassName());
		} catch(Exception e) {
			// not found in apk
			return true;
		} finally {
			return false;
		}
	}
	
	
}
