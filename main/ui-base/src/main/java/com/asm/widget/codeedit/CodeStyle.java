package com.asm.widget.codeedit;

import com.asm.annotation.NonNull;
import com.asm.annotation.Nullable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.asm.Settings.codestyle.*;


/**
 * The CodeStyle is defines colors of a CodeEdit.
 * You can inflate it from xml, by CodeStyle.parse().
 */
public class CodeStyle implements Serializable, CodeStyleInterface
{
	/**  */
	public static class ColorValue implements Serializable
	{
		/** the main value. */
		private int value;
		
		/**
		 * the type of value.
		 * null does means color is same as value.
		 * '@pref/{pref_name}' does means get color from preferences.
		 * in this case, method value will be ignored.
		 * '@color/{color_name}' does means get color defined from
		 * its parent.
		 */
		private @Nullable String type;
		
		/** parent. */
		private transient CodeStyle parent;
		
		private String name;
		
		private String lang;
		
		private int colCache;
		
		private boolean isColCached = false;
		
		
		public ColorValue(CodeStyle parent) {
			this.parent = parent;
			lang = parent.currentLanguage;
		}
		
		/** Construct ColorValue with color. */
		public ColorValue(CodeStyle parent, int value) {
			this.parent = parent;
			setColor(value);
		}
		
		public ColorValue(CodeStyle style, int value, String name) {
			this(style, value);
			this.name = name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		/**
		 * Returns a value of this color.
		 * May throws a error when color value is wrong.
		 */
		public int getColor() {
			if(isColCached) return colCache;
			else return getColorRefresh();
		}
		
		private int getColorRefresh() {
			isColCached = true;
			
			if(type == null) return value;
			else if(type.startsWith("@pref/")) {
				String name = "style_" + lang + nameSeperator + type.substring(6);
				Log.d("ColorValue", "getColor pref " + name); //TODO: DEBUG
				if(parent.pref.contains(name)) return parent.pref.getInt(name, -1);
				else throw new IllegalStateException("preference " + name + " is defined, but not exist on preferences");
			}
			else if(type.startsWith("@color/")) {
				String name = type.substring(7);
				return parent.getColor(name);
			}
			else if(type.charAt(0) == '@' && type.contains(":color/")) {
				String lang = type.substring(1, type.indexOf(":color/") + 7);
				String name = type.substring(lang.length());
				return parent.getColor(lang, name);
			}
			else throw new RuntimeException("UNKNOWN ERROR: wrong type: " + type);
		}
		
		/**
		 * Set the constant color.
		 */
		public void setColor(int color) {
			value = color;
			type = null;
		}
		
		/**
		 * Return the id.
		 */
		public short getId() {
			return parent.getId(name);
		}
		
		
		/**
		 * Set the color from resource.
		 * '@pref/{pref_name}' does means get color from preferences.
		 * '@color/{color_name}' does means get color defined from
		 * its parent.
		 */
		public void setColorRes(String resName) {
			type = resName;
		}
		
		/**
		 * As same as
		 * <code>ColorValue color =  new ColorValue();
		 * color.setColorRes(res);</code>
		 */
		public static ColorValue res(CodeStyle parent, String name) {
			ColorValue value = new ColorValue(parent);
			value.type = name;
			return value;
		}
		
		/**
		 * If str is color code, return new ColorValue(Color.parseColor(str).
		 * Otherwise, return ColorValue.res(str).
		 */
		public static ColorValue fromString(CodeStyle parent, String str) {
			if(str.charAt(0) == '#') return new ColorValue(parent, Color.parseColor(str));
			else return ColorValue.res(parent, str);
		}
		
		public ColorValue clone() {
			ColorValue value = new ColorValue(parent);
			value.value = this.value;
			value.type = type;
			return value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof NameContrast) return obj.equals(this);
			return super.equals(obj);
		}
		
		public Object nameContrast() {
			return new NameContrast(name);
		}
		
		private static class NameContrast
		{
			private String name;
			
			
			public NameContrast(String name) {
				this.name = name;
			}
			
			public boolean equals(Object other) {
				return other instanceof ColorValue && ((ColorValue) other).name.equals(name);
			}
		}
	}
	
	
	/** foreground color. texts without highlighging is like this. */
	public static final String COLOR_FOREGROUND = "base.foreground";
	
	/** default code style. */
	private static CodeStyle defaultCodeStyle;
	
	/** where default code style located in assets. */
	private static final String DEFAULTCODESTYLEPATH = "code-style/default.xml";
	
