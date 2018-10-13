package com.asm.gongbj.gradle;
import android.app.*;
import com.asm.ASMT.*;
import com.asm.gongbj.gradle.info.*;
import com.asm.gongbj.gradle.repository.*;
import com.asm.gongbj.gradle.sync.*;
import com.asm.gongbj.tools.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
/**
 @author GongBJ
 */
public class Syncer
{
	private Activity ac;
	private ProgressListener progressListener;
	private ErrorListener errorListener;
	public Syncer(Activity activity){
		ac = activity;
	}
	public void setProgressListener(ProgressListener pro){
		progressListener = pro;
	}
	public void setErrorListener(ErrorListener err){
		errorListener = err;
	}
	/**
	 *this method sync the hole android project.
	 *It analyzes build.gradle, and prepare for some operations(like real-time error analyze)
	 *It generates R file from res and Inject Manifest, and Merge Manifests
	 *When there are error while reading res and gradle files, it calls 'onError' in ErrorListener Interface.
	 *@author GongBJ
	 *@param String androidProjectPath
	 *@param String mainGradlePath : the folder path of gradle project that you are going to build
	 */
	private String androidProjectPath, mainGradlePath;
	public SyncData sync(String androidProjectPath, String mainGradlePath){
		G.fnInit(ac);
		this.androidProjectPath = androidProjectPath;
		this.mainGradlePath = mainGradlePath;
		File androidProject = new File(androidProjectPath);
		if(!androidProject.exists()){
			errorListener.onError(new ProgressFail("Android Project is not found",androidProjectPath,"sync"));
		}
		File mainGradle = new File(mainGradlePath);
		if(!mainGradle.exists()){
			errorListener.onError(new ProgressFail("Gradle Project is not found",mainGradlePath,"sync"));
		}
		syncData = new SyncData();
		progressListener.onProgressStart();
		
		try{
			syncData.setTopLevelGradleInfo(new ProjectManager(ac).getToplevelGradleInfo(androidProjectPath));
		}catch(ProgressFail pf){
			errorListener.onError(pf);
			return null;
		}
		try{
			syncData.setMainProjectName(mainGradlePath.substring(mainGradlePath.lastIndexOf("/")+1,mainGradlePath.length()));
			scanGradleProject(mainGradlePath);
		}catch (ProgressFail e){
			errorListener.onError(e);
			return null;
		}
		//Merge Manifest
		ManifestManager mm = new ManifestManager();
		AnalysisData an = mm.merge(
								new File(syncData.getMainMaifestPath()),
								new File(syncData.getOutManifestPath()),
								syncData.getLibManifestFile()
								);
		
		if(an.exitValue != 0){
			ProgressFail e = new ProgressFail("Fail to Merge Manifest",syncData.getOutManifestPath(),"Gradle");
			e.setAnalysisData(an);
		}
		
		progressListener.onprogressFinish();
		return syncData;
		
		
	}

