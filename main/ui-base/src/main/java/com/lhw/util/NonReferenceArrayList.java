package com.lhw.util;

import java.io.Serializable;


public class NonReferenceArrayList implements Serializable
{
	public static interface Listener
	{
		public Object copyOf(Object arr, int len);
	}
	
	
	private static final int CAPACITY = 16;
	
	private int len;
	private int capacity;
	private Object arr;
	transient private Listener l;
	
	// TODO : catch wrong index bound and throw exception
	
	public NonReferenceArrayList(Object arr, int len) {
		this(arr, len, null);
	}
	
	public NonReferenceArrayList(Object arr, int len, Listener l) {
		this.arr = arr;
		this.len = len;
		this.capacity = len;
		this.l = l;
	}
	
	public void newCapacity(int newLen) {
		int lastCap = capacity;
		len = newLen;
		capacity = (int) Math.ceil((float) newLen / CAPACITY) * CAPACITY;
		if(capacity != lastCap) {
			arr = l.copyOf(arr, capacity);
		}
	}
	
	public void insert(int where, Object data, int start, int end) {
		int dataLen = end - start;
		int lastLen = len;
		newCapacity(lastLen + dataLen);
		System.arraycopy(arr, where, arr, where + dataLen, lastLen - where + dataLen);
		System.arraycopy(data, start, arr, where, lastLen);
	}
	
	public void append(Object data, int start, int end) {
		int dataLen = end - start;
		newCapacity(len + dataLen);
		System.arraycopy(data, start, arr, len, dataLen);
		len += dataLen;
	}
	
	public void delete(int start, int end) {
		int dataLen = end - start;
		System.arraycopy(arr, end, arr, start, len - end);
		arr = l.copyOf(arr, len - dataLen);
		len -= dataLen;
	}
	
	public Object get() {
		capacity = len;
		return arr = l.copyOf(arr, len);
	}
	
	public int length() {
		return len;
	}
	
	public void clear() {
		arr = l.copyOf(arr, 0);
	}
	
	public void setListener(Listener l) {
		this.l = l;
	}
}
