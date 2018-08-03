package com.asm.gongbj.tools;

import android.content.res.*;
import android.os.*;
import com.asm.gongbj.*;
import java.io.*;
import android.content.*;

public class Aapt
{
	String androidJarPath;
	public Aapt(String android_jar_path){
		androidJarPath = android_jar_path;
	}

	/**
	 return the android jar path that the app can use.
	 if there's no jar file, it returns null
	 */
	public static String getAndroidJarPath(){
		File destFile = new File(Environment.getExternalStorageDirectory()+"/.ASM/android.jar");
		if(destFile.exists()){
			return destFile.getAbsolutePath();
		}else return null;
	}

	public static String requestAndroidJar(Context activity) throws Exception{
		File destFile = new File(Environment.getExternalStorageDirectory()+"/.ASM/android.jar");
		if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();
		if (!destFile.exists()) {
			destFile.createNewFile();
			AssetManager assetMgr = activity.getAssets();
			InputStream in = assetMgr.open("android.jar");
			OutputStream out = new FileOutputStream(destFile);
			copyFile(in,out);
		}
		return destFile.getAbsolutePath();
	}
	public String generateR(String rPath,String androidManifetPath, String resPath[], String jarPath[]){
		String cmd = "";
		cmd += "package ";
		cmd += "-m ";
		cmd += "-J " + rPath + " ";
		cmd += "-M " + androidManifetPath + " ";
		for(String res : resPath){
			
			if(res!=null)cmd += "-S " + res + " ";
		}
		cmd = cmd + "-I " + androidJarPath + " ";
		if(jarPath!=null){
			for(String jar : jarPath){
				cmd += "-I " + jar + " ";
			}
		}

		return cmd + "\n" + apkBuilder.runAapt(cmd);
	}

	public String generateApk(String apkPath, String androidManifestPath, String resPath[], String jarPath[]){
		String cmd = "package -f -M ";
		cmd += androidManifestPath;
		for(String tmp : resPath){
			cmd = cmd + " -S " + tmp;
		}
		for(String tmp : jarPath){
			cmd = cmd + " -j " + tmp;
		}
		cmd = cmd + " -F " + apkPath;
		cmd += "-I " + androidJarPath;

		return apkBuilder.runAapt(cmd);
	}

	public String addFileInApk(String apkPath, String targetFilePath){
		String cmd = "add -f -k ";
		cmd += apkPath;
		cmd += targetFilePath;

		return apkBuilder.runAapt(cmd);
	}

	public String removeFileInApk(String apkPath, String targetFilePath){
		String cmd = "remove -f ";
		cmd += apkPath;
		cmd += targetFilePath;

		return apkBuilder.runAapt(cmd);
	}
	private static void copyFile(InputStream in, OutputStream out) throws IOException { byte[] buffer = new byte[1024]; int read; while ((read = in.read(buffer)) != -1) { out.write(buffer, 0, read); } }

}
