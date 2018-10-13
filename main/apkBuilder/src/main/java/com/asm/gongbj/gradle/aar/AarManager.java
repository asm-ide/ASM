package com.asm.gongbj.gradle.aar;
import android.util.*;
import com.asm.gongbj.gradle.*;
import com.asm.gongbj.gradle.sync.*;
import com.asm.gongbj.tools.*;
import java.io.*;
import net.lingala.zip4j.core.*;

public class AarManager
{

	public Syncer.ErrorListener errorL;
	public void setErrorListner(Syncer.ErrorListener errorL)
	{
		this.errorL = errorL;
	} 
	public void sync(String path, SyncData sd)
	{
		File zip = new File(path);
		File des = new File(zip.getParent() + "/" + zip.getName().replace(".aar", ""));
		if (!(des.exists()))
		{
			try
			{
				unzip(zip, des);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				//TODO : catch the error for errorListener
			}
		}
		
		//put infos to SyncData
		
		{
			//#1 : put R.java info
			sync_r(des,sd);
		}{
			//#2 : put Manifest info
			String manifestPath = des.getAbsolutePath() + "/AndriodManifest.xml";
			sd.addManifestPath(manifestPath);
		}{
			//#3 : put jar(classes.jar) info
			String jarPath = des.getAbsolutePath() + "/classes.jar";
			sd.addManifestPath(jarPath);
		}{
			//#4 : put /res info
			String resPath = des.getAbsolutePath() + "/res";
			if(new File(resPath).exists()){
				sd.addResource(resPath);
				sd.addScannedAar(des.getAbsolutePath());
			}
		}{
			//#5 : put public.xml info
			File publicT = new File(des.getAbsolutePath() + "/public.txt");
			File publicX = new File(des.getAbsolutePath() + "/res/values/public.xml");
			
			if(publicT.exists()){
				sync_public(publicT, publicX, sd);
			}
		}{
			//#6 : put jni info
			
			//ToDo : make this
		}{
			//#7 : put libs info
			File libDir = new File(des.getAbsolutePath()+"/libs");
			if(libDir.exists()){
				File[] inside = libDir.listFiles();
				for(File file : inside){
					String ex = getExtention(file);
					if(ex.equals("jar")){
						sd.addScanedJar(file.getAbsolutePath());
					}else if(ex.equals("aar")){
						sync(file.getAbsolutePath(),sd);
					}
				}
			}
			
		}{
			//#8 : put asset info
			String assetPath = des.getAbsolutePath() + "/assets";
			if(new File(assetPath).exists())sd.addAsset(assetPath);
		}{
			
		}{
			//#put aidl info
			
			//ToDo : make this
		}
		
		
		
	}

	private void sync_r(File des, SyncData sd){
		String manifestPath = des.getAbsolutePath() + "/AndroidManifest.xml";
		String rTxtPath = des.getAbsolutePath() + "/R.txt";
		
		ManifestManager mm = new ManifestManager();
		String packageName = "";
		try
		{
			packageName = mm.getPackageName(manifestPath);
		}
		catch (Exception e)
		{
			//ToDo : catch this error
			e.printStackTrace();
			return;
		}
		SymbolLoader sl = new SymbolLoader(new File(rTxtPath), null);
		try
		{
			sl.load();
		}
		catch (Exception e)
		{
			//ToDo : catch error
			e.printStackTrace();
			return;
		}

		String symbolWritePath  = des.getAbsolutePath() + "/java";
		
		Log.i("Aar Manager",symbolWritePath);
		if(!new File(symbolWritePath).exists()){
			new File(symbolWritePath).mkdirs();
		}
		SymbolWriter sw = new SymbolWriter(symbolWritePath, packageName, sl);
		sw.addSymbolsToWrite(sl);
		try
		{
			sw.write();
		}
		catch (Exception e)
		{
			//ToDo : catch this error
			e.printStackTrace();
			return;

		}

		sd.addRPath(symbolWritePath);
	}
	
	private void sync_public(File txt, File xml, SyncData sd){
		PublicLoader pl = new PublicLoader(txt);
		
		try
		{
			pl.load();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//ToDo : catch this error
			return;
		}
		
		PublicWriter pw = new PublicWriter(pl);
		
		try
		{
			pw.write(xml);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//ToDo : catch this error
		}
	}
	private static void unzip(File zipFile, File targetDirectory) throws Exception{
		ZipFile zip = new ZipFile(zipFile);
		zip.extractAll(targetDirectory.getAbsolutePath());
	}
	/*
	private static void unzip(File zipFile, File targetDirectory) throws IOException
	{
		ZipInputStream zis = new ZipInputStream(
            new BufferedInputStream(new FileInputStream(zipFile)));
		try
		{
			ZipEntry ze;
			int count;
			byte[] buffer = new byte[8192];
			while ((ze = zis.getNextEntry()) != null)
			{
				File file = new File(targetDirectory, ze.getName());
				File dir = ze.isDirectory() ? file : file.getParentFile();
				if (!dir.isDirectory() && !dir.mkdirs())
					throw new FileNotFoundException("Failed to ensure directory: " +
													dir.getAbsolutePath());
				if (ze.isDirectory())
					continue;
				FileOutputStream fout = new FileOutputStream(file);
				try
				{
					while ((count = zis.read(buffer)) != -1)
						fout.write(buffer, 0, count);
				}
				finally
				{
					fout.close();
				}
				/* if time should be restored as well
				 long time = ze.getTime();
				 if (time > 0)
				 file.setLastModified(time);
				 
			}
		}
		finally
		{
			zis.close();
		}
	}
	*/
	
	
	private String getExtention(File file){
		String name = file.getName();
		return name.substring(name.lastIndexOf(".") + 1).trim().toLowerCase();
	}

}
