package com.asm.yoon2.widget.codeedit;

import com.asm.yoon2.widget.*;


/**
 * general interface for apply in CodeEdit.
 */
public interface Language
{
	public void initLanguage(CodeEdit edit);
	public void beforeTextChanged(CharSequence text, int start, int count, int after);
	public void aftetTextChanged(CharSequence text, int start, int before, int count);
	public void highlight(int start, int end);
	public String getName();
}
