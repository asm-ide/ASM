package com.asm.preference;

import com.asm.Consts;

import android.support.v7.preference.PreferenceDataStore;

import android.util.Log;

import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;


// TODO: is this work well?


public class PreferenceDataStoreImpl extends PreferenceDataStore
{
	private HashMap<String, Object> mData = new HashMap<>();
	private boolean mDirty = false;
	
	
	public PreferenceDataStoreImpl() {
		
	}
	
	
	public void openFromXML(String path) throws IOException, XmlPullParserException {
		openFromXML(new FileInputStream(path));
	}
	
	public void openFromXML(InputStream is) throws IOException, XmlPullParserException {
		openFromXML(new InputStreamReader(is));
	}
	
	/**
	 * example:
	 * 	<preference>
	 *		<int key="abc">123</int>
	 *		<string-set key="def">
	 *			<item>uskdkw88ri2ii</item>
	 *		</string-set>
	 *	</preference>
	 */
	public void openFromXML(Reader reader) throws IOException, XmlPullParserException {
		if(reader == null) throw new NullPointerException("reader is null");
		
		XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
		parser.setInput(reader);
		
		int event = parser.getEventType();
		event = parser.next(); // <?xml version="1.0" encoding="utf-8"?>
		if(parser.getName().equalsIgnoreCase("xml"))
			event = parser.next(); // <language>
		if(!parser.getName().equalsIgnoreCase("preference"))
			throw new XmlPullParserException("tag not starts by <preference>");
		
		String key = null;
		
		while(event != END_DOCUMENT) {
			switch(event) {
				case START_DOCUMENT:
					// never receive
					break;
					
				case START_TAG:
					key = parser.getAttributeValue(null, "key");
					if(key == null) 
						throw new NullPointerException("key is null");
					break;
					
				case TEXT:
					if(key == null) {
						// unexepted
					} else {
						switch(key) {
							case "int":
								mData.put(key, Integer.parseInt(parser.getText()));
								break;
								
							case "float":
								mData.put(key, Float.parseFloat(parser.getText()));
								break;
							
							case "long":
								mData.put(key, Long.parseLong(parser.getText()));
								break;
							
							case "string":
								mData.put(key, parser.getText());
								break;
							
							case "boolean":
								mData.put(key, Boolean.parseBoolean(parser.getText()));
								break;
							
							case "string-set":
								ArrayList<String> list = new ArrayList<>();
								loop : while(true) {
									switch(event) {
										case START_TAG:
											if(!parser.getName().equals("item"))
												throw new XmlPullParserException("unknown tag: " + parser.getName());
											break;
											
										case TEXT:
											list.add(parser.getText());
											break;
											
										case END_TAG:
											switch(parser.getName()) {
												case "item": break;
												case "string-set": break loop;
												default: throw new XmlPullParserException("unexepted closing tag: " + parser.getName());
											}
											break;
									}
									event = parser.next();
								}
								mData.put(key, list);
								break;
						}
						break;
					}
					
					break;
					
				case END_TAG:
					
					break;
			}
			event = parser.next();
		}
		
		mDirty = true;
	}
	
	public void setSource(File file) {
		// TODO : support save
	}
	
	
	@Override
	public void putInt(String key, int value) {
		mData.put(key, value);
		mDirty = true;
	}
	
	@Override
	public void putFloat(String key, float value) {
		mData.put(key, value);
		mDirty = true;
	}
	
	@Override
	public void putLong(String key, long value) {
		mData.put(key, value);
		mDirty = true;
	}
	
	@Override
	public void putString(String key, String value) {
		mData.put(key, value);
		mDirty = true;
	}
	
	@Override
	public void putBoolean(String key, boolean value) {
		mData.put(key, value);
		mDirty = true;
	}
	
	@Override
	public void putStringSet(String key, Set<String> values) {
		mData.put(key, values);
		mDirty = true;
	}
	
	@Override
	public boolean getBoolean(String key, boolean defValue) {
		return mData.get(key);
	}
	
	@Override
	public float getFloat(String key, float defValue) {
		return getOrDefault(key, defValue);
	}
	
	@Override
	public long getLong(String key, long defValue) {
		return getOrDefault(key, defValue);
	}
	
	@Override
	public int getInt(String key, int defValue) {
		return getOrDefault(key, defValue);
	}
	
	@Override
	public Set<String> getStringSet(String key, Set<String> defValues) {
		return getOrDefault(key, defValues);
	}
	
	@Override
	public String getString(String key, String defValue) {
		return getOrDefault(key, defValue);
	}
	
	@SuppressWarnings("unsafe")
	private <T> T getOrDefault(String key, T defValue) {
		try {
			return mData.containsKey(key)? (T) mData.get(key) : defValue;
		} catch(ClassCastException e) {
			Log.e("PreferenceDataStoreImpl",
				"given type "
				+ defValue.getClass().getSimpleName()
				+ " is not match, original type: "
				+ mData.get(key).getClass().getSimpleName()
				+ ", key = " + key);
			
			if(Consts.DEBUG_DOTHROW)
				throw e;
			return defValue;
		}
	}
}
