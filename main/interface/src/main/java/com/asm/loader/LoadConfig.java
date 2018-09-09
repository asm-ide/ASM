package com.asm.loader;

import com.asm.Res;
import com.asm.annotation.NonNull;
import com.asm.plugin.PluginLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;


public class LoadConfig
{
	private static final String BASE_NAME = Res.getString(Res.APP_NAME) + ":";
	private static HashMap<String, Loader> sLoaders = new HashMap<>();
	
	
	/**
	 * LoadConfig cannot be instantiated
	 */
	private LoadConfig() {}
	
	
	public static Map<String, Object> singleMap(Object obj) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("", obj);
		return map;
	}
	
	public static Object fromSingleMap(Map<String, Object> args) {
		return args.get("");
	}
	
	public static Map<String, Object> mapFromList(Object... list) {
		HashMap<String, Object> map = new HashMap<>();
		for(int i = 0; i < list.length; i++) map.put("p" + i, list[i]);
		return map;
	}
	
	public static Object[] listFromMap(Map<String, Object> data) {
		return data.values().toArray();
	}
	
	public static <T> T newInstanceFromData(Class<T> type, Map<String, Object> args) {
		String[] keys = args.keySet().toArray(new String[0]);
		Object[] values = args.values().toArray();
		ArrayList<Object> list = new ArrayList<>();
		for(int i = 0; i < keys.length; i++)
			if(keys[i].startsWith("p")) list.add(values[i]);
		
		Class[] classes = new Class[list.size()];
		for(int i = 0; i < list.size(); i++)
			classes[i] = list.get(i).getClass();
		
		try {
			Constructor<T> constructor = type.getConstructor(classes);
			return constructor.newInstance(values);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void set(String key, @NonNull Loader<?> loader) {
		if(sLoaders.containsKey(key))
			sLoaders.remove(key);
		
		sLoaders.put(key, loader);
	}
	
	@SuppressWarnings("unsafe")
	public static <T> Loader<T> loader(String key) {
		return (Loader<T>) sLoaders.get(BASE_NAME + key);
	}
	
	public static <T> T load(String key, Map<String, Object> args) {
		return LoadConfig.<T>loader(key).load(args);
	}
	

	@SuppressWarnings("unsafe")
	public static <T> Loader<T> loaderPackage(String name) {
		if(!sLoaders.containsKey(name)) {
			try {
				sLoaders.put(name,
					getLoaderFromLoadable(
						(Class<Loadable<T>>) PluginLoader.getClassLoader()
						.loadClass(name)));
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return (Loader<T>) sLoaders.get(name);
	}
	
	public static <T> T loadPackage(String name, Map<String, Object> args) {
		return LoadConfig.<T>loaderPackage(name).load(args);
	}
	
	@SuppressWarnings("unsafe")
	public static <T> Loader<T> getLoaderFromLoadable(Class<Loadable<T>> loadable) {
		try {
			Method getLoader = loadable.getMethod("getLoader");
			getLoader.setAccessible(true);
			return (Loader<T>) getLoader.invoke(null);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T loadFromLoadable(Class<Loadable<T>> loadable, Map<String, Object> args) {
		return getLoaderFromLoadable(loadable).load(args);
	}
	
	public static <T> T loadForClass(Class<?> type, Map<String, Object> args) {
		return LoadConfig.<T>load(type.getName(), args);
	}
}

