package com.asm.util;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;


public class ProtectedMap<K, V> implements Map<K,V>, Value<Map<K, V>>
{
	private final ProtectedValue<Map<K, V>> mValue;
	private final String mClassName;
	
	
	public ProtectedMap(Map<K, V> data) {
		super();
		
		mValue = new ProtectedValue<>(data);
		mClassName = Reflection.getCallingStackTrace(2).getClassName();
	}
	
	public ProtectedMap() {
		super();
		
		mValue = new ProtectedValue<>(new HashMap<K, V>());
		mClassName = Reflection.getCallingStackTrace(2).getClassName();
	}
	
	
	private void checkAccess(boolean isRead) {
		Reflection.checkProtected(3, mClassName, isRead);
	}
	
	
	@Override
	public V put(K key, V value) {
		checkAccess(false);
		return get0().put(key, value);
	}
	
	@Override
	public Map<K, V> get() {
		checkAccess(true);
		return get0();
	}
	
	@ProtectedAccess
	private Map<K, V> get0() {
		return mValue.get();
	}
	
	@Override
	@ProtectedAccess
	public void set(Map<K, V> value) {
		checkAccess(false);
		mValue.set(value);
	}
	
	@Override
	public int size() {
		checkAccess(true);
		return get0().size();
	}
	
	@Override
	public boolean isEmpty() {
		checkAccess(true);
		return get0().isEmpty();
	}
	
	@Override
	public boolean containsKey(Object key) {
		checkAccess(true);
		return get0().containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		checkAccess(true);
		return get0().containsValue(value);
	}
	
	@Override
	public V get(Object key) {
		checkAccess(true);
		return get0().get(key);
	}
	
	@Override
	public V remove(Object key) {
		checkAccess(false);
		return get0().remove(key);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		checkAccess(false);
		get0().putAll(map);
	}
	
	@Override
	public void clear() {
		checkAccess(false);
		get0().clear();
	}
	
	@Override
	public Set<K> keySet() {
		checkAccess(false);
		return get().keySet();
	}
	
	@Override
	public Collection<V> values() {
		checkAccess(false);
		return get().values();
	}
	
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		checkAccess(false);
		return get().entrySet();
	}
	
	@Override
	public Map<K, V> getOrDefault(Map<K, V> defaultValue) {
		checkAccess(false);
		return mValue.getOrDefault(defaultValue);
	}
	
}
