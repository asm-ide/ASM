package com.asm.widget.codeedit;

import android.text.Editable;
import com.asm.language.LanguageInfo;


/**
 * The codeedit interface for use.
 */
public interface CodeEditInterface
{
	public Editable getText();
	public LanguageInfo getLanguageInfo();
	// TODO
	//public Highlightable getHighlightCache();
}