	/** color values */
	private ArrayList<ColorValue> colors = new ArrayList<>();
	
	/** preferences to load color values. */
	private transient SharedPreferences pref;
	
	/**
	 * current language name for load color values.
	 * "base", "java", "c", "xml", or etc.
	 */
	private String currentLanguage;
	
	/** default typeface for drawing texts. */
	private transient Typeface typeface;
	
	/** For security */
	private boolean isFromXml = false;
	
	
	public CodeStyle() {}
	
	/**
	 * set the default typeface.
	 */
	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
	}
	
	/**
	 * return the default typeface.
	 */
	public Typeface getTypeface() {
		return typeface;
	}
	
	/**
	 * set the current language name.
	 */
	public void setCurrentLanguage(String lang) {
		this.currentLanguage = lang;
	}
	
	/**
	 * return the current language name.
	 */
	public String getCurrentLanguage() {
		return currentLanguage;
	}
	
	/**
	 * return the color calculated.
	 */
	@Override
	public int getColor(String name) {
		return getColorValue(name).getColor();
	}
	
	@Override
	public int getColor(short id) {
		return getColorValue(id).getColor();
	}
	
	@Override
	public short getId(String name) {
		return (short) containsKeyIndex(name);
	}
	
	/**
	 * return the ColorValue.
	 * name format can be "{name}" or "{language}.{name}".
	 * In first one, if in the current language color name is exist,
	 * return getColorValue("{currentLanguage}.{name}") or if "base.{name}"
	 * exist, return that.
	 * In second one, just return it.
	 */
	public ColorValue getColorValue(String name) {
		if(name.contains(nameSeperator)) return get(name);
		if(containsKey(currentLanguage + nameSeperator + name)) return get(currentLanguage + nameSeperator + name);
		return get("base" + nameSeperator + name);
	}
	
	public ColorValue getColorValue(int id) {
		return colors.get(id);
	}
	
	/**
	 * return the color "{lang}.{name}"
	 */
	@Override
	public int getColor(String lang, String name) {
		return get(lang + nameSeperator + name).getColor();
	}
	
	/**
	 * Set the color.
	 */
	@Override
	public short setColor(String key, int value) {
		return setColor(key, new ColorValue(this, value));
	}
	
	public short setColor(String key, ColorValue value) {
		return put(key, value);
	}
	
	/** 
	 * return the SharedPreferences was setted.
	 */
	public SharedPreferences getPreferences() {
		return pref;
	}
	
	/**
	 * set the preferences.
	 */
	public void setPreference(SharedPreferences sf) {
		pref = sf;
	}
	
	/**
	 * Apply the other code style from {@code other.
	 * Only colors will be applied.
	 * <p>
	 * In color, if force is false, each color value will
	 * be added if not exist in this or if forcr is true,
	 * just change the value.
	 */
	public void apply(CodeStyle other, boolean force) {
		//if more applies, need to adjust javadoc.
		{ //colors
			for(ColorValue value : other.colors) {
				if(force || !containsKey(value.name)) {
					put(value.name, value.clone()); // TODO : is value.name should be cloned?
				}
			}
		}
		//else
	}
	
	public CodeStyle clone() {
		CodeStyle style = new CodeStyle();
		style.colors = new ArrayList<>();
		for(ColorValue value : colors) {
			style.colors.add(value.clone());
		}
		style.currentLanguage = currentLanguage;
		return style;
	}
	
	public void onReadFromSeriazable(SharedPreferences pref) {
		this.pref = pref;
		
		for(ColorValue value : colors) {
			value.parent = this;
		}
	}
	
	private boolean containsKey(String key) {
		return containsKeyIndex(key) != -1;
	}
	
	private int containsKeyIndex(String key) {
		for(int i = 0; i < colors.size(); i++) {
			if(key.equals(colors.get(i).name)) return i;
		}
		return -1;
	}
	
	private ColorValue get(String name) {
		for(ColorValue value : colors) {
			if(name.equals(value.name)) return value;
		}
		throw new IllegalArgumentException("key " + name + " is not exist");
	}
	
	private short put(String name, ColorValue value) {
		if(colors.size() > (2 ^ 15) - 1) throw new OutOfMemoryError();
		value.name = name;
		int index = containsKeyIndex(name);
		if(index == -1) {
			colors.add(value);
			return (short) (colors.size() - 1);
		} else {
			colors.set(index, value);
			return (short) index;
		}
	}
	
	/**
	 * @see CodeStyle#parse(CodeStyle, Context, String, InputStream)
	 */
	public static CodeStyle parse(Context context, String sf, InputStream is) throws ParserConfigurationException, IOException, SAXException {
		return parse(new CodeStyle(), context, sf, is);
	}
	
	/**
	 * Parse <code>CodeStyle</code> from xml.
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
	public static CodeStyle parse(CodeStyle style, Context context, String sf, InputStream is) throws ParserConfigurationException, IOException, SAXException {
		style.isFromXml = true;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(is);
		//<code-style>
		NodeList languages = document.getChildNodes().item(0).getChildNodes();
		style.pref = context.getSharedPreferences(sf, Context.MODE_PRIVATE);
		SharedPreferences.Editor sfEditor = style.pref.edit();
		for(int i = 0; i < languages.getLength(); i++) {
			Node item2 = languages.item(i); //<language 
			switch(item2.getNodeName()) {
				case "import-default": {
					getDefault(context);
					boolean force = false;
					if(item2.hasAttributes() && item2.getAttributes().getNamedItem("force").getNodeValue() == "true") force = true;
					style.apply(defaultCodeStyle, force);
					break;
				}
				case "include": {
					boolean force = false;
					if(item2.hasAttributes() && item2.getAttributes().getNamedItem("force").getNodeValue() == "true") force = true;
					String path = item2.getAttributes().getNamedItem("path").getNodeValue();
					int sfIndex = sf.lastIndexOf('/');
					if(sfIndex == -1) sfIndex = 0;
					style.apply(parse(context, sf.substring(sfIndex), context.getAssets().open(path)), force);
					
					break;
				}
				case "language": {
					String lang = item2.getAttributes().getNamedItem("lang").getNodeValue(); //lang="???">
					NodeList items = item2.getChildNodes();
					for(int ii = 0; ii < items.getLength(); ii++) {
						Node item = items.item(ii);
						try {
							switch(item.getNodeName()) {
								case "color": { //<color name="???">#??????</color>
									String name = item.getAttributes().getNamedItem("name").getNodeValue();
									String strColor = item.getTextContent(); //?
									//if(style.colors.containsKey(name)) Log.w("CodeStyle", "override color value:" + name);
									ColorValue value = ColorValue.fromString(style, strColor);
									value.lang = lang;
									style.put(lang + "." + name, value);
									Log.d("CodeStyle", "colors " + name + "=" + strColor);
									break;
								}
								case "preference": {
									String name = "style_" + languages + nameSeperator + item.getAttributes().getNamedItem("name").getNodeValue();
									if(item.getTextContent() != null){// && !style.pref.contains(name)) {
										String content = item.getTextContent();
										Log.d("CodeStyle", "pref " + name + "=" + content);
										switch(item.getAttributes().getNamedItem("type").getNodeValue()) {
											case "color": sfEditor.putInt(name, ColorValue.fromString(style, content).getColor()); break;
											case "integer": sfEditor.putInt(name, Integer.parseInt(content)); break;
											case "float": sfEditor.putFloat(name, Float.parseFloat(content)); break;
											case "long": sfEditor.putLong(name, Long.parseLong(content)); break;
											case "string": sfEditor.putString(name, content); break;
											case "boolean": sfEditor.putBoolean(name, Boolean.parseBoolean(content)); break;
											default: throw new Exception("unknown type: " + item.getAttributes().getNamedItem("type").getNodeValue());
										}
									}
									break;
								} //end case
							} //end switch
						} catch(Exception e) { throw new IllegalStateException("error on item on lang=" + lang + ", no=" + ii + ", tag=" + item.getNodeName()); }
					}
					break;
				}
				default:
					throw new RuntimeException("wrong xml item: " + item2.getNodeName());
			}
		}
		sfEditor.apply();
		return style;
	}
	
	/** if default not exist, create default code style and return. */
	@NonNull
	public static CodeStyle getDefault(Context context) {
		
		if(defaultCodeStyle == null) {
			try {
				defaultCodeStyle = parse(context, preferencePath, context.getAssets().open(DEFAULTCODESTYLEPATH));
			} catch(Exception e) { throw new IllegalStateException("cannot open/parse default codestyle.", e); }
		}
		return defaultCodeStyle;
	}
	
	/** return default code style but might be null. */
	@Nullable
	public static CodeStyle peekDefault() {
		return defaultCodeStyle;
	}
}
