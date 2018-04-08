package com.asm.yoon2.widget.codeedit;

import java.util.*;


public class Words
{
	private String[] words;
	private String colorName;
	private ArrayList<Rule> rules;


	public Words()
	{}

	public Words(String colorName, String[] words)
	{
		this.colorName = colorName;
		this.words = words;
	}
	
	public void setColor(String colorName) { this.colorName = colorName; }
	public String getColor() { return colorName; }
	
	public void setWords(String[] words) { this.words = words; }
	public void addWord(String word)
	{
		String[] dst = new String[words.length + 1];
		for(int i = 0; i < words.length; i++) dst[i] = words[i];
		this.words = dst;
	}
	
	public int addRule(Rule rule) { rules.add(rule); return rules.size() - 1; }
	public void removeRule(int index) { rules.remove(index); }
	public void removeRule(Rule matches) { rules.remove(matches); }
}
