package com.asm.yoon2.widget.codeedit.languages;

import com.asm.yoon2.widget.*;
import com.asm.yoon2.widget.codeedit.*;
import java.util.*;


public abstract class BaseLanguage implements Language
{
	private boolean inited = false;
	private CodeEdit edit;
	private HashMap<String, Words> wordsList;
	
	
	public BaseLanguage()
	{
		wordsList = new HashMap<String, Words>();
	}
	
	public CodeEdit getEdit() { return edit; }
	
	@Override
	public void initLanguage(CodeEdit edit)
	{
		if(inited) throw new IllegalStateException("initLanguage already called");
		this.edit = edit;
		inited = true;
	}
	
	@Override
	public void highlight(int start, int end)
	{
		
	}
	
	public void addWords(String name, Words words) { wordsList.put(name, words); }
	public int getColor(String name) { return edit.getColor(name); }
	public int getColor(String lang, String name) { return edit.getColor(lang, name); }
	
	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after)
	{
		
	}
	
	@Override
	public void aftetTextChanged(CharSequence text, int start, int before, int count)
	{
		
	}
	
	@Override
	public String getName() { return "base"; }
}
