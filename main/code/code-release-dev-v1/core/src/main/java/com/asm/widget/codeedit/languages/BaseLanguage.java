package com.asm.widget.codeedit.languages;

import com.asm.widget.CodeEdit;
import com.asm.widget.codeedit.Language;
import com.asm.widget.codeedit.LanguageInfo;
import com.asm.widget.codeedit.Highlightable;
import com.asm.yoon2.codeedit.CodeEditInterface;
import com.asm.widget.codeedit.Pair;

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
		public String textQuotes() {
			return textQuote;
		}
		
		@Override
		public char textEscaper() {
			return textEscaper;
		}
		
		@Override
		public Pair<String>[] comments() {
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
		public String getArg(String name, String defaultValue)
		{
			if(args.containsKey(name)) return (String) args.get(name);
			return defaultValue;
		}
	};
	
	private static final String SPACES = " \t\n";
	
	
	//private boolean inited = false;
	
	private CodeEdit edit;
	
	private String langName = "base";
	private String textQuote = null;
	private char textEscaper = '\\';
	private Pair<String>[] comment;
	private String textSeperator = ";.,{}()[]:+-/*?<>&|!=^~";
	
	
	/**
	 * default arguments :
	 * name : String. language name
	 * textQuote : char[]. like "". all available quotes.
	 * textEscaper : char. in "", which can put textually like "he said, \"hi this-->\""
	 * comment : Pair<String>[]. all comments: a to b. a:"/*" b:"* /" or a:"//" b:"\n"
	 * textSeperator : String. It can seperate text; for instance: in "abc=123", if '=' is not seperator, "abc=123" would senced with a variable name.
	 * 
	 * ex) <code>{"name":"java","textEscaper":"\\",...,"sentence":["a","b","c",..], "math":[...], ..}</code>
	 */
	
	private HashMap<String, Object> args = new HashMap<String, Object>();
	
	
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
	public void initLanguage(CodeEditInterface edit) {
		if(!(edit instanceof CodeEdit)) throw new IllegalArgumentException("edit instanceof CodeEdit returns false");
		this.edit = (CodeEdit) edit;
	}
	
	@Override
	public void highlight(Highlightable data, int start, int end) {
		
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
}
