package com.asm.util;

import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.ArrayList;


public class ProtectedList<T> extends ProtectedValue<List<T>> implements List<T>
{
	public ProtectedList() {
		super(new ArrayList<T>());
	}
	
	public ProtectedList(List<T> list) {
		super(list);
	}
	
	
	@Override
	public int size() {
		return get().size();
	}
	
	@Override
	public boolean isEmpty() {
		return get().isEmpty();
	}
	
	@Override
	public boolean contains(Object data) {
		return get().contains(data);
	}
	
	@Override
	public Iterator<T> iterator() {
		return get().iterator();
	}
	
	@Override
	public Object[] toArray() {
		return get().toArray();
	}
	
	@Override
	public <T extends Object> T[] toArray(T[] type) {
		return get().toArray(type);
	}
	
	@Override
	public boolean add(T data) {
		return get().add(data);
	}
	
	@Override
	public boolean remove(Object data) {
		return get().remove(data);
	}
	
	@Override
	public boolean containsAll(Collection<?> list) {
		return get().containsAll(list);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return get().addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return get().addAll(index, c);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return get().removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return get().retainAll(c);
	}
	
	@Override
	public void clear() {
		get().clear();
	}
	
	@Override
	public T get(int index) {
		return get().get(index);
	}
	
	@Override
	public T set(int index, T value) {
		return get().set(index, value);
	}
	
	@Override
	public void add(int index, T value) {
		get().add(index, value);
	}
	
	@Override
	public T remove(int index) {
		return get().remove(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return get().indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return get().lastIndexOf(o);
	}
	
	@Override
	public ListIterator<T> listIterator() {
		return get().listIterator();
	}
	
	@Override
	public ListIterator<T> listIterator(int index) {
		return get().listIterator(index);
	}
	
	@Override
	public List<T> subList(int start, int end) {
		return get().subList(start, end);
	}
}
