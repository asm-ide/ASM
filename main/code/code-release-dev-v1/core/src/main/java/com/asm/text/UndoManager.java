package com.asm.text;


import com.lhw.util.TextUtils;

import android.text.Editable;
import android.text.TextWatcher;

import java.io.Serializable;
import java.util.ArrayList;

// temp
import android.util.*;


public class UndoManager implements Serializable, TextWatcher
{
	/**
	 * Undo mode and marks that do not use undo.
	 * If this mode was setted, all undo history will be destoryed and
	 * set to null.
	 */
	public static final int UNDO_MODE_OFF = 0;
	
	/**
	 * Undo mode and marks that undo was disenabled for a while.
	 * 
	 * UNDO_MODE_OFF does mean no not use undo manager forever and
	 * destory all undo history but this does mean not mark on undo
	 * stack while this mode was set.
	 * 
	 * This can be used during edit text without history.
	 */
	public static final int UNDO_MODE_DISABLED = 1;
	
	/**
	 * Default undo mode.
	 * 
	 * This mode automaticly sence that insert, replace,
	 * or delete texts and add to history.
	 */
	public static final int UNDO_MODE_AUTOMARK = 2;
	
	/** 
	 * Undo mode and marks that all actions will be marked in one {@link Actions}.
	 * 
	 * This is the mode that put all actions while this mode was set
	 * in one action. So, if undo() or redo() called, all actions
	 * with this mode will be edited.
	 */
	public static final int UNDO_MODE_OPENED = 0x00000001 << 31;
	
	
	private static final String UNDO_CUT = " \n\t,.";
	
	
	/** history lists */
	private ArrayList<Actions> mHistory = new ArrayList<Actions>();
	
	/** history index */
	private int mIndex = -2;
	
	/**
	 * Max history count.
	 * If {@code mHistory.size() > mMaxCount}, the first history will be erased.
	 * {@code -1} to unlimited size.
	 */
	private int mMaxCount = -1;
	
	/**
	 * undo mode.
	 * Can select UNDO_MODE_...
	 */
	private int mUndoMode = UNDO_MODE_OPENED;//UNDO_MODE_AUTOMARK;
	
	/**
	 * last undo mode.
	 * For restore last undo mode.
	 */
	private int mLastUndoMode = UNDO_MODE_AUTOMARK;
	
	/** last texts start from {@code start} to {@code count} when called beforeTextChanged(). */
	private CharSequence mLastText;
	
	/** opened actions. exist if mode is not closed */
	private Actions mOpenedActions;
	
	
	public UndoManager() {
		
	}
	
	
	public void addOpenAction(int start, CharSequence last, CharSequence text) {
		addOpenAction(new Action(start, last, text));
	}
	
	public void addOpenAction(Action act) {
		if(mOpenedActions == null) mOpenedActions = new Actions();
		mOpenedActions.add(act);
		Log.d("addOpenAction()", act.pos + ", " + act.text + "->" + act.text2);
		TextData e = TextData.valueOf("AAAAAAAAAAAAAAAAAAAAAAA");
		mOpenedActions.undo(e);
		Log.d("addOpenAction()", e.toString());
	}
	
	public void storeOpenedActions() {
		if(mOpenedActions != null && mOpenedActions.size() > 0) {
			mHistory.add(mOpenedActions);
			mIndex++;
		}
	}
	
	public void storeOpenedActionsAndNew() {
		if(mOpenedActions != null && mOpenedActions.size() > 0) {
			mHistory.add(mOpenedActions);
			mIndex++;
		}
		mOpenedActions = new Actions();
	}
	
	/**
	 * If status are opened, close it and open the actions.
	 */
	private void tmpOpenActions() {
		
	}
	
	public void closeActions() {
		if(mOpenedActions != null) {
			mHistory.add(mOpenedActions);
			mIndex++;
			mOpenedActions = null;
		}
	}
	
	public int getUndoMode() {
		return mUndoMode;
	}
	
