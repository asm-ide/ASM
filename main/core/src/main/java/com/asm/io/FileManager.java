package com.asm.io;

import java.io.IOException;
import java.util.Set;
import java.util.HashMap;


// TODO : add support about RandomAcrossFile

/** currently temporary */
public class FileManager implements SaveManager
{
	private HashMap<SaveDest, FileStream> files;
	
	
	public FileManager() {
		files = new HashMap<>();
	}
	
	@Override
	public Stream open(SaveDest dest) throws IOException {
		if(files.containsKey(dest)) {
			FileStream stream = files.get(dest);
			if(stream.isClosed()) files.remove(dest);
			else return stream;
		}
		
		FileStream stream = new FileStream(dest.name, dest.mode);
		files.put(dest, stream);
		return stream;
	}
	
	@Override
	public Set<SaveDest> list() {
		cleanUpClosed();
		return files.keySet();
	}
	
	private void cleanUpClosed() {
		for(SaveDest dest : files.keySet()) {
			if(files.get(dest).isClosed())
				files.remove(dest);
		}
	}
}
