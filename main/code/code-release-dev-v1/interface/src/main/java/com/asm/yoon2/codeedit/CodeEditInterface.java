package com.asm.yoon2.codeedit;

import android.text.Editable;
import com.asm.widget.codeedit.LanguageInfo;


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
