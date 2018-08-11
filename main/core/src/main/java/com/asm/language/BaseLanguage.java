package com.asm.language;

import com.asm.analysis.CodeAnalysis;
import com.asm.analysis.CodeSuggest;
import com.asm.language.Language;
import com.asm.language.LanguageInfo;
import com.asm.analysis.CodeFinder;
import com.lhw.util.TypeUtils;

import com.asm.widget.codeedit.CodeEditInterface;

import java.util.HashMap;


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
		public CodeFinder finder(int type) {
			return null;
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
	
	private CodeEditInterface edit;
	
	private String langName = "base";
	private HashMap<Integer, CodeFinder> finders = new HashMap<>();
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
	
	private HashMap<String, Object> args = new HashMap<>();
	
	private HashMap<String, Object> datas = new HashMap<>();
	
	
	public void setLanguageName(String langName) {
		this.langName = langName;
	}
	
	public String getLanguageName() {
		return langName;
	}
	
	
	
	public int getColor(String type) {
		return edit.getStyle().getColor(type);
	}
	
	public int getColor(String type, String name) {
		return edit.getStyle().getColor(type, name);
	}
	
	public CodeEditInterface getEdit() {
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
	
	public void setArg(String name, String type, String value) {
		args.put(name, TypeUtils.getObjectFromString(value, type));
	}
	
	public void setArgByValue(String name, Object value) {
		args.put(name, value);
	}
}