	/**
	 * Set the undo / redo stack mode.
	 * If mode setted to UNDO_MODE_OFF, all history will be destoryed.
	 */
	public int setMode(int mode) {
		if(mLastUndoMode == mode) return mLastUndoMode;
		mLastUndoMode = mUndoMode;
		
		switch(mode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISABLED:
				switch(mLastUndoMode) {
					case UNDO_MODE_OPENED:
						closeActions();
						break;
				}
				break;

			case UNDO_MODE_OPENED:
				if(mLastUndoMode != UNDO_MODE_OPENED) {
					openActions();
				}
				break;

			case UNDO_MODE_AUTOMARK:
				break;

			default:
				throw new IllegalArgumentException("unknown mode: " + Integer.toHexString(mode));
		}
		mUndoMode = mode;
		if(mode == UNDO_MODE_OFF) {
			mHistory.clear();
			mOpenedActions = null;
			mIndex = -1;
		}
		return mLastUndoMode;
	}
	
	/**
	 * Open the undo actions.
	 * Used when undo mode is {@link UNDO_MODE_OPENED} and if there is any
	 * opened {@link Actions}, close it and open new {@link Actions}.
	 */
	public void openActions() {
//		if(mLastUndoMode == UNDO_MODE_OPENED)
//			return; // quoted for new actions
		mLastUndoMode = mUndoMode;
		mUndoMode = UNDO_MODE_OPENED;

		closeActions();
		tmpOpenActions();
	}
	
	/**
	 * Open the undo actions if current undo mode marks actions.
	 * @see openUndoActions()
	 */
	public void openUndoActionsTemporary() {
		switch(mUndoMode) {
			case UNDO_MODE_AUTOMARK:
			case UNDO_MODE_OPENED:
				openActions();
				break;
		}
	}
	
	/**
	 * Close the actions if when called openUndoActionsTemporary()
	 * opened the actions and restore to last mode.
	 */
	public void closeUndoActionsTemporary() {
		switch(mLastUndoMode) {
			case UNDO_MODE_AUTOMARK:
			case UNDO_MODE_OPENED:
				closeActions();
				mUndoMode = mLastUndoMode;
				break;
		}
	}
	
	/**
	 * Used when tempory disable undo mode.
	 */
	public int disableUndo() {
		return setMode(UNDO_MODE_DISABLED);
	}
	
	/**
	 * Used when current mode is UNDO_MODE_DISABLED or called disableUndo();
	 * this re-enables undo mode.
	 * 
	 * For instance, when mode is a and called disableUndo() and later called
	 * this method, actions between calling disableUndo() and calling this will
	 * be not marked on undo history.
	 *
	 * @throws IllegalStateException if undo mode is not disabled
	 */
	public void enableUndo() {
		if(mUndoMode == UNDO_MODE_DISABLED) {
			if(mLastUndoMode != UNDO_MODE_DISABLED) {
				setMode(mLastUndoMode);
			}
		} else throw new IllegalStateException("this only can used when mode is UNDO_MODE_DISABLED");
	}
	
	
	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after) {
		switch(mUndoMode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISABLED:
				break;

			case UNDO_MODE_AUTOMARK:
			case UNDO_MODE_OPENED:
				// saves last text
				mLastText = text.subSequence(start, start + count);
				break;
		}
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start, int before, int count) {
		switch(mUndoMode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISABLED:
				break;

			case UNDO_MODE_AUTOMARK:
				Log.d("onTextChanged", text + "@" + start + " " + before + "/" + count);
				
				// marks in automark mode
				CharSequence cur = text.subSequence(start, start + count);
				byte action = Action.getAction(before, count);
				
				if(mIndex == -2) { // same as mIndex = -1
					mIndex = -1;
					storeOpenedActionsAndNew();
					addOpenAction(start, mLastText, cur);
				} else {
					boolean isAppend = false; // whether append to last action
					Actions lastActions;
					if(mOpenedActions != null) lastActions = mOpenedActions;
					else lastActions = mHistory.get(mIndex);
					Action act = new Action(start, mLastText, cur);
					
					// determines : append or not
					if(lastActions.isAppendable()) {
						if(lastActions.getLast().getAction() == action) {
							if(cur.length() > 2 || lastActions.size() != 0) {
								// paste text : cut OR last actions is 
							} else {
								// when type text, len <= 2
								
								switch(action) {
									case Action.ACTION_INSERT:
									case Action.ACTION_DELETE: {
										if(TextUtils.includesIn(cur, UNDO_CUT) == -1) {
											// not includes in UNDO_CUT
											isAppend = true;
										} else {
											if(cur.length() == 1 && TextUtils.allEquals(cur.charAt(0), cur)) {
												// length = 1 AND all texts in current inserted are as same as last(the one char)
												isAppend = true;
											} // else ;
										}
										break;
									}
									case Action.ACTION_REPLACE: {
										// Do nothing
										// isAppend = false;
										break;
									}
								}
							}
						} else {
							
						}
					}
					
					// append or new Actions
					if(!isAppend) {
						storeOpenedActionsAndNew();
					}
					addOpenAction(act);
				}
				break;
			case UNDO_MODE_OPENED:
				// marks all things in Actions
				addOpenAction(start, mLastText, text.subSequence(start, start + count));
				
				break;
		}
		mLastText = null;
		Log.d("UM", "changed index " + mIndex);
	}
	
	@Override
	public void afterTextChanged(Editable e) {
		// do nothing
	}
	
	
	protected void afterAdded() {
		if(mMaxCount != -1)
			while(mHistory.size() > mMaxCount)
				mHistory.remove(0);
	}
	
	public boolean undo(Editable e) {
		closeActions();
		if(mIndex <= -1) return false;
		
		Actions cur = mHistory.get(mIndex);
		cur.undo(e);
		mIndex--; // TODO : after undo made new action: how to save last actions?
		
		Log.d("UM", "index " + mIndex);
		
		return true;
	}
	
	public boolean redo(Editable e) {
		closeActions();
		if(mIndex >= mHistory.size() - 1 || mIndex == -1) return false;
		
		Actions cur = mHistory.get(mIndex);
		cur.redo(e);
		mIndex++;
		
		return true;
	}
	
	

