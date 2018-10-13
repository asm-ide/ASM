package com.asm.gongbj.gradle.sync;
import com.asm.gongbj.gradle.info.*;
import java.io.*;
import java.util.*;
public class SyncData
{

	private ArrayList<String> projectName;
	private ArrayList<GradleInfo> gradleInfo;
	private ArrayList<String> scaned;

//	for maven
	private TopLevelGradleInfo topLevelGradleInfo;

//	for aar
	private ArrayList<String> resources;
	private ArrayList<String> rPath;
	private ArrayList<String> manifestPath;
	private ArrayList<String> assets;
	private ArrayList<String> scannedAar;
//	
	private String main;

	public SyncData()
	{
		projectName = new ArrayList<String>();
		gradleInfo = new ArrayList<GradleInfo>();
		scaned = new ArrayList<String>();
		resources = new ArrayList<String>();
		rPath = new ArrayList<String>();
		manifestPath = new ArrayList<String>();
		assets = new ArrayList<String>();
		scannedAar = new ArrayList<String>();
	}
	public void setTopLevelGradleInfo(TopLevelGradleInfo tlgi)
	{
		topLevelGradleInfo = tlgi;
	}
	public TopLevelGradleInfo getTopLevelGradleInfo()
	{
		return topLevelGradleInfo;
	}
	public void addGradleInfo(String projectName, GradleInfo gradleInfo)
	{
		this.projectName.add(projectName);
		this.gradleInfo.add(gradleInfo);
	}
	public boolean isProjectScaned(String projectName)
	{
		return this.projectName.contains(projectName);
	}
	public boolean isProjectPathScaned(String projectPath)
	{
		String p[] = getSyncedProjectPath();
		for (String str : p)
		{
			if (str.equals(projectPath))return true;
		}
		return false;
	}
	public String[] getSyncedProjectPath()
	{
		String data[] = new String[gradleInfo.size()];
		int i=0;
		for (GradleInfo gi : gradleInfo)
		{
			data[i] = gi.fullPath;
			i++;
		}
		return data;
	}

	public String[] getSyncedProjectName()
	{
		String[] data  = new String[projectName.size()];
		for (int i= 0; i < projectName.size(); i++)
		{
			data[i] = projectName.get(i);
		}
		return data;
	}
	public GradleInfo[] getGradleInfo()
	{
		return gradleInfo.toArray(new GradleInfo[gradleInfo.size()]);
	}
	public GradleInfo getGradleInfo(String projectName)
	{
		if (!this.projectName.contains(projectName))return null;
		int index = this.projectName.indexOf(projectName);
		return this.gradleInfo.get(index);
	}
	public void addScanedJar(String path)
	{
		scaned.add(path);
	}
	public boolean isJarScaned(String path)
	{
		return scaned.contains(path);
	}
	public String[] getScanedJar()
	{
		String[] data  = new String[scaned.size()];
		for (int i= 0; i < scaned.size(); i++)
		{
			data[i] = scaned.get(i);
		}
		return data;
	}

	public String getMainProjectName()
	{
		return main;
	}
	public void setMainProjectName(String name)
	{
		main = name;
	}

	public String getMainMaifestPath()
	{
		return getGradleInfo(main).fullPath + "/build/bin/injected/AndroidManifest.xml";
	}

	public String[] getLibManifestPath()
	{
		List<String> list = new ArrayList<>();
		
//		for (String name : getSyncedProjectName())
//		{
//			if (!(name.equals(main)))
//			{
//				list.add(getGradleInfo(name).fullPath + "/build/bin/injected/AndroidManifest.xml");
//			}
//		}
		
		String mainP = getMainMaifestPath();
		for(String str : this.manifestPath){
			if(!str.equals(mainP)){
				list.add(str);
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public File[] getLibManifestFile()
	{
		List<File> list = new ArrayList<>();
/*
		for (String name : getSyncedProjectName())
		{
			if (!(name.equals(main)))
			{
				list.add(new File(getGradleInfo(name).fullPath + "/build/bin/injected/AndroidManifest.xml"));
			}
		}
*/
		String p[] = getLibManifestPath();
		for(String str : p){
			list.add(new File(str));
		}
		return list.toArray(new File[list.size()]);
	}

	public String getOutManifestPath()
	{
		return getGradleInfo(main).fullPath + "/build/bin/merged/AndroidManifest.xml";
	}
	private boolean equals(String path1, String path2)
	{
		String str1 = new File(path1).getAbsolutePath();
		String str2 = new File(path2).getAbsolutePath();

		return str1.equals(str2);
	}

//	for AAR 
//#############################################################
	public void addResource(String path)
	{
		if(!this.resources.contains(path)){

			this.resources.add(path);
		}
	}
	
	public List<String> getResources(){

		return this.resources;
	}
	
	
	public void addRPath(String path)
	{
		if(!this.rPath.contains(path)){

			this.rPath.add(path);
		}
	}

	public List<String> getRPaths(){

		return this.rPath;
	}
	
	
	public void addManifestPath(String path)
	{
		if(!this.manifestPath.contains(path)){

			this.manifestPath.add(path);
		}
	}

	public List<String> getManifestPaths(){

		return this.manifestPath;
	}
	
	
	public void addAsset(String path)
	{
		if(!this.assets.contains(path)){

			this.assets.add(path);
		}
	}

	public List<String> getAssets(){

		return this.assets;
	}
	
	
	public void addScannedAar(String path)
	{
		if(!this.scannedAar.contains(path)){

			this.scannedAar.add(path);
		}
	}

	public List<String> getScannedAars(){

		return this.scannedAar;
	}
//#############################################################
	
	public String[] getLibResourcePaths(){
		String projectP[] = getSyncedProjectPath();
		List<String> aars = getScannedAars();
		
		String result[] = new String[projectP.length + aars.size()];
		
		int i = 0;
		for(; i < projectP.length ; i++){
			result[i] = projectP[i] + "/src/main/";
		}
		
		for(String str : aars){
			result[i] = str;
			i++;
		}
		
		return result;
	}
}
