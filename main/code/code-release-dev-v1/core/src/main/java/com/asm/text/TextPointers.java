package com.asm.text;

import android.text.TextWatcher;
import android.text.Editable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * This class is used when wants to mark where is 'the point'
 * on the text.
 * <p>
 * For example, in text "Hello world!", you inserted
 * the pointer between w and o. If text was inserted at front
 * of the pointer, it will be moved to back. If text inserted
 * at the back of the pointer, nothing.
 */
public class TextPointers<T extends TextPointer> extends ArrayList<T> implements TextWatcher
{
	public TextPointers() {
		super();
	}
	
	public TextPointers(int capacity) {
		super(capacity);
	}
	
	
	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after) {
		
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start, int before, int count) {
		for(T item : this) {
			item.onAct(start, before, count);
		}
	}
	
	@Override
	public void afterTextChanged(Editable text) {
		
	}
	
	@Override
	public void add(int index, T element) {
		
	}
	
	public T getAt(int position) {
		for(T item : this) 
			if(item.getPosition() == position)
				return item;
		
		throw new IllegalArgumentException("item at " + position + " is not exist");
	}
	
	public boolean containsAt(int position) {
		for(T item : this) 
			if(item.getPosition() == position)
				return true;

		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void sort() {
		ArrayList<T> list = (ArrayList<T>) clone(); // shallow copy
		clear();
		
		for(int i = 0; i < list.size(); i++) {
			int min = Integer.MAX_VALUE;
			T minItem = null;
			for(T item : list) {
				if(item.getPosition() < min) {
					min = item.getPosition();
					minItem = item;
				}
			}
			super.add(minItem);
			list.remove(minItem);
		}
	}
}
