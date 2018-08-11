package com.asm.gongbj.codeManage;

import java.net.*;
import java.lang.reflect.*;
import java.io.*;
import java.lang.annotation.*;
import android.content.*;

public class CodeFromClass
{
	public static final int A = 0;
	
	public interface i{
		
	}
	public static String get(String path,Context con)throws Exception{
		Class clazz = CodeFromClass.class;//getClassFromFile(path);
		return get(clazz,con);
		
	}
	public static String get(String path,String className,Context con)throws Exception{
		
		//return get(getClassFromFile(path,className),con);
		return get(CodeFromClass.class,con);
	}
	
	public static String get(Class clazz, Context con){
		
		if(clazz==null)return "";
		String str = "";
		
		if(clazz.getName().contains("$")){
			str += Modifier.toString(clazz.getModifiers()) + isClass(clazz) + clazz.getName().substring(clazz.getName().lastIndexOf("$")+1) +extendToString(clazz) +implementsToString(clazz) + "{\n";
		}else{
			str += Modifier.toString(clazz.getModifiers()) + isClass(clazz) + clazz.getName() +extendToString(clazz)+implementsToString(clazz)+"{\n";
		}
		
		Field[] fields1 = clazz.getFields(); // returns inherited members but not private members.
		Field[] fields2 = clazz.getDeclaredFields(); // returns all members including private members but not inherited members.
		for(Field f : fields2){
			str += "\n";
			Annotation anno[] = f.getDeclaredAnnotations();
			for(Annotation a : anno){
				str += "@" + a.annotationType().getName() + "\n";
			}
			str += Modifier.toString(f.getModifiers()) + " " + f.getType().getName() +" " + f.getName();
			Object value = null;
			try {
				f.setAccessible(true);
				value = f.get(con);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if(value != null){
				str += " = " + String.valueOf(value);
			}
			str+=";";
		}
		
		str += "\n";

		Method[] md = clazz.getDeclaredMethods();
		for(Method method : md){
			str += "\n" + methodToString(method);
		}
		

		Class dc[] = clazz.getDeclaredClasses();
		for(Class c : dc){
			str += "\n" + get(c,con);
		}
		
		str += "\n}";
		return CleanJavaCode.clean(str,"        ");
	}
	private static String isClass(Class c){
		if(Modifier.toString(c.getModifiers()).contains("interface")){
			return " ";
		}else{
			return " class ";
		}
	}
	private static String implementsToString(Class c){
		String str = "";
		Class cl[] = c.getInterfaces();
		if(cl.length==0){
			return "";
		}else{
			str+= " implements ";
			for(Class i:cl){
				str += i.getName() + ",";
			}
			str = str.substring(0,str.length()-1);
			return str;
		}
	}
	private static String extendToString(Class c){
		Class cl = c.getSuperclass();
		if(cl!=null){
			if(!cl.getName().toString().equals("java.lang.Object")){
				return " extends " + cl.getName();
			}else{
				return "";
			}
		}else{
			return "";
		}
	}
	private static String methodToString(Method md){
		String str = "";
		Annotation anno[] = md.getDeclaredAnnotations();
		for(Annotation a : anno){
			str += "@" + a.annotationType().getName() + "\n";
		}
		str += Modifier.toString( md.getModifiers());
		str += " " + md.getReturnType().getName() + " " + md.getName() + "(";
		Class p[] = md.getParameterTypes();
		
		for(int i = 0; i<p.length; i++){
			str+=p[i].getName() + " ";
			str+="p"+String.valueOf(i);
			if(i!=p.length-1)str += ",";
		}
		Class e[] = md.getExceptionTypes();
		
		if(e.length>0){
			String ex = "";
			for(Class excep : e){
				ex += excep.getName() + ",";
			}
			ex = ex.substring(0,ex.length()-1);
			str+=") throws "+ex+"{ }" + "\n";
		}else{
			str+="){ }" + "\n";
		}
		
		return str;
	}
	private static Class getClassFromFile(String path,String fullClassName) throws Exception {
		URLClassLoader classLoader = new URLClassLoader( new URL[]{new File(path).toURL()}); // TODO: File#toURL() has been deprecated
		return classLoader.loadClass(fullClassName);
		
	}
	public class aa{
		
	}
	
}
