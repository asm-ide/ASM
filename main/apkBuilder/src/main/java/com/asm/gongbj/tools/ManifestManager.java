package com.asm.gongbj.tools;

import com.android.annotations.*;
import com.android.manifmergers.*;
import com.android.utils.*;
import com.asm.gongbj.gradle.info.*;
import com.asm.gongbj.utils.*;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class ManifestManager{
	
	
	AnalysisData ad;
	public ManifestManager(){

	}
	
	public AnalysisData merge(File mainXml, File outPut, File[] libs){
		OutputListener.start();
		
		ad = new AnalysisData();
		if(!outPut.getParentFile().exists()){
			outPut.getParentFile().mkdirs();
		}
		if(!outPut.exists()){
			try
			{
				outPut.createNewFile();
			}
			catch (IOException e)
			{
				
			}
		}
		boolean result = false;
		try{
			com.android.manifmergers.ManifestMerger mm = new com.android.manifmergers.ManifestMerger(wrapSdkLog(new StdLogger(StdLogger.Level.VERBOSE)), null);
			result = mm.process(outPut, mainXml, libs, null, null);
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}
		
		String output = OutputListener.stop();
		
		
		ad.exitValue = result ? 0 : 1;
		
		ad.fullLog = output;
		
		ad.time = -1;
		
		return ad;
	}

	public void inject(GradleInfo gradleInfo, String xml, String out)throws Exception{
		
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(xml));
			
			
			Node node = document.getElementsByTagName("manifest").item(0);
			if(gradleInfo.android.defaultConfig.versionCode != null){
					((Element)node).setAttribute("android:versionCode", gradleInfo.android.defaultConfig.minSdkVersion);
			}
			if(gradleInfo.android.defaultConfig.versionName != null){
					((Element)node).setAttribute("android:versionName", gradleInfo.android.defaultConfig.targetSdkVersion);
			}
			
			
			Node usesSdk = ((Element)node).getElementsByTagName("uses-sdk").item(0);
			if(usesSdk == null){
				Element e = document.createElement("uses-sdk");
				node.appendChild(e);
				usesSdk = e;
			}
			
			if(gradleInfo.android.defaultConfig.minSdkVersion != null){
				((Element)usesSdk).setAttribute("android:minSdkVersion", gradleInfo.android.defaultConfig.minSdkVersion);	
			}
			if(gradleInfo.android.defaultConfig.targetSdkVersion != null){
				((Element)usesSdk).setAttribute("android:targetSdkVersion", gradleInfo.android.defaultConfig.minSdkVersion);	
			}
			
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(out));
			transformer.transform(source, result);
			
			
	}
	
	
	private boolean hasAttribute(Node node, String name){
		return hasAttribute(node.getAttributes(), name);
	}
	
	private boolean hasAttribute(NamedNodeMap node, String name){
		for(int i = 0; i < node.getLength(); i++){
			Node n = node.item(i);
			if(n.getNodeName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	
	
	private IMergerLog wrapSdkLog(@NonNull final ILogger sdkLog) {
        return new IMergerLog() {
            @Override
            public void error(
				@NonNull Severity severity,
				@NonNull FileAndLine location,
				@NonNull String message,
				Object...msgParams) {
				AnalysisData.ErrData ed = new AnalysisData.ErrData();
				
                switch(severity) {
					case INFO:
						sdkLog.info(
                            "[%1$s] %2$s",                                  //$NON-NLS-1$
                            location,
                            String.format(message, msgParams));
						ed.type = AnalysisData.ErrData.INFO;
						ed.comment = String.format(message,msgParams);
						ed.filePath = location.toString();
						ad.errData.add(ed);
						//System.out.println("INFO : \nfile : "+location.getFileName()+"\nMessage : " + String.format(message, msgParams) );
						break;
					case WARNING:
						sdkLog.warning(
                            "[%1$s] %2$s",                                  //$NON-NLS-1$
                            location,
                            String.format(message, msgParams));
						ed.type = AnalysisData.ErrData.WARNING;
						ed.comment = String.format(message,msgParams);
						ed.filePath = location.toString();
						ad.errData.add(ed);
						//System.out.println("WARNING : \nfile : "+location.getFileName()+"\nMessage : " + String.format(message, msgParams) );
						break;
					case ERROR:
						sdkLog.error(null /*throwable*/,
									 "[%1$s] %2$s",                                  //$NON-NLS-1$
									 location,
									 String.format(message, msgParams));
						ed.type = AnalysisData.ErrData.ERROR;
						ed.comment = String.format(message,msgParams);
						ed.filePath = location.toString();
						ad.errData.add(ed);
						//System.out.println("ERROR : \nfile : "+location.getFileName()+"\nMessage : " + String.format(message, msgParams) );
						break;
                }
            }

            @Override
            public void conflict(@NonNull Severity severity,
								 @NonNull FileAndLine location1,
								 @NonNull FileAndLine location2,
								 @NonNull String message,
								 Object...msgParams) {
									 
				AnalysisData.ErrData ed = new AnalysisData.ErrData();
                switch(severity) {
					case INFO:
						sdkLog.info(
                            "[%1$s, %2$s] %3$s",                          //$NON-NLS-1$
                            location1,
                            location2,
                            String.format(message, msgParams));
						ed.type = AnalysisData.ErrData.INFO;
						ed.comment = String.format(message,msgParams);
						ed.filePath = location1.toString();
						ed.filePath2 = location2.toString();
						ad.errData.add(ed);
						//System.out.println("CONFLICT_INFO : \nfile1 : "+location1.getFileName()+"\nfile2 : "+location2.getFileName()+"\nMessage : " + String.format(message, msgParams) );
						break;
					case WARNING:
						sdkLog.warning(
                            "[%1$s, %2$s] %3$s",                          //$NON-NLS-1$
                            location1,
                            location2,
                            String.format(message, msgParams));
						ed.type = AnalysisData.ErrData.WARNING;
						ed.comment = String.format(message,msgParams);
						ed.filePath = location1.toString();
						ed.filePath2 = location2.toString();
						ad.errData.add(ed);
						//System.out.println("CONFLICT_WARNING : \nfile1 : "+location1.getFileName()+"\nfile2 : "+location2.getFileName()+"\nMessage : " + String.format(message, msgParams) );
						break;
					case ERROR:
						sdkLog.error(null /*throwable*/,
									 "[%1$s, %2$s] %3$s",                          //$NON-NLS-1$
									 location1,
									 location2,
									 String.format(message, msgParams));
						ed.type = AnalysisData.ErrData.ERROR;
						ed.comment = String.format(message,msgParams);
						ed.filePath = location1.toString();
						ed.filePath2 = location2.toString();
						ad.errData.add(ed);
						//System.out.println("CONFLICT_ERROR : \nfile1 : "+location1.getFileName()+"\nfile2 : "+location2.getFileName()+"\nMessage : " + String.format(message, msgParams) );
						break;
                }
            }
        };
    }
	
	
}
