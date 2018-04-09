package com.asm.language;

import com.asm.widget.codeedit.CodeStyle;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;


public final class LanguageLoader
{
	@SuppressWarnings("raw-types")
	public static Language fromXml(InputStream is) {
		BaseLanguage lang;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);
			
			Node mainNode = document.getFirstChild();
			if(mainNode.getNodeName() != "language")
				throw new IllegalStateException("root node is not language");
			if(document.getChildNodes().getLength() != 1)
				throw new IllegalStateException("rootnode length is wrong");
			
			if(document.getAttributes().getNamedItem("baseClass") == null) {
				lang = new BaseLanguage();
			} else {
				String className = document.getAttributes().getNamedItem("baseClass").getNodeValue();
				Class baseClass = Class.forName(className);
				lang = (BaseLanguage) baseClass.getConstructor().newInstance();
			}
			String langName = document.getAttributes().getNamedItem("name").getNodeValue();
			lang.setLanguageName(langName);
			
			NodeList rootList = mainNode.getChildNodes();
			for(int i = 0; i < rootList.getLength(); i++) {
				Node node = rootList.item(i);
				switch(node.getNodeName()) {
					case "item": {
						NamedNodeMap attrs = node.getAttributes();
						String name = attrs.getNamedItem("name").getNodeValue();
						
						break;
					}
//					case "color": {
//						NodeList list = node.getChildNodes();
//						CodeStyle.ColorValue color = CodeStyle.ColorValue.fromString(node.getAttributes().getNamedItem("colorName").getNodeValue());
//						for(int ii = 0; ii < list.getLength(); i++) {
//							Node node2 = list.item(ii);
//							
//						}
//						break;
//					}
					
					
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return lang;
	}
}
