package com.asm.util;


public interface Value<T>
{
	public void set(T value);
	public T get();
	public T getOrDefault(T defaultValue);
	public boolean equals(Object obj);
}
