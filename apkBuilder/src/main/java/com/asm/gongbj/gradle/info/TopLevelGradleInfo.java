package com.asm.gongbj.gradle.info;

import java.util.*;

public class TopLevelGradleInfo
{
	public allprojects allprojects;
	public TopLevelGradleInfo(){
		allprojects = new allprojects();
	}
	public class allprojects{
		public repositories repositories;
		public allprojects(){
			repositories = new repositories();
		}
		public class repositories{
			public List<String> mavenUrls;
			public repositories(){
				mavenUrls = new ArrayList<>();
			}
		}
	}
}
