package com.asm.widget.codeedit;

import com.asm.annotation.API;
import com.asm.language.Language;
import com.asm.language.LanguageInfo;


/**
 * The codeedit interface for use.
 */
@API
public interface CodeEditInterface extends TextViewInterface
{
	public LanguageInfo getLanguageInfo();
	public CodeStyleInterface getStyle();
	public Language getLanguage();
	// TODO
	//public Highlightable getHighlightCache();
}
