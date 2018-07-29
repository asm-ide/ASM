package com.asm.gongbj.tools;
import android.app.*;
import com.asm.gongbj.*;

public class Ecj
{
	private String androidJarPath;
	private Activity ac;
	public Ecj(Activity activity, String androidJarPath){
		ac = activity;
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
		command += "-bootclasspath ";
		String b = "";
		for(String t : sourcePath){
			b += t + ":";
		}
		b = b.substring(0,b.length()-1);
		command += b + " ";
		command += mainSourcePath;
		
		return apkBuilder.runJavaCompiler(command);
	}
	
	
}
