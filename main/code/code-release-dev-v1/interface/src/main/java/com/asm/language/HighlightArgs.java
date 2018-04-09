package com.asm.language;

import com.asm.widget.codeedit.CodeEditInterface;
import com.asm.widget.codeedit.CodeStyleInterface;
import android.text.Editable;


public class HighlightArgs
{
	private CodeStyleInterface mStyle;
	private Editable mText;
	private LanguageInfo mInfo;
	private Language mLang;
	
	
	public HighlightArgs() {}
	
	public HighlightArgs(CodeEditInterface edit) {
		mStyle = edit.getStyle();
		mText = edit.getText();
		mInfo = edit.getLanguageInfo();
		mLang = edit.getLanguage();
	}
	
	public CodeStyleInterface getStyle() {
		return mStyle;
	}
	
	public Editable getText() {
		return mText;
	}
	
	public LanguageInfo getInfo() {
		return mInfo;
	}
	
	public Language getLanguage() {
		return mLang;
	}
}
