package com.asm.language.xml;

import com.asm.language.Language;
import com.asm.language.LanguageInfo;
import com.asm.language.HighlightArgs;
import com.asm.analysis.CodeAnalysis;
import com.asm.analysis.CodeSuggest;


public class LoadedLanguage implements Language
{
	@Override
	public void initLanguage(HighlightArgs edit)
	{
		// TODO: Implement this method
	}

	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after)
	{
		// TODO: Implement this method
	}

	@Override
	public void aftetTextChanged(CharSequence text, int start, int before, int count)
	{
		// TODO: Implement this method
	}

	@Override
	public void highlight(int start, int end)
	{
		// TODO: Implement this method
	}

	@Override
	public LanguageInfo getInfo()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public CodeAnalysis newAnalysis()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public CodeSuggest getSuggest()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public <T> T getData(String name, T defaultValue)
	{
		// TODO: Implement this method
		return null;
	}

	
	
}
