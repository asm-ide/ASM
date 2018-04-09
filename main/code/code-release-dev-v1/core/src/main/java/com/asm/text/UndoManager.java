package com.asm.text;

import android.text.Editable;
import android.text.TextWatcher;

import java.io.Serializable;
import java.util.ArrayList;


public class UndoManager implements Serializable
{
	public static class Actions implements Serializable
	{
		private ArrayList<Action> mActions = new ArrayList<Action>();
		
		
		public Actions() {}
		
		public Actions(Action oneChild) {
			mActions.add(oneChild);
		}
		
		public Actions(ArrayList<Action> actions) {
			mActions = actions;
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
			for(int i = 0; i < mActions.size(); i++) {
				mActions.get(i).undo(e);
			}
		}
		
		public void redo(Editable e) {
			for(int i = mActions.size(); i > 0; i--) {
				mActions.get(i).redo(e);
			}
		}
	}
	
	public static class Action implements Serializable
	{
		public static final byte ACTION_INSERT = 0;
		public static final byte ACTION_REPLACE = 1;
		public static final byte ACTION_DELETE = 2;
		
		public byte action;
		public CharSequence text;
		public CharSequence text2;
		public int pos;
		
		
		public Action() {}
		
		public Action(byte action, CharSequence text, CharSequence text2, int pos) {
			this.action = action;
			this.text = text;
			if(action == ACTION_REPLACE) this.text2 = text2;
			this.pos = pos;
		}
		
		public boolean edit(CharSequence txt, CharSequence lastText, int start, int before, int count, boolean appendAction) {
			switch(action) {
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
			switch(action){
				case Action.ACTION_INSERT: e.delete(action + pos, pos + text.length()); break;
				case Action.ACTION_REPLACE: e.replace(pos, pos + text2.length(), text); break;
				case Action.ACTION_DELETE: e.insert(pos, text); break;
			}
		}
		
		public void redo(Editable e) {
			switch(action) {
				case Action.ACTION_INSERT: e.insert(pos, text); break;
				case Action.ACTION_DELETE: e.delete(pos, pos + text.length()); break;
				case Action.ACTION_REPLACE: e.replace(pos, pos + text.length(), text2); break;
			}
		}
		
		//public void dbg() { Log.d("Action", "action=" + action + ", text=" + (text == null? "null" : text) + ", text2=" + (text2 == null? "null" : text2) + ", pos=" + pos); }
		
		public static Action del(int pos, CharSequence before) {
			return new Action(ACTION_DELETE, before, null, pos);
		}
		
		public static Action rep(int pos, CharSequence before, CharSequence after) {
			return new Action(ACTION_REPLACE, before, after, pos);
		}
		
		public static Action ins(int pos, CharSequence after) {
			return new Action(ACTION_INSERT, after, null, pos);
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
		
		public static Action create(int action, CharSequence last, CharSequence newText, int start, int after) {
			Action act;
			switch(action) {
				case ACTION_INSERT: act = ins(start, newText.subSequence(start, start + after)); break;
				case ACTION_REPLACE: act = rep(start, last, newText.subSequence(start, start + after)); break;
				case ACTION_DELETE: act = del(start, last); break;
				default: throw new IllegalArgumentException("wrong action");
			}
			return act;
		}
	}
	

	public ArrayList<Actions> history = new ArrayList<Actions>();
	public int historyIndex = -1;
	public int maxHistorySize = 30;
	private Actions openedActions;
	transient private CharSequence lastText;
	
	transient private TextWatcher textWatcher;
	
	
	public UndoManager() {
		textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence txt, int start, int before, int count) {
				lastText = txt.subSequence(start, start + before);
			}

			@Override
			public void onTextChanged(CharSequence txt, int start, int before, int count) {
				//Log.d("Action", "edit " + lastText + " to " + txt.subSequence(start, start + count));
				byte action = Action.getAction(before, count);
				boolean appendAction = false;
				if(history.size() != 0) {
					Actions lastActs = history.get(history.size() - 1);
					Action lastAct = lastActs.getLast();
					if(lastAct == null) {

					} else if(lastAct.action == action) {
						appendAction = lastAct.edit(txt, lastText, start, before, count, appendAction);
					}
				}
				if(appendAction) {

				} else {
					//Log.d("Action", "add history");
					Action act = Action.create(action, lastText, txt, start, count);
					if(openedActions == null)
						history.add(new Actions(act));
					else openedActions.add(act);
					if(history.size() == historyIndex + 1) historyIndex++;
					else historyIndex = history.size() - 1;
				}
				afterAdded();
			}

			@Override
			public void afterTextChanged(Editable edit) {

			}
		};
	}
	
	private void afterAdded() {
		while(history.size() > maxHistorySize)
			history.remove(0);
	}
	
	public TextWatcher getAutoMarkWatcher() {
		return textWatcher;
	}
	
	public void addOpenAction(CharSequence text, int start, int before, int after) {
		history.get(history.size() - 1).add(Action.create(Action.getAction(before, after), lastText, text, start, after));
	}
	
	public boolean undo(Editable e) {
		close();
		if(historyIndex <= -1) return false;
		
		Actions cur = history.get(historyIndex);
		cur.undo(e);
		historyIndex--;
		
		return true;
	}
	
	public boolean redo(Editable e) {
		close();
		if(historyIndex >= history.size() - 1 || historyIndex == -1) return false;
		
		historyIndex++;
		Actions cur = history.get(historyIndex);
		cur.redo(e);
		
		return true;
	}
	
	public void close() {
		if(openedActions != null) {
			history.add(openedActions);
			openedActions = null;
		}
	}
	
	public void openActions() {
		close();
		openedActions = new Actions();
	}
}
