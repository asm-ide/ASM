package com.asm.gongbj.gradle.aar;


import com.google.common.base.*;
import com.google.common.io.*;
import java.io.*;
import java.util.*;

public class PublicLoader
{
	private File file;
	public List<Element> elements;
	public class Element
	{
		public String type;
		public String name;
	}

	public PublicLoader(File file)
	{
		this.file = file;
	}

	public void load() throws Exception
	{
		List<String> lines = Files.readLines(file, Charsets.UTF_8);
		elements = new ArrayList<>();
		
		for(String str : lines){
			String splited[] = str.split(" ");
			Element e = new Element();
			e.type = splited[0];
			e.name = splited[1];
			elements.add(e);
		}
	}
}
