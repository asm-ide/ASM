package com.asm.loader;

import java.util.Map;


public interface Loader<T>
{
	public T load(Map<String, Object> args);
}
