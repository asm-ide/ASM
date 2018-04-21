package com.asm.gongbj.tools;

import com.asm.gongbj.apkBuilder;

public class Aapt
{
	String jarPath;
	public Aapt(String android_jar_path){
		jarPath = android_jar_path;
	}
	public String generateR(String rPath,String androidManifetPath, String resPath[]){
		String cmd = "";
		cmd += "aapt package ";
		cmd += "-m ";
		cmd += "-J " + rPath + " ";
		cmd += "-M " + androidManifetPath + " ";
		for(String res : resPath){
			cmd += "-S " + res + " ";
		}
		cmd += "-I " + jarPath;

		return apkBuilder.runAapt(cmd);
	}

	public String generateApk(String apkPath, String androidManifestPath, String resPath[], String jarPath[]){
		String cmd = "aapt package -f -M ";
		cmd += androidManifestPath;
		for(String tmp : resPath){
			cmd = cmd + " -S " + tmp;
		}
		for(String tmp : jarPath){
			cmd = cmd + " -j " + tmp;
		}
		cmd = cmd + " -F " + apkPath;
		cmd += "-I " + jarPath;

		return apkBuilder.runAapt(cmd);
	}

	public String addFileInApk(String apkPath, String targetFilePath){
		String cmd = "aapt add -f -k ";
		cmd += apkPath;
		cmd += targetFilePath;

		return apkBuilder.runAapt(cmd);
	}

	public String removeFileInApk(String apkPath, String targetFilePath){
		String cmd = "aapt remove -f ";
		cmd += apkPath;
		cmd += targetFilePath;

		return apkBuilder.runAapt(cmd);
	}
}
