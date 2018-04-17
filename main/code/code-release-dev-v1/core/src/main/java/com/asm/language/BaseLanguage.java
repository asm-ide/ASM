package com.asm.language;

import com.asm.analysis.CodeAnalysis;
import com.asm.analysis.CodeSuggest;
import com.asm.language.Language;
import com.asm.language.LanguageInfo;
import com.asm.widget.CodeEdit;
import com.asm.widget.codeedit.Highlightable;
import com.asm.widget.codeedit.CodeEditInterface;

import java.util.HashMap;
import java.lang.reflect.Constructor;
import com.asm.annotation.Nullable;
import com.lhw.util.TypeUtils;


/**
 * Might this source code become to legacy code.
 */
public class BaseLanguage implements Language
{
	private LanguageInfo info = new LanguageInfo() {
		@Override
		public String languageName() {
			return langName;
		}
		
		@Override
		public String textQuotes() {
			return textQuote;
		}
		
		@Override
		public char textEscaper() {
			return textEscaper;
		}
		
		@Override
		public String[] comments() {
			return comment;
		}
		
		@Override
		public String textSeperators() {
			return textSeperator;
		}
		
		@Override
		public boolean getArg(String name, boolean defaultValue) {
			if(args.containsKey(name)) return (boolean) args.get(name);
			return defaultValue;
		}

		@Override
		public String getArg(String name, String defaultValue) {
			if(args.containsKey(name)) return (String) args.get(name);
			return defaultValue;
		}
	};
	
	//private static final String SPACES = " \t\n";
	
	private CodeEdit edit;
	
	private String langName = "base";
	private String textQuote = null;
	private char textEscaper = '\\';
	private String[] comment;
	private String textSeperator = ";.,{}()[]:+-/*?<>&|!=^~";
	private Class<CodeAnalysis> analysisClass;
	private CodeSuggest suggest;
	
	
	/**
	 * default arguments :
	 * name : String. language name
	 * textQuote : String. like "". all available quotes.
	 * textEscaper : char. in "", which can put textually like "he said, \"hi this-->\""
	 * comment : Pair<String>[]. all comments: a to b. a:"/*" b:"* /" or a:"//" b:"\n"
	 * textSeperator : String. It can seperate text; for instance: in "abc=123", if '=' is not seperator, "abc=123" would senced with a variable name.
	 * 
	 * ex) <code>{"name":"java","textEscaper":"\\",...,"sentence":["a","b","c",..], "math":[...], ..}</code>
	 */
	
	private HashMap<String, Object> args = new HashMap<String, Object>();
	
	private HashMap<String, Object> datas = new HashMap<String, Object>();
	
	
	public void setLanguageName(String langName) {
		this.langName = langName;
	}
	
	public String getLanguageName() {
		return langName;
	}
	
	
	
	public int getColor(String type) {
		return edit.getColor(type);
	}
	
	public int getColor(String type, String name) {
		return edit.getColor(type, name);
	}
	
	public CodeEdit getEdit() {
		return edit;
	}
	
	@Override
	public void initLanguage(HighlightArgs edit) {
		
	}
	
	@Override
	public void highlight(int start, int end) {
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after) {

	}

	@Override
	public void aftetTextChanged(CharSequence text, int start, int before, int count) {

	}

	@Override
	public LanguageInfo getInfo() {
		return info;
	}
	
	public void setAnalysisClass(Class<CodeAnalysis> clazz) {
		analysisClass = clazz;
	}
	
	public void setSuggest(CodeSuggest suggest) {
		this.suggest = suggest;
	}
	
	@Override
	public CodeAnalysis newAnalysis() {
		try {
			return analysisClass.newInstance();
		} catch(Exception e) {
			throw new IllegalStateException("wrong class state", e);
		}
	}
	
	@Override
	public CodeSuggest getSuggest() {
		return suggest;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getData(String name, T defaultValue) {
		if(datas.containsKey(name)) return (T) datas.get(name);
		return defaultValue;
	}
	
	public void setData(String name, Object value) {
		datas.put(name, value);
	}
	
	public void setArg(String name, @Nullable String type, String value) {
		if(type == null) {
			switch(name) {
				case "info.textQuotes": textQuote = value; break;
				case "info.textSeperator": textSeperator = value; break;
				case "info.textEscaper": textEscaper = value.charAt(0); break;
				case "info.comments": comment = (String[]) TypeUtils.parseList(value); break;
				default: throw new IllegalArgumentException("unknown name : " + name);
			}
		} else {
			args.put(name, TypeUtils.getObjectFromString(value, type));
		}
	}
}
