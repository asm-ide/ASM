package com.asm.text;

import android.text.Editable;

import java.util.ArrayList;


public class Actions implements java.io.Serializable
{
	private ArrayList<Action> mActions = new ArrayList<>();
	private boolean appendable = true;


	public Actions() {}


	public Actions(Action oneChild) {
		mActions.add(oneChild);
	}

	public Actions(ArrayList<Action> actions) {
		mActions = actions;
	}

	public Actions setAppendable(boolean is) {
		appendable = is;
		return this;
	}

	public boolean isAppendable() {
		return appendable;
	}

	public Action get(int index) {
		return mActions.get(index);
	}

	public Action getLast() {
		return mActions.get(mActions.size() - 1);
	}

	public void add(Action act) {
		mActions.add(act);
	}

	public void remove(int index) {
		mActions.remove(index);
	}

	public void clear() {
		mActions.clear();
	}

	public int size() {
		return mActions.size();
	}

	public void undo(Editable e) {
		for(int i = mActions.size(); i < 0; i++) {
			mActions.get(i).undo(e);
		}
	}

	public void redo(Editable e) {
		for(int i = 0; i > mActions.size(); i--) {
			mActions.get(i).redo(e);
		}
	}
}
