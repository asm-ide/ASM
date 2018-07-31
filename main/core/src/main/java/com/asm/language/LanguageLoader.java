package com.asm.language;

import com.asm.analysis.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import com.lhw.util.TypeUtils;
import android.util.*;


public final class LanguageLoader
{
	@SuppressWarnings("raw-types")
	public static Language fromXml(InputStream is) {
		BaseLanguage lang = new BaseLanguage();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);
			
			Node mainNode = document.getFirstChild();
			if(!mainNode.getNodeName().equals("language"))
				throw new IllegalStateException("root node is not language, node name = " + mainNode.getNodeName());
			if(document.getChildNodes().getLength() != 1)
				throw new IllegalStateException("rootnode length is wrong");
			
			NamedNodeMap attrs = mainNode.getAttributes();
			
			Node analysis = attrs.getNamedItem("analysisClass");
			if(analysis != null) {
				lang.setAnalysisClass((Class<CodeAnalysis>) Class.forName(analysis.getNodeValue()));
			}
			Node suggest = attrs.getNamedItem("suggestClass");
			if(suggest != null) {
				String suggestName = suggest.getNodeValue();
				Class suggestClass = Class.forName(suggestName);
				lang.setSuggest((CodeSuggest) suggestClass.newInstance());
			}
			
			String langName = attrs.getNamedItem("name").getNodeValue();
			lang.setLanguageName(langName);
			
			NodeList rootList = mainNode.getChildNodes();
			
			for(int i = 0; i < rootList.getLength(); i++) {
				Node node = rootList.item(i);
				//Log.d(".", "i = " + i);
				switch(node.getNodeName()) {
					case "data": {
						
						NamedNodeMap attrs2 = node.getAttributes();
						String name = attrs2.getNamedItem("name").getNodeValue();
						String type = attrs2.getNamedItem("type").getNodeValue();
						//Log.d(".", node.getNodeValue() + ", " + node.getTextContent());
						lang.setData(name, TypeUtils.getObjectFromString(node.getTextContent(), type));
						break;
					}
					
					case "arg": {
						NamedNodeMap attrs2 = node.getAttributes();
						String name = attrs2.getNamedItem("name").getNodeValue();
						String _value = attrs2.getNamedItem("value").getNodeValue();
						Object value;
						String type = null;
						if(attrs2.getNamedItem("type") != null)
							type = attrs2.getNamedItem("type").getNodeValue();
						if(_value.charAt(0) == '@') {
							if(_value.startsWith("@data/")) {
								value = lang.getData(_value.substring(6), null);
							} else throw new IllegalStateException("wrong value on args: " + _value);
						} else {
							value = TypeUtils.getObjectFromString(_value, type);
						}
						lang.setArgByValue(name, value);
						break;
					}
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return lang;
	}
}
