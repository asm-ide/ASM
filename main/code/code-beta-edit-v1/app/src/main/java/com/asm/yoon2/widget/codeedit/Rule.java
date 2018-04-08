package com.asm.yoon2.widget.codeedit;

import com.asm.yoon2.widget.*;
import android.text.*;
import java.util.*;


public class Rule
{
	private String[] beforeWords;
	private String[] afterWords;
	private String[] notBeforeWords;
	private String[] notAfterWords;
	private String[] equals;
	
	public Rule()
	{}

	public void setNotBeforeWords(String[] notBeforeWords)
	{
		this.notBeforeWords = notBeforeWords;
	}

	public String[] getNotBeforeWords()
	{
		return notBeforeWords;
	}

	public void setNotAfterWords(String[] notAfterWords)
	{
		this.notAfterWords = notAfterWords;
	}

	public String[] getNotAfterWords()
	{
		return notAfterWords;
	}

	public void setEquals(String[] equals)
	{
		this.equals = equals;
	}

	public String[] getEquals()
	{
		return equals;
	}
	
	public void setBeforeWords(String[] beforeWords)
	{
		this.beforeWords = beforeWords;
	}
	
	public String[] getBeforeWords()
	{
		return beforeWords;
	}
	
	public void setAfterWords(String[] afterWords)
	{
		this.afterWords = afterWords;
	}
	
	public String[] getAfterWords()
	{
		return afterWords;
	}
	
	public boolean checkRule(CharSequence text, int start, int length)
	{
		boolean okay = true;
		if(beforeWords != null) {
			if(endsWith(beforeWords, start - 1, text));
		}
		return okay;
	}
	
	private static boolean startsWith(CharSequence[] texts, int start, CharSequence target)
	{
		for(CharSequence text : texts)
			if(text.subSequence(start, start + target.length() - 1) == target) return true;
		
		return false;
	}
	
	private static boolean endsWith(CharSequence[] texts, int end, CharSequence target)
	{
		for(CharSequence text : texts)
			if(text.subSequence(end - target.length() + 1, end) == target) return true;

		return false;
	}
}