	private void scanGradleProject(String path)throws ProgressFail{
		String name = path.substring(path.lastIndexOf("/")+1,path.length());

		progressListener.onProgressChange("Gradle Running : " + name);
		GradleInfo gi = (new ProjectManager(ac)).getGradleProjectInfo(path);
		syncData.addGradleInfo(name,gi);
		//Sync jar info
		
		for(CompileInfo ci : gi.dependencies.compile){
			if(ci.type == CompileInfo.TYPE_FILETREE){
				syncFileTree(ci,path);
			}else if(ci.type == CompileInfo.TYPE_LIB){
				String pathh = ci.value1;
				String namee = ci.value2;
				String fullPath = androidProjectPath + "/" + pathh;
				if(new File(fullPath).exists()){
					syncData.addScanedJar(fullPath);
				}else{
					errorListener.onError(new ProgressFail("file not found in : " + pathh,fullPath,"sync"));
				}
				
			}else if(ci.type == CompileInfo.TYPE_MAVEN){
				//Toast.makeText(ac,"Maven Project id not supported now.",Toast.LENGTH_SHORT);
				//Temp disable Maven
				MavenScaner mavenScaner = new MavenScaner();
				mavenScaner.setErrorListener(errorListener);
				mavenScaner.downloadMaven(syncData,ci.value1,ac);
			}else if(ci.type == CompileInfo.TYPE_PROJECT){
				String pathh = ci.value1;
				String fullPath = androidProjectPath + "/" + pathh;
				try
				{
					if(!syncData.isProjectPathScaned(fullPath)){
						scanGradleProject(fullPath);
					}
				}
				catch (ProgressFail e)
				{
					errorListener.onError(new ProgressFail(e.toString(),fullPath,"sync"));
				}
			}
		}
		
		//inject manifest
		inject(path,gi);
		
		//############
		//sync assets
		{
			File list[] = new File(path + "/src/main/").listFiles();
			for(File f : list){
				String fName = f.getName();
				if(f.isFile())continue;
				if(fName.equals("res")||fName.equals("aidl")||fName.equals("jni")){
					syncData.addAsset(f.getAbsolutePath());					
				}
			}
		}
		//############
		
		
		//Sync R
		File build = new File(path+"/build");
		if(!build.exists())build.mkdirs();
		if(build.isFile())build.delete(); build.mkdirs();
		File gen = new File(build.getAbsolutePath()+"/gen");
		if(!gen.exists())gen.mkdirs();
		if(gen.isFile())gen.delete(); gen.mkdirs();
		Aapt aapt = new Aapt(Aapt.getAndroidJarPath());
		
		String manifest = path + "/src/main/AndroidManifest.xml";
		String res = path + "/src/main/res";
		
		String libResources[] = syncData.getLibResourcePaths();
		String result = aapt.generateR(gen.getAbsolutePath(),manifest,new String[]{res},libResources,null);
		AnalysisData ad = AaptResultAnalyze.analysis(result);
		if(!(ad.exitValue==0)){
			ProgressFail f = new ProgressFail("cannot generate R.java",null,"sync");
			f.setAnalysisData(ad);
			errorListener.onError(f);
			//return;
			//Toast.makeText(ac,ad.toString(),Toast.LENGTH_LONG).show();
		}
		
		//##########################
		syncData.addResource(res);
		syncData.addRPath(gen.getAbsolutePath());
		syncData.addManifestPath(manifest);
		//###########################
		
		
	}
	
	private void inject(String path, GradleInfo gi){
		String targetXml = path + "/src/main/AndroidManifest.xml";
		String desXml = path + "/build/bin/injected/AndroidManifest.xml";
		File des = new File(desXml);
		if(!(des.getParentFile().exists())){
			des.getParentFile().mkdirs();
		}
		try{
			new ManifestManager().inject(gi,targetXml,desXml);
		}catch(Exception e){
			e.toString();
		}
	}
	
	private String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	private void syncFileTree(CompileInfo ci,String mpath){
		String path = ci.value1;
		String name = ci.value2;
		String fullPath = mpath + "/" + path;
		//Todo : make aar available
		if(!"*.jar".equals(name)) errorListener.onError(new ProgressFail("Only \".jar\" is available",fullPath,"sync"));
		File f = new File(fullPath);
		if(!(f.exists())){
			//errorListener.onError(new ProgressFail("folder path is not exist in "+ path,fullPath,"sync"));
			//If, File Tree Path is Not exists : not error, but return.
			return;
		}
		File list[] = f.listFiles();
		for(File file : list){
			if(file.getName().toLowerCase().contains(".jar")){
				syncData.addScanedJar(file.getAbsolutePath());
			}
		}
	}
	
	
	//util######
	private String[] without(List<String> list, String str){
		int size = list.size();
		if(list.contains(str)){
			size --;
		}else{
			return list.toArray(new String[size]);
		}
		
		String result[] = new String[size];
		int i = 0;
		for(String text : list){
			if(!text.equals(str)){
				result[i] = text;
				i++;
			}
		}
		
		return result;
	}
	
	//##########
	
	
	private SyncData syncData;
	public static interface ProgressListener{

		public void onProgressStart();
		public void onProgressChange(String progressName);
		public void onprogressFinish();
	}
	public static interface ErrorListener{
		public boolean onError(ProgressFail progressFail);
	}
}
