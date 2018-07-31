package com.asm.io;

import com.asm.text.TextData;

import java.io.IOException;
import java.util.Set;


/** for save many different types of files. */
public interface SaveManager
{
	/** for manager's each save data */
	public static class SaveDest {
		public String name;
		public int mode;
		
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof SaveDest)) return false;
			SaveDest dest = (SaveDest) obj;
			return name.equals(dest.name) && mode == dest.mode;
		}
	}
	
	
	public Stream open(SaveDest dest) throws IOException;
	
	public Set<SaveDest> list();
}
