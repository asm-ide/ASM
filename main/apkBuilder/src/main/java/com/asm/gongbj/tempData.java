package com.asm.gongbj;
import javax.xml.transform.sax.*;
import java.util.*;

public class tempData
{
	
	private ArrayList<String> filePath = new ArrayList<>();
	private ArrayList<String> data = new ArrayList<>();
	
	public tempData(){
		
	}
	public void addData(String filePath,String data){
		this.filePath.add(filePath);
		this.data.add(data);
	}
	public String getData(String filePath){
		int count =0;
		for(String s : this.filePath){
			if(s.equals(filePath)){
				return this.data.get(count);
			}
			count++;
		}
		return null;
	}
	
	public void removeData(String filePath){
		int count =0;
		for(String s : this.filePath){
			if(s.equals(filePath)){
				this.filePath.remove(count);
				this.data.remove(count);
				return;
			}
			count++;
		}
		
	}
	
	public ArrayList<String> getFileList(){
		return filePath;
	}
	
}
