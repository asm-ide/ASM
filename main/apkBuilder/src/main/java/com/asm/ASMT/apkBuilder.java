package com.asm.ASMT;
import java.io.*;
import android.os.*;
import android.content.res.*;
import android.content.*;

import java.util.jar.*;
import android.app.*;
import com.asm.lib.io.StringWriterOutputStream;
import org.apache.commons.io.*;
import com.asm.ASMT.*;
import android.net.*;
import com.asm.gongbj.*;

public class apkBuilder
{
	//private Context context;
	private Activity activity;
	private String jarP="";
	private String path="";
	private String ok = "ExitValue: 0";
	
	
	
	
	public apkBuilder(Activity ac){
		//this.context = contextt;
		activity = ac;
	}
	
	public boolean prepare(){
		try{
			File destFile = new File(Environment.getExternalStorageDirectory()+"/ApkBuilder/android.jar");
			if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();
			if (!destFile.exists()) {
				destFile.createNewFile();


				AssetManager assetMgr = activity.getAssets();
				InputStream in = assetMgr.open("android.jar");
				OutputStream out = new FileOutputStream(destFile);
				copyFile(in,out);
				
			}
			G.fnInit(activity);
			return true;
		}catch(Exception e){
			return false;
		}
		
	}
	
	public Result buildApk(String projectPath,int sequenceCount) throws buildException{
		File jar = new File(Environment.getExternalStorageDirectory()+"/ApkBuilder/android.jar");
		if(!jar.exists()){
			throw new buildException("android.jar is not found",buildException.JAR_NOT_FOUND);
		}
		if(!("com.asm".equals(activity.getPackageName()))){
			throw new buildException("this java library is only for ASM /n PACKAGE_NAME not match Exception**",0);
		}
		jarP = Environment.getExternalStorageDirectory()+"/ApkBuilder/android.jar";
		
		path = projectPath+"/";
		
		if(sequenceCount==1){
			return buildApk_1();
		}else if(sequenceCount==2){
			return buildApk_2();
		}else if(sequenceCount==3){
			return buildApk_3();
		}else if(sequenceCount==4){
			return buildApk_4();
		}else if(sequenceCount==5){
			return buildApk_5();
		}else if(sequenceCount==6){
			return buildApk_6();
		}else{
			throw new buildException("unknown sequenceCount",buildException.UNKNOWN_SEQUENCE_NAME);
		}
		}
	private String logOut;
	private String aapt1,aapt2,ecj,dx,aapt3,ZipSigner; 
	private StringWriterOutputStream swos;
	
	private Result buildApk_1(){
		
		//activity.onActivityResult("Making R file...");
		logOut = logOut + "Make R";
		aapt1 = "package -m -J " + path + "gen/ -M " + path + "AndroidManifest.xml -S " + path + "res/ -I " + jarP;
		swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnAapt(aapt1);

		G.ide.fnLogOutput(swos);
		if(!swos.toString().contains(ok)){
			logOut = logOut + "\n" + swos.toString().replace(aapt1.replace(" ","￦n").replace("aapt￦n",""),"");
			//return null;
		}
		return new Result("making R",swos.toString(),getInt(swos.toString().contains(ok)));
		
	}
	
	private Result buildApk_2(){
		//#####################################
		//publishProgress("Preparing APK File...");
		//logOut = logOut + "\nPrepare Apk";
		aapt2 = "package -f -M " + path + "AndroidManifest.xml -S " + path + "res/ -I " + jarP + " -F " + path + "res.apk";
		swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnAapt(aapt2);

		G.ide.fnLogOutput(swos);
		if(!swos.toString().contains(ok)){
			logOut = logOut + "\n" + swos.toString().replace(aapt2.replace(" ","￦n").replace("aapt￦n",""),"");
			//return null;
		}
		return new Result("preparing apk",swos.toString(),getInt(swos.toString().contains(ok)));
		
	}
	public void moveJavas(){
		try{
			File srcDir = new File(path + "java"); File destDir = new File(path + "gen"); FileUtils.copyDirectory(srcDir, destDir);


		}catch(IOException i){
			logOut = logOut + "\n" + i.toString();
			//return null;
		}
	}
	public Result comlileCJ(String Path){
		logOut = logOut + "\nCompile java";
		ecj = "-cp " + jarP + " -1.5 -d none " + Path;
		
		swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnCompile(ecj);
		G.ide.fnLogOutput(swos);
		if(!swos.toString().contains(ok)){
			logOut = logOut + "\n" + swos.toString().replace(ecj.replace(" ","￦n"),"");
			//return null;
		}
		return new Result("compiling java",swos.toString(),getInt(swos.toString().contains(ok)));
		
	}
	private Result buildApk_3(){
		//publishProgress("Moving Java files to gen folder...");
		logOut = logOut + "\nMove javas";
		try{
			File srcDir = new File(path + "java"); File destDir = new File(path + "gen"); FileUtils.copyDirectory(srcDir, destDir);


		}catch(IOException i){
			logOut = logOut + "\n" + i.toString();
			//return null;
		}
		//#####################################
		//publishProgress("Compiling java files...");
		logOut = logOut + "\nCompile java";
		ecj = "-cp " + jarP + " -1.5 -d none " + path + "gen";
		swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnCompile(ecj);
		G.ide.fnLogOutput(swos);
		if(!swos.toString().contains(ok)){
			logOut = logOut + "\n" + swos.toString().replace(ecj.replace(" ","￦n"),"");
			//return null;
		}
		return new Result("compiling java",swos.toString(),getInt(swos.toString().contains(ok)));
		
	}
	
