package com.asm.language;

import com.asm.analysis.CodeAnalysis;
import com.asm.analysis.CodeSuggest;
import com.asm.widget.codeedit.Highlightable;
import com.asm.annotation.Nullable;


/**
 * general interface for apply in CodeEdit.
 * You can make language easier to extends BaseLanguage.
 * @see com.asm.widget.codeedit.language.BaseLanuguage
 */
public interface Language
{
	/**
	 * Called when language initialize.
	 * Might called several times, when <code>CodeEdit.setLanguage()</code> several called.
	 */
	public void initLanguage(HighlightArgs edit);
	
	/**
	 * Edit event that called before text changed.
	 */
	public void beforeTextChanged(CharSequence text, int start, int count, int after);
	/**
	 * Edit event that called after text changed.
	 */
	public void aftetTextChanged(CharSequence text, int start, int before, int count);
	
	/**
	 * Highlight the words from start to end.
	 * The call priority is beforeTextChanged -> afterTextChanged -> highlight. (when text changed)
	 * text changed event is run on UI thread, but highlight might be called
	 */
	public void highlight(int start, int end);
	
	/**
	 * Return the language info.
	 */
	public LanguageInfo getInfo();
	
	/**
	 * Return the new CodeAnalysis object.
	 * Can be null.
	 */
	public @Nullable CodeAnalysis newAnalysis();
	
	/**
	 * Return the CodeSuggest object.
	 * Can be null.
	 */
	public @Nullable CodeSuggest getSuggest();
	
	/**
	 * Return the some parameters and arguments.
	 * May be many values, like keywords
	 */
	public <T> T getData(String name, T defaultValue);
}
