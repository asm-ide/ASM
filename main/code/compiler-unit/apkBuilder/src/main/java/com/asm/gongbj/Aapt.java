package com.asm.gongbj;

public class Aapt
{
	String jarPath;
	public Aapt(String android_jar_path){
		jarPath = android_jar_path;
	}
	public String generateR(String path,String androidManifetPath, String resPath[]){
		String cmd = "";
		cmd += "aapt package ";
		cmd += "-m ";
		cmd += "-J " + path + " ";
		cmd += "-M " + androidManifetPath + " ";
		for(String res : resPath){
			cmd += "-S " + res + " ";
		}
		cmd += "-I " + jarPath;
		
		return apkBuilder.runAapt(cmd);
	}
	
	public String generateApk(){
		return null;
	}
}