	private Result buildApk_4(){
		//publishProgress("Compressing compiled classes...");
		logOut = logOut + "\nCompress classes";
		try {
			File file1 = new File(path + "gen/");
			File file2 = new File(path + "gen/com.zip");
			zipFile z = new zipFile();

			z.zipFileAtPath(path + "gen",path + "gen/com.zip");

		}catch(Exception n){
			logOut = logOut + "￦n" + n.toString();
		}
		//#####################################v
		//publishProgress("Dx running...");
		dx = "--dex --no-strict --output=" + path + "classes.dex " + path + "gen/com.zip";
		logOut = logOut + "\nDx";
		ecj = "-nowarn -cp " + path + "android.jar -1.5 -d " + path + "gen " + path + "gen";
		swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnDx(dx);
		G.ide.fnLogOutput(swos);
		if(!swos.toString().contains(ok)){
			logOut = logOut + "\n" + swos.toString().replace(dx.replace(" ","￦n").replace("dx￦n",""),"");
			//return null;
		}
		return new Result("dx running",swos.toString(),getInt(swos.toString().contains(ok)));
		
	}
	
	private Result buildApk_5(){
		//publishProgress("Building APK...");
		aapt3 = "add -k " + path + "res.apk " + path + "classes.dex";
		logOut = logOut + "\nBuild APK";
		swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnAapt(aapt3);

		G.ide.fnLogOutput(swos);
		if(!swos.toString().contains(ok)){
			logOut = logOut + "\n" + swos.toString().replace(aapt3.replace(" ","￦n").replace("aapt￦n",""),"");
			//return null;
		}
		return new Result("building apk",swos.toString(),getInt(swos.toString().contains(ok)));
		
	}
	
	private Result buildApk_6(){
		//publishProgress("Signing...");
		ZipSigner = "-M auto-testkey -I " + path + "res.apk -O " + path + "final.apk";
		logOut = logOut + "\nSign";
		aapt1 = "package -m -J " + path + "gen/ -M " + path + "AndroidManifest.xml -S " + path + "res/ -I " + path + "android.jar";
		swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnSignApk(ZipSigner);
		return new Result("signing",swos.toString(),getInt(swos.toString().contains(ok)));
		
	}
	
	private void clearTrash(){
		deleteDirectory(new File(path+"gen"));
		File temp1 = new File(path+"res.apk");
		temp1.delete();
		temp1 = new File(path+"classes.dex");
		temp1.delete();
	}
	
	private void openApk(){
		Uri u = Uri.fromFile(new File(path+"final.apk"));
		Intent in = new Intent(Intent.ACTION_VIEW);
		in.setDataAndType(u, "application/vnd.android.package-archive");
		activity.startActivity(in);
		
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException { byte[] buffer = new byte[1024]; int read; while ((read = in.read(buffer)) != -1) { out.write(buffer, 0, read); } }
	private boolean deleteDirectory(File path) { if(!path.exists()) { return false; } File[] files = path.listFiles(); for (File file : files) { if (file.isDirectory()) { deleteDirectory(file); } else { file.delete(); } } return path.delete(); }
	private int getInt(boolean b){
		if(b){
			return 1;
		}else{
			return 0;
		}
	}
	///////////////////////////////////
	
	public static String runAapt(String commandLine){
		
		StringWriterOutputStream swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnAapt(commandLine);

		G.ide.fnLogOutput(swos);
		return swos.toString();
	}
	public static String runDx(String commandLine){
		StringWriterOutputStream swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnDx(commandLine);

		G.ide.fnLogOutput(swos);
		return swos.toString();
	}
	public static String runJavaCompiler(String commandLine){
		StringWriterOutputStream swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnDx(commandLine);

		G.ide.fnLogOutput(swos);
		return swos.toString();
	}
	public static String runZipSigner(String commandLine){
		StringWriterOutputStream swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		G.ide.fnSignApk(commandLine);

		G.ide.fnLogOutput(swos);
		return swos.toString();
	}
	
}
