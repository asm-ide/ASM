package com.asm.gongbj.tools;

import java.io.*;
import com.asm.gongbj.*;

public class Dx
{
	
	public Dx(){
		
	}
	
	public void jar2dex(String jarPath,String outputPath){
		String command = "";
		command += "--dex --no-strict ";
		command += "--output=" + outputPath + " ";
		command += jarPath;
		apkBuilder.runDx(command);
	}
	public void class2dex(String targets[], String outputPath){
		String command = "";
		command += "--dex --no-strict ";
		command += "--output=" + outputPath + " ";
		for(String t : targets){
			command += splitPath(t) + " ";
		}
		command = command.trim();
		apkBuilder.runDx(command);
	}
	private String splitPath(String path){
		File f = new File(path);
		if(!f.isDirectory())return path;
		String r = "";
		for(String t : f.list()){
			r += t + " ";
		}
		return r.substring(0,r.length()-1);
	}
}
