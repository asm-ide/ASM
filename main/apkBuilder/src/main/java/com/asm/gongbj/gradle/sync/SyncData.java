package com.asm.gongbj.gradle.sync;
import com.asm.gongbj.gradle.info.*;
import java.io.*;
import java.util.*;
public class SyncData
{
	
	private ArrayList<String> projectName;
	private ArrayList<GradleInfo> gradleInfo;
	private ArrayList<String> scaned;
	private TopLevelGradleInfo topLevelGradleInfo;
	private String main;
	
	public SyncData(){
		projectName = new ArrayList<String>();
		gradleInfo = new ArrayList<GradleInfo>();
		scaned = new ArrayList<String>();
	}
	public void setTopLevelGradleInfo(TopLevelGradleInfo tlgi){
		topLevelGradleInfo = tlgi;
	}
	public TopLevelGradleInfo getTopLevelGradleInfo(){
		return topLevelGradleInfo;
	}
	public void addGradleInfo(String projectName,GradleInfo gradleInfo){
		this.projectName.add(projectName);
		this.gradleInfo.add(gradleInfo);
	}
	public boolean isProjectScaned(String projectName){
		return this.projectName.contains(projectName);
	}
	public String[] getSyncedProjectPath(){
		String data[] = new String[gradleInfo.size()];
		int i=0;
		for(GradleInfo gi : gradleInfo){
			data[i] = gi.fullPath;
			i++;
		}
		return data;
	}
	
	public String[] getSyncedProjectName(){
		String[] data  = new String[projectName.size()];
		for(int i= 0; i<projectName.size(); i++){
			data[i] = projectName.get(i);
		}
		return data;
	}
	public GradleInfo[] getGradleInfo(){
		return gradleInfo.toArray(new GradleInfo[gradleInfo.size()]);
	}
	public GradleInfo getGradleInfo(String projectName){
		if(!this.projectName.contains(projectName))return null;
		int index = this.projectName.indexOf(projectName);
		return this.gradleInfo.get(index);
	}
	public void addScanedJar(String path){
		scaned.add(path);
	}
	public boolean isJarScaned(String path){
		return scaned.contains(path);
	}
	public String[] getScanedJar(){
		String[] data  = new String[scaned.size()];
		for(int i= 0; i<scaned.size(); i++){
			data[i] = scaned.get(i);
		}
		return data;
	}
	
	public String getMainProjectName(){
		return main;
	}
	public void setMainProjectName(String name){
		main = name;
	}
	
	public String getMainMaifestPath(){
		return getGradleInfo(main).fullPath + "/build/bin/injected/AndroidManifest.xml";
	}
	
	public String[] getLibManifestPath(){
		List<String> list = new ArrayList<>();
		
		for(String name : getSyncedProjectName()){
			if(!(name.equals(main))){
				list.add(getGradleInfo(name).fullPath + "/build/bin/injected/AndroidManifest.xml");
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	public File[] getLibManifestFile(){
		List<File> list = new ArrayList<>();

		for(String name : getSyncedProjectName()){
			if(!(name.equals(main))){
				list.add(new File(getGradleInfo(name).fullPath + "/build/bin/injected/AndroidManifest.xml"));
			}
		}
		return list.toArray(new File[list.size()]);
	}
	
	public String getOutManifestPath(){
		return getGradleInfo(main).fullPath + "/build/bin/merged/AndroidManifest.xml";
	}
	private boolean equals(String path1, String path2){
		String str1 = new File(path1).getAbsolutePath();
		String str2 = new File(path2).getAbsolutePath();
		
		return str1.equals(str2);
	}
	
	
}
