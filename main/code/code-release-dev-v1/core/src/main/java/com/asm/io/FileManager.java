package com.asm.io;

import com.asm.text.TextData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;


/** currently temporary */
public class FileManager implements SaveManager
{
	public static class FileData implements SaveManager.SaveDest
	{
		private File file;
		private FileReader fr;
		private FileWriter fw;
		
		
		FileData() {}
		
		public FileReader read() throws FileNotFoundException {
			if(fr == null) fr = new FileReader(file);
			return fr;
		}
		
		public FileWriter write() throws IOException {
			if(fw == null) fw = new FileWriter(file);
			return fw;
		}
		
		public void write(TextData text) throws IOException {
			FileWriter fw = write();
			fw.write(text.toString());
			fw.flush();
			fw.close();
		}
		
		public TextData readAll() throws FileNotFoundException, IOException {
			FileReader reader = read();
			String encoding = reader.getEncoding();
			StringBuilder builder = new StringBuilder(256);
			char[] buffer = new char[256];
			int i;
			while((i = reader.read(buffer)) > 0) {
				builder.append(buffer, 0, i);
			}
			reader.close();
			TextData data = TextData.from(builder);
			data.setEncoding(encoding);
			return data;
		}
		
		public static FileData create(File file) {
			FileData data = new FileData();
			data.file = file;
			return data;
		}
		
		public static FileData openFile(File file) throws IOException {
			FileData data = create(file);
			if(!file.canRead()) throw new IOException("file cannot be readed");
			
			return data;
		}
	}
	
	
	private HashMap<String, FileData> files;
	
	
	public FileManager() {
		files = new HashMap<String, FileData>();
	}
	
	@Override
	public void save(String name, TextData text) throws IOException {
		FileData data = files.get(name);
		data.write(text);
	}
	
	@Override
	public void read(String name, TextData dest) throws IOException {
		FileData data = files.get(name);
		dest.set(data.readAll());
	}

	@Override
	public HashMap<String, SaveManager.SaveDest> list() {
		return (HashMap<String, SaveManager.SaveDest>) files;
	}
}