//	public ArrayList<Actions> history = new ArrayList<Actions>();
//	public int historyIndex = -1;
//	public int maxHistorySize = 30;
//	private Actions openedActions;
//	transient private CharSequence lastText;
//	
//	transient private TextWatcher textWatcher;
//	
//	
//	public UndoManager() {
//		textWatcher = new TextWatcher() {
//			@Override
//			public void beforeTextChanged(CharSequence txt, int start, int before, int count) {
//				lastText = txt.subSequence(start, start + before);
//			}
//
//			@Override
//			public void onTextChanged(CharSequence txt, int start, int before, int count) {
//				//Log.d("Action", "edit " + lastText + " to " + txt.subSequence(start, start + count));
//				byte action = Action.getAction(before, count);
//				boolean appendAction = false;
//				if(history.size() != 0) {
//					Actions lastActs = history.get(history.size() - 1);
//					Action lastAct = lastActs.getLast();
//					if(lastAct == null) {
//
//					} else if(lastAct.action == action) {
//						appendAction = lastAct.edit(txt, lastText, start, before, count, appendAction);
//					}
//				}
//				if(appendAction) {
//
//				} else {
//					//Log.d("Action", "add history");
//					Action act = Action.create(action, lastText, txt, start, count);
//					if(openedActions == null)
//						history.add(new Actions(act));
//					else openedActions.add(act);
//					if(history.size() == historyIndex + 1) historyIndex++;
//					else historyIndex = history.size() - 1;
//				}
//				afterAdded();
//			}
//
//			@Override
//			public void afterTextChanged(Editable edit) {
//
//			}
//		};
//	}
//	
//	private void afterAdded() {
//		while(history.size() > maxHistorySize)
//			history.remove(0);
//	}
//	
//	public TextWatcher getAutoMarkWatcher() {
//		return textWatcher;
//	}
//	
//	public void addOpenAction(CharSequence text, int start, int before, int after) {
//		history.get(history.size() - 1).add(Action.create(Action.getAction(before, after), lastText, text, start, after));
//	}
//	
//	public boolean undo(Editable e) {
//		close();
//		if(historyIndex <= -1) return false;
//		
//		Actions cur = history.get(historyIndex);
//		cur.undo(e);
//		historyIndex--;
//		
//		return true;
//	}
//	
//	public boolean redo(Editable e) {
//		close();
//		if(historyIndex >= history.size() - 1 || historyIndex == -1) return false;
//		
//		historyIndex++;
//		Actions cur = history.get(historyIndex);
//		cur.redo(e);
//		
//		return true;
//	}
//	
//	public void close() {
//		if(openedActions != null) {
//			history.add(openedActions);
//			openedActions = null;
//		}
//	}
//	
//	public void openActions() {
//		close();
//		openedActions = new Actions();
//	}
}
