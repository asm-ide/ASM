package com.asm.gongbj.tools;
import com.asm.gongbj.*;
import java.io.*;

public class Ecj
{
	private String androidJarPath;
	
	public Ecj( String androidJarPath){
		this.androidJarPath = androidJarPath;
	}
	public String compile(String mainSourcePath, String sourcePath[], String destPath,String jar[]){
		String command = "";
		command += "-nowarn -1.5 ";
		command += "-cp " + androidJarPath + " ";
		command += "-d " + destPath + " ";
		for(String t : jar){
			command += "-cp " + t + " ";
		}
		command += "-sourcepath ";
		String b = "";
		for(String t : sourcePath){
			b += t + ":";
		}
		b = b.substring(0,b.length()-1);
		command += b + " ";
		command += mainSourcePath;
		
		return /*command + "\n" + */apkBuilder.runJavaCompiler(command);
	}
	private String splitPath(String path){
		File f = new File(path);
		if(!f.isDirectory())return path;
		String r = "";
		for(String t : f.list()){
			r += path+"/"+t + ":";
		}
		return r.substring(0,r.length()-1);
	}
	
	
}
