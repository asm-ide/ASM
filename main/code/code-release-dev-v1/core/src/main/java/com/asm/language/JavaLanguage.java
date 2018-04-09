package com.asm.language;

import com.asm.language.Language;
import com.asm.language.LanguageInfo;
import com.asm.widget.CodeEdit;
import com.asm.widget.codeedit.Highlightable;
import com.asm.widget.codeedit.CodeEditInterface;
import com.lhw.util.TextUtils;
import com.lhw.util.Pair;


public class JavaLanguage implements Language
{
	public static class JavaInfo implements LanguageInfo
	{
		@Override
		public String languageName() {
			return "java";
		}
		
		@Override
		public String textQuotes() {
			return "\"\'";
		}
		
		@Override
		public char textEscaper() {
			return '\\';
		}
		
		@Override
		public Pair<String>[] comments() {
			return new Pair[] {
				//new Pair<String>("//", "\n"),
				//new Pair<String>("/*", "*/"),
			};
		}
		
		@Override
		public String textSeperators() {
			return ";.,{}()[]:+-/*?<>&|!=^~";
		}
		
		@Override
		public boolean getArg(String name, boolean defaultValue) {
			return defaultValue;
		}
		
		@Override
		public String getArg(String name, String defaultValue) {
			return defaultValue;
		}
	}
	
	
	public class JavaSearcher {
		public CharSequence text;
		public int startIndex, index = 0;
		public boolean isUnderSlashLast;
		public boolean endedType = true;
		public int type; //type 0normal 1string 2char 3oneline-note 4lines-note //5operator
		
		
		public int next() {
			if(index >= text.length() - 1) {
				type = -1;
			}
			int lastIndex = index;
			switch(type) {
				case -1: {
					return -1;
				}
				case 0: {
					switch(text.charAt(index)) {
						case '/':
							if(index < text.length() - 1) {
								switch(text.charAt(index + 1)) {
									case '/':
										type = 3;
										index += 2;
										break;
										
									case '*':
										type = 4;
										index += 2;
										break;
								}
							} else {
								type = -1;
							}
							break;
							
						case '\"':
							type = 1;
							isUnderSlashLast = false;
							index += 1;
							break;
							
						case '\'':
							type = 2;
							index += 1;
							break;
							
						default:
							int len = text.length();
							if(TextUtils.includes(text.charAt(index), highlightCutRange)) {
								startIndex = ++index;
								break;
							}
							while(TextUtils.includes(text.charAt(index + 1), highlightCutRange)) {
								if(index == len - 1) {
									type = -1;
									return -1;
								}
								index++;
							}
							startIndex = index;
							break;
					}
					break;
				} case 1: {
					char c = text.charAt(index);
					index++;
					if(c == '\\') {
						isUnderSlashLast = !isUnderSlashLast;
					} else if(!isUnderSlashLast && c == '\"' || c == '\n') { // NOTE : if catch code error, use this
						type = 0;
						startIndex = index;
					}
					break;
				} case 2: {
					char c = text.charAt(index);
					if(c == '\\') index += 2;
					else index += 1;
					//if(text.charAt(index) != '\'') ; // NOTE : catch code error
					index++;
					type = 0;
					startIndex = index;
					break;
				} case 3: {
					char c = text.charAt(index);
					if(c == '\n') {
						type = 0;
						startIndex = index;
					}
					break;
				} case 4: {
					if(index == text.length() - 1 || (text.charAt(index) == '*' && text.charAt(index + 1) == '/')) {
						index++;
						type = 0;
						startIndex = index;
					} else {
						index++;
					}
					break;
				}
			}
			return index - lastIndex;
		}
	}
	
	
	
	private static final String[] sentence = {"abstract", "assert", "break", "case", "catch", "class", "continue", "default", "do", "else", "extends", "final", "finally", "for", "if", "import", "implements", "instanceof", 
		"interface", "native", "new", "package", "public", "protected", "private", "return", "static", "super", "switch", "this", "throw", "throws", "try", "volatile", "while",
		"strictfp", // old
	};
	private static final String[] type = {"boolean", "byte", "char", "double", "float", "int", "long", "short", "void", };
	private static final String symbol = ";.,{}()[]:";
	private static final String math = "+-/*?<>&|!=";
	private static final String number = "0123456789";
	private static final String[] othervalue = {"true", "false", "null", };
	private static final String highlightCutRange = " ;.,{}()[]:+-/*?<>&|!=";
	
	private CodeEdit edit;
	private CharSequence delText, insText;
	private Highlightable h;
	
	
	@Override
	public void initLanguage(CodeEditInterface edit) {
		if(!(edit instanceof CodeEdit)) throw new IllegalArgumentException("edit instanceof CodeEdit returns false");
		this.edit = (CodeEdit) edit;
	}

	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after) {
		delText = text.subSequence(start, count);
	}

	@Override
	public void aftetTextChanged(CharSequence text, int start, int before, int count) {
		insText = text.subSequence(start, count);
	}

	@Override
	public void highlight(Highlightable data, int start, int end) {
		int s = start, e = end, len = edit.length();
		while(s >= 0 && !TextUtils.includes(edit.charAt(s), highlightCutRange)) s--;
		while(e < len && !TextUtils.includes(edit.charAt(e), highlightCutRange)) e++;
		if(delText.length() != 0 && h != null) {
			JavaSearcher js = new JavaSearcher();
			js.text = delText;
			//js.type = // DOING
			int delta;
			while((delta = js.next()) != -1) {
				switch(js.type) {
					case 1:
						
				}
			}
		}
	}

	@Override
	public LanguageInfo getInfo() { // TODO
		return null;
	}
}
