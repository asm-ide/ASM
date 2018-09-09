package com.asm.loader;


/**
 * Marker interface that marks that class can loaded by {@link Loader}.
 * If you implement this, you should make {@code protected static Loader&lt;<b>your class</r>&rt; getLoader()}.
 * <p>
 * You can load object by: {@link LoadConfig#loadFromLoadable(Loadable<T>, Map<String, Object>)}.
 */
public interface Loadable<T>
{
}
