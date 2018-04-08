package com.asm.analysis.java;

import com.asm.analysis.CodeSuggest;
import com.asm.analysis.CodeSuggest.SuggestListItem;
import android.text.Editable;


public class JavaSuggest extends CodeSuggest
{
	@Override
	public void autoInsert(Editable text, int index) {
		
	}
	
	@Override
	public SuggestListItem[] suggestList(Editable text, int index, int len) {
		
		return null;
	}
	
	
}
