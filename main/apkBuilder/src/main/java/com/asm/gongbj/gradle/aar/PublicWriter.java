package com.asm.gongbj.gradle.aar;

import com.google.common.base.*;
import com.google.common.io.*;
import java.io.*;
import java.util.*;

public class PublicWriter
{
	private PublicLoader loader;
	
	public PublicWriter(PublicLoader loader){
		this.loader = loader;
	}
	
	public PublicWriter(){
		
	}
	
	public void setPublicLoader(PublicLoader loader){
		this.loader = loader;
	}
	
	public void write(File file) throws Exception{
		if(file.isDirectory()){
			throw new Exception("Cannot write to Directory");
		}
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		
		if(loader == null){
			throw new Exception("PublicLoader is null.");
		}
		
		BufferedWriter writer = Files.newWriter(file, Charsets.UTF_8);
		
		List<PublicLoader.Element> list = loader.elements;
		
		{
			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			writer.write("<resources>\n");
			for(PublicLoader.Element e : list){
				String newLine = "\t<public type=\"";
				newLine += e.type;
				newLine += "\" name=\"";
				newLine += e.name;
				newLine += "\" />\n";
				
				writer.write(newLine);
			}
			writer.write("</resources>");
			Closeables.closeQuietly(writer);
		}
		
	}
}
