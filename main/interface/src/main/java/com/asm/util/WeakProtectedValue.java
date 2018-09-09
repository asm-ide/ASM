package com.asm.util;

import java.lang.ref.WeakReference;


public class WeakProtectedValue<T> implements Value<T>
{
	private final WeakReference<ProtectedValue<T>> mValue;
	private final String mClassName;
	
	
	public WeakProtectedValue(T value) {
		super();
		
		mValue = new WeakReference<>(new ProtectedValue<T>(value));
		mClassName = Reflection.getCallingStackTrace(2).getClassName();
	}
	
	
	@ProtectedAccess
	@Override
	public T get() {
		Reflection.ensureCallingClass(2, mClassName);
		return mValue.get().get();
	}
	
	@Override
	public void set(T value) {
		Reflection.ensureCallingClass(2, mClassName);
		mValue.get().set(value);
	}
	
	@Override
	public T getOrDefault(T defaultValue) {
		Reflection.ensureCallingClass(2, mClassName);
		return mValue.get().getOrDefault(defaultValue);
	}
	
	public boolean isReferenceExist() {
		return mValue.get() == null;
	}
}
