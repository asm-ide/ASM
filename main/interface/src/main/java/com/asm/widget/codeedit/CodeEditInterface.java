package com.asm.widget.codeedit;

import android.text.Editable;
import com.asm.language.Language;
import com.asm.language.LanguageInfo;


/**
 * The codeedit interface for use.
 */
public interface CodeEditInterface extends TextViewInterface
{
	public LanguageInfo getLanguageInfo();
	public CodeStyleInterface getStyle();
	public Language getLanguage();
	// TODO
	//public Highlightable getHighlightCache();
}
