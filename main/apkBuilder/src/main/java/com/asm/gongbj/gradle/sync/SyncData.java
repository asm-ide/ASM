package com.asm.gongbj.gradle.sync;
import com.asm.gongbj.gradle.*;
import java.util.*;
import com.asm.gongbj.gradle.info.*;
public class SyncData
{
	
	private ArrayList<String> projectName;
	private ArrayList<GradleInfo> gradleInfo;
	private ArrayList<String> scaned;
	public SyncData(){
		projectName = new ArrayList<String>();
		gradleInfo = new ArrayList<GradleInfo>();
		scaned = new ArrayList<String>();
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
}
