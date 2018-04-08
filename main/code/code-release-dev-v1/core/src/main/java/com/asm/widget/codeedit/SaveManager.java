package com.asm.widget.codeedit;

import java.io.IOException;
import java.util.HashMap;


/** for save many different types of files. */
public interface SaveManager
{
	/** for manager's each save data */
	public static interface SaveDest {
		
	}
	
	
	public void save(String name, TextData text) throws IOException;
	
	public void read(String name, TextData dest) throws IOException;
	
	public HashMap<String, SaveDest> list();
}
