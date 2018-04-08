package com.asm.analysis;

import android.graphics.Bitmap;
import android.text.Editable;
import com.asm.annotation.Nullable;


public abstract class CodeSuggest
{
	public static class SuggestListItem
	{
		public static interface OnItemSelectedListener
		{
			public void onSelected(SuggestListItem item, Editable edit, int index);
		}
		
		
		public CharSequence title;
		public @Nullable CharSequence subTitle;
		public @Nullable Bitmap image;
		public OnItemSelectedListener listener;
	}
	
	
	/**
	 * You can override this method to auto insert texts like:
	 * when you insert {, } will be automaticly inserted.
	 * All actions on this method are marked in one {@code Actions}.
	 */
	public void autoInsert(Editable text, int index) {
	}
	
	/**
	 * You can override this method to suggest lists which can
	 * be typed when select something.
	 */
	public SuggestListItem[] suggestList(Editable text, int index, int len) {
		return null;
	}
}
