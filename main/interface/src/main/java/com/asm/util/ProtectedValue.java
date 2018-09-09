package com.asm.util;

import java.lang.annotation.Annotation;


// Legacy security code.
// will be updated before release


/**
 * This provides protection about avoiding reflection injection.
 * TODO: WARNING: this does not provide protection feature yet.
 */
public class ProtectedValue<T> implements Value<T>
{
	private static class NullObject {}
	
	private static final NullObject NULL = new NullObject();
	
	
	
	static {
		// loadLibrary here
		//System.loadLibrary("asm-security");
	}
	
	
	/** will be destoryed and replaced by long native pointer. */
	private Object mValue;
	
	/** across by jni. */
	private final String mClassName;
	
	/** annotation config. */
	private final boolean mRequiresAnnotation;
	private final boolean mRequiresGetClass;
	
	
	@SuppressWarnings("unsafe")
	public ProtectedValue(T value) {
		this.mValue = value;
		this.mClassName = Reflection.getCallingStackTrace(getDepth()).getClassName();
		
		try {
			Class clazz = Class.forName(mClassName);
			ProtectedValuePolicy anno = (ProtectedValuePolicy) clazz.getAnnotation(ProtectedValuePolicy.class);
			
			mRequiresAnnotation = anno.requiresAnnotation();
			mRequiresGetClass = anno.requiresGetClass();
		} catch(Exception e) {
			mRequiresAnnotation = false;
			mRequiresGetClass = false;
		}
	}
	
	
	@SuppressWarnings("unsafe")
	@Override
	public T get() {
		if(mValue instanceof NullObject)
			return null;
			
		return (T) mValue;
	}
	
	@Override
	public T getOrDefault(T defaultValue) {
		T obj = get();
		return obj == null? defaultValue : obj;
	}
	
	@Override
	public void set(T value) {
		mValue = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ProtectedValue)
			return ((ProtectedValue) obj).get().equals(get());
		else
			return obj.equals(get());
	}
	
	public String getCreatedClass() {
		return mClassName;
	}
	
	protected int getDepth() {
		return 2;
	}
	
	// called by native
	@SuppressWarnings("UnusedDeclaration")
	private void doCheckAccess() {
		checkAccess(getDepth() + 1); // TODO: if native applied, change +1
	}
	
	protected void checkAccess(int depth) {
		Reflection.ensureCallingClass(depth, mClassName);
	}
	
	
	// will cause UnsatisfiedLinkError
	public native void nativeSet(Object value);
}
