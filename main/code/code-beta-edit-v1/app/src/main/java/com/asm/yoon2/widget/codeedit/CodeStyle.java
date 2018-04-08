package com.asm.yoon2.widget.codeedit;

import android.graphics.*;
import android.util.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import android.content.res.*;


public class CodeStyle implements Serializable
{
	private static CodeStyle defaultCodeStyle;
	private static final String DEFAULTCODESTYLEPATH = "code-style/default.xml";
	
	public static final String COLOR_FOREGROUND = "foreground";
	
	private HashMap<String, Integer> colors = new HashMap<String, Integer>();
	private String currentLanguage;
	
	
	public CodeStyle()
	{}

	public void setCurrentLanguage(String lang)
	{
		this.currentLanguage = lang;
	}

	public String getCurrentLanguage()
	{
		return currentLanguage;
	}
	
	public int getColor(String name)
	{
		if(colors.containsKey(currentLanguage + "." + name)) return colors.get(currentLanguage + "." + name);
		return colors.get("base." + name);
	}
	
	public int getColor(String lang, String name)
	{
		return colors.get(lang + "." + name);
	}
	
	public void putColor(String key, int value)
	{
		colors.put(key, value);
	}
	
	//apply to me
	public void apply(CodeStyle style, boolean force)
	{
		for(String key : style.colors.keySet().toArray(new String[0])) if(force || !colors.containsKey(key)) colors.put(key, style.colors.get(key));
		
	}
	
	public CodeStyle clone()
	{
		CodeStyle style = new CodeStyle();
		style.colors = (HashMap<String, Integer>) colors.clone();
		style.currentLanguage = currentLanguage;
		return style;
	}
	
	/**
	 * Parse @code(CodeStyle) from xml.
	 * example:
	 *	<code-style>
	 *		<language lang="base">
	 *			<!--can across to getColor("base.foreground")-->
	 *			<color name="foreground">#000000</color>
	 *			<color name="background">#00000000</color>
	 *			<color name="reserved">#3333cc</color>
	 *			..etc.
	 *			<preference name="textSize" type="number">
	 *				<!--default value-->14f
	 *			</preference>
	 *		</language>
	 *	<code-style>
	 */
	public static CodeStyle parse(InputStream is) throws ParserConfigurationException, IOException, SAXException, Exception
	{
		return parse(new CodeStyle(), is);
	}
	
	public static CodeStyle parse(CodeStyle style, InputStream is) throws ParserConfigurationException, IOException, SAXException, Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(is);
		//<code-style>
		
		NodeList languages = document.getChildNodes();
		for(int i = 0; i < languages.getLength(); i++) {
			Node itemHead = languages.item(i); //<language 
			switch(itemHead.getNodeName()) {
				case "language":
					String lang = itemHead.getAttributes().getNamedItem("lang").getNodeValue(); //lang="???">
					NodeList items = itemHead.getChildNodes();
					for(int ii = 0; ii < items.getLength(); ii++) {
						Node item = items.item(ii);
						try {
							switch(item.getNodeName()) {
								case "color": //<color name="???">#??????</color>
									style.putColor(lang + "." + item.getAttributes().getNamedItem("name").getNodeValue(), Color.parseColor(item.getNodeValue()));
									Log.d("CodeStyle", item.getAttributes().getNamedItem("name").getNodeValue() + "=" + item.getNodeValue());
									break;
								case "preference":
									
								case "import-default":
									
							}
						} catch(Exception e) { throw new Exception("error on item on lang=" + lang + ", no=" + ii + ", tag=" + item.getNodeName()); }
					}
					break;
					
				case "import-default":
					boolean force = false;
					if(itemHead.getAttributes() != null && itemHead.getAttributes().getNamedItem("force") != null) {
						force = itemHead.getAttributes().getNamedItem("force").getNodeValue() == "true";
					}
					style.apply(defaultCodeStyle, force);
			}
		}
		return style;
	}
	
	public static void loadDefaultStyleIfNotLoaded(AssetManager assets)
	{
		if(defaultCodeStyle == null){
			try {
				defaultCodeStyle = CodeStyle.parse(assets.open(DEFAULTCODESTYLEPATH));
			} catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	public static CodeStyle peekDefault()
	{
		return defaultCodeStyle;
	}
	
	public static CodeStyle peekNewDefaultStyle()
	{
		return defaultCodeStyle.clone();
	}
	
	public static CodeStyle getDefault(AssetManager manager)
	{
		CodeStyle.loadDefaultStyleIfNotLoaded(manager);
		return CodeStyle.peekDefault();
	}
}
