package com.asm.util;

import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.ArrayList;


@ProtectedValuePolicy(requiresAnnotation = false)
public class ProtectedList<T> implements Value<T>, List<T>
{
	private final ProtectedValue<List<T>> mValue;
	
	
	public ProtectedList() {
		mValue = new ProtectedValue<>(new ArrayList<T>());
	}
	
	
	class PrA<T> extends ProtectedValue<T>
	{
		public PrA(T in) {
			super(in);
		}
		
		@Override
		protected int getDepth() {
			return 3;
		}
	}
	
	
	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean contains(Object p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public Iterator<T> iterator()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Object[] toArray()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public <T extends Object> T[] toArray(T[] p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean add(T p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean remove(Object p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean addAll(int p1, Collection<? extends T> p2)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void clear()
	{
		// TODO: Implement this method
	}

	@Override
	public T get(int p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public T set(int p1, T p2)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public void add(int p1, T p2)
	{
		// TODO: Implement this method
	}

	@Override
	public T remove(int p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public int indexOf(Object p1)
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int lastIndexOf(Object p1)
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public ListIterator<T> listIterator()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public List<T> subList(int p1, int p2)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public void set(T value)
	{
		// TODO: Implement this method
	}

	@Override
	public T get()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public T getOrDefault(T defaultValue)
	{
		// TODO: Implement this method
		return null;
	}
	
	
}
