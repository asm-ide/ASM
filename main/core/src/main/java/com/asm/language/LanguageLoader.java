package com.asm.language;

import android.os.Build;

import com.asm.analysis.CodeAnalysis;
import com.asm.analysis.CodeSuggest;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;


public final class LanguageLoader
{
	@SuppressWarnings("unchecked")
	public static Language fromXml(InputStream is) {
		BaseLanguage lang = new BaseLanguage();
		
		XmlPullParserFactory factory;
		
		try {
			factory = XmlPullParserFactory.newInstance();
		} catch(XmlPullParserException e) {
			throw new RuntimeException("couldn't initialize the factory", e);
		}
		
		
		class ParseException extends Exception
		{
			public ParseException(String message) {
				super(message);
			}
		}
		
		try {
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(is, null);
			
			int event = parser.getEventType();
			
			event = parser.next(); // <?xml version="1.0" encoding="utf-8"?>
			if(parser.getName().equalsIgnoreCase("xml"))
				event = parser.next(); // <language>
			if(!parser.getName().equalsIgnoreCase("language"))
				throw new ParseException("tag not starts by <language>");
			
			// attribute : analysisClass, suggestClass
			{
				String value = parser.getAttributeValue(null, "analysisClass");
				
				try {
					if(value != null)
						lang.setAnalysisClass((Class<CodeAnalysis>) Class.forName(value));
				} catch(ClassNotFoundException e) {
					throw new RuntimeException("class " + value + " in analysisClass not exist");
				}
				
				value = parser.getAttributeValue(null, "suggestClass");
				
				try {
					if(value != null) {
						CodeSuggest suggest = (CodeSuggest) Class.forName(value).newInstance();
						lang.setSuggest(suggest);
					}
				} catch(ClassNotFoundException e) {
					throw new RuntimeException("class " + value + " in suggestClass not exist");
				} catch(Exception e) {
					throw new RuntimeException("couldn't instantiate class " + value);
				}
			}
			
			while(event != END_DOCUMENT) {
				switch(event) {
					case START_DOCUMENT:
						// none, never called
						break;
						
					case START_TAG:
						switch(parser.getName()) {
							
						}
						
						break;
						
					case TEXT:
						
						break;
						
					case END_TAG:
						
						break;
						
					case END_DOCUMENT:
						
						break;
						
				}
				
				event = parser.next();
			}
		} catch(XmlPullParserException | IOException e) {
			throw new RuntimeException("failed to parse xml", e);
		} catch(ParseException e) {
			throw new RuntimeException("failed to parse xml" + e.getMessage());
		}
		
		return lang;
	}
}
/* example:

<?xml version="1.0" encoding="utf-8"?>
<language name="java"
	analysisClass="com.asm.analysis.java.JavaAnalysis"
	suggestClass="com.asm.analysis.java.JavaSuggest">
	<data name="keywords" type="strings">
	"abstract", "break", "case", "catch", "class", "continue", "default", "do", "else", "extends", "final", "finally", "for", "if", "import", "implements", "instanceof", 
	"interface", "native", "new", "package", "public", "protected", "private", "return", "static", "super", "switch", "this", "throw", "throws", "try", "volatile", "while"
	</data>
	<data name="otherDatas" type="strings">
	"true", "false", "null"
	</data>
	<data name="types" type="strings">
	"boolean", "byte", "char", "double", "float", "int", "long", "short"
	</data>
	<data name="operators" type="string">;.,{}()[]:</data>
	<data name="maths" type="string">"+-/*?&lt;&gt;&amp;|!="</data>
	<data name="textSeperators" type="string"> ;.,{}()[]:+-/*?&lt;&gt;&amp;|!=</data>
	<data name="textQuotes" type="string">"'</data>
	<data name="textEscaper" type="char">\</data>
	<data name="comments" type="strings">
	"/*", "*\/",
	"//", "\n"
	</data>
	<arg name="info.textQuotes" value="@data/textQuotes" />
	<arg name="info.textSeperators" value="@data/textSeperators" />
	<arg name="info.textEscaper" value="@data/textEscaper" />
	<arg name="info.comments" value="@data/comments" />
	<arg name="info.isNewlineBreakText" type="boolean" value="true" />
	</language>
*/
