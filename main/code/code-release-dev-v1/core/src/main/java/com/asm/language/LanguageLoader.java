package com.asm.language;

import com.asm.analysis.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import com.lhw.util.TypeUtils;


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
			if(mainNode.getNodeName() != "language")
				throw new IllegalStateException("root node is not language");
			if(document.getChildNodes().getLength() != 1)
				throw new IllegalStateException("rootnode length is wrong");
			
			NamedNodeMap attrs = document.getAttributes();
			
			if(document.getAttributes().getNamedItem("baseClass") == null) {
				lang = new BaseLanguage();
			} else if(attrs != null) {
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
			}
			String langName = document.getAttributes().getNamedItem("name").getNodeValue();
			lang.setLanguageName(langName);
			
			NodeList rootList = mainNode.getChildNodes();
			for(int i = 0; i < rootList.getLength(); i++) {
				Node node = rootList.item(i);
				switch(node.getNodeName()) {
					case "data": {
						NamedNodeMap attrs2 = node.getAttributes();
						String name = attrs2.getNamedItem("name").getNodeValue();
						String type = attrs2.getNamedItem("type").getNodeValue();
						lang.setData(name, TypeUtils.getObjectFromString(node.getTextContent(), type));
						break;
					}
					
					case "arg": {
						NamedNodeMap attrs2 = node.getAttributes();
						String name = attrs2.getNamedItem("name").getNodeValue();
						String value = attrs2.getNamedItem("value").getNodeValue();
						String type = null;
						if(attrs2.getNamedItem("type") != null)
							type = attrs2.getNamedItem("type").getNodeValue();
						lang.setArg(name, type, value);
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
