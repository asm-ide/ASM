package com.asm.text;

import android.text.Editable;


public class Action implements java.io.Serializable
{
	public static final byte ACTION_INSERT = 0;
	public static final byte ACTION_REPLACE = 1;
	public static final byte ACTION_DELETE = 2;

	public CharSequence text;
	public CharSequence text2;
	public int pos;


	public Action() {}


	public Action(int pos, CharSequence text, CharSequence text2) {
		this.text = text;
		this.text2 = text2;
		this.pos = pos;
	}


	public boolean edit(CharSequence txt, CharSequence lastText, int start, int before, int count, boolean appendAction) {
		switch(getAction()) {
			case Action.ACTION_INSERT:
				if(pos + text.length() == start) appendAction = true;
				if(appendAction) {
					text += txt.subSequence(start, start + count).toString();
					//lastAct.dbg();
				}
				break;
			case Action.ACTION_REPLACE: break;
			case Action.ACTION_DELETE:
				if(pos - before == start) appendAction = true;
				if(appendAction) {
					text = lastText.toString() + text;
				}
				break;
			default: throw new IllegalStateException();
		}
		return appendAction;
	}

	public void undo(Editable e) {
		//Log.d("undo", pos + " " + e.toString() + "," + text + "," + text2);
		switch(getAction()) {
			case Action.ACTION_INSERT: e.delete(pos, pos + text.length()); break;
			case Action.ACTION_REPLACE: e.replace(pos, pos + text2.length(), text); break;
			case Action.ACTION_DELETE: e.insert(pos, text); break;
			default: throw new IllegalStateException("unexcepted: unknown action " + getAction());
		}
	}

	public void redo(Editable e) {
		switch(getAction()) {
			case Action.ACTION_INSERT: e.insert(pos, text); break;
			case Action.ACTION_DELETE: e.delete(pos, pos + text.length()); break;
			case Action.ACTION_REPLACE: e.replace(pos, pos + text.length(), text2); break;
			default: throw new IllegalStateException("unexcepted: unknown action " + getAction());
		}
	}

	//public void dbg() { Log.d("Action", "action=" + action + ", text=" + (text == null? "null" : text) + ", text2=" + (text2 == null? "null" : text2) + ", pos=" + pos); }

	public static Action del(int pos, CharSequence before) {
		return new Action(pos, before, null);
	}

	public static Action rep(int pos, CharSequence before, CharSequence after) {
		return new Action(pos, before, after);
	}

	public static Action ins(int pos, CharSequence after) {
		return new Action(pos, after, null);
	}

	public byte getAction() {
		if(text == null) {
			if(text2 == null) return -1;
			else return ACTION_INSERT;
		} else {
			if(text2 == null) return ACTION_DELETE;
			else return ACTION_REPLACE;
		}
	}

	public static byte getAction(int before, int after) {
		if(before == 0) {
			if(after > 0) return ACTION_INSERT;
			else return -1;
		} else {
			if(after > 0) return ACTION_REPLACE;
			else return ACTION_DELETE;
		}
	}
}
