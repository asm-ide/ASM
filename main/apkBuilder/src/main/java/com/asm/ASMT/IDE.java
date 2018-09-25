package com.asm.ASMT;

import android.content.res.*;
import android.os.*;
import android.util.*;
import com.asm.lib.io.*;
import java.io.*;
import java.security.*;

import java.lang.Process;
//##################################################################
/** This class provides access to the Java development tools.
 * 
 * @since 01.06.2011
 * @author Tom Arn
 * @author www.t-arn.com
 */
public class IDE
//##################################################################
{
  
//===================================================================
public IDE () 
//===================================================================
{
  
}
//===================================================================
public int fnAapt (String commandLine)
//===================================================================
{
  return a(commandLine);
} //
//===================================================================
protected int a(String strArr) {
    try{
        int i;
        int i2 = 0;
        long j = 0;
        String str = "aapt_pie";
        
        File file = new File(G.main.getDir("aapt",0),"aapt");
        if (!file.isFile()) {
            //a(str, file)
            //try{
            prepare(file);
     //}catch(Exception e){
     //G.fnToast(e.toString(),1000);
     //}
        }
        j = System.currentTimeMillis();
        str = file.getAbsolutePath();
        System.out.println("aapt arguments:");
        str = str + " " + strArr;
        System.out.println("");
        Process exec = Runtime.getRuntime().exec(str);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                break;
            }
            System.out.println(readLine);
        }
        bufferedReader = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                break;
            }
            System.err.println(readLine);
        }
        i = exec.waitFor();
        System.out.println("\nDone in " + ((System.currentTimeMillis() - j) / 1000) + " sec.\n");
        System.out.println("ExitValue: " + i);
        return i;
        }catch(Exception e){
        //G.fnToast(e.toString(),1000);
        return 0;
 }
    }
    private void copyAssets(String outS, String assets){
try{
File destFile = new File(outS);
//showMessage(outS);
  if (!destFile.getParentFile().exists()){
 // mkD(destFile.getParentFile());
   destFile.getParentFile().mkdirs();
  }
  if (!destFile.exists()) {
  destFile.createNewFile();
  }
 
  AssetManager assetMgr = G.main.getAssets();
InputStream in = assetMgr.open(assets);
OutputStream out = new FileOutputStream(destFile);
   copyFile(in,out);
   in.close();
   out.close();
   }catch(Exception e){
   //Toast.makeText(G.main,e.toString(),Toast.LENGTH_SHORT).show();
   //showMessage(e.toString());
   }
}
private void copyFile(InputStream in, OutputStream out) throws IOException { byte[] buffer = new byte[1024]; int read; while ((read = in.read(buffer)) != -1) { out.write(buffer, 0, read); } }
	
    private void prepare(File file){
    //Toast.makeText(G.main,"event_prepare",Toast.LENGTH_SHORT).show();
   
       String abi=Build.CPU_ABI;
       String typeA = "";
       //Toast.makeText(G.main,abi,Toast.LENGTH_SHORT).show();
   
       if(abi.contains("arm")){
          typeA="arm-a";
       }else if(abi.contains("mip")){
          typeA="mips-a";
       }else if(abi.contains("×86")){
          typeA="×86-a";
       }else{
          typeA="arm-a";
       }
       //Toast.makeText(G.main,file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
   
       copyAssets(file.getAbsolutePath(),typeA);
       if(file.setExecutable(true)){
       
       }else{
       System.out.println("cannot make aapt executable");
       //Toast.makeText(G.main,"make executable fail",Toast.LENGTH_SHORT).show();
       //G.fnToast("make Executable fail",1000);
       }
       
    }
    
  



//===================================================================
public int fnApkBuilder (String commandLine) 
//===================================================================
{
  return fnApkBuilder (fnTokenize(commandLine));
}
//===================================================================
public int rc;
public int fnApkBuilder (String[] args) 
//===================================================================
{
  long start=0;
  int i = 99;
  
  try
  {
    // show arguments
    start = System.currentTimeMillis();
    System.out.println("ApkBuilder arguments:");
    for (i=0;i<args.length;i++) System.out.println(args[i]);
    System.out.println("");
    // call ApkBuilder
	rc = com.android.sdklib.build.ApkBuilderMain.main(args);
	
	//System.setSecurityManager(null);
  }
  catch (Throwable t)
  {
    rc = 99;
    System.out.println("Error occurred!\n"+t.getMessage());
    t.printStackTrace();
  }
  System.out.println("\nDone in "+(System.currentTimeMillis()-start)/1000+" sec.\n");
  System.out.println("ExitValue: "+rc);
  return rc;
}//fnApkBuilder
//===================================================================
public int fnCompile (String commandLine) 
//===================================================================
{
  return fnCompile (fnTokenize(commandLine));
}
//===================================================================
public int fnCompile (String[] args) 
//===================================================================
{
  long start=0;
  int i, rc=99;
  boolean ok;
  org.eclipse.jdt.internal.compiler.batch.Main ecjMain;
	org.eclipse.jdt.internal.compiler.apt.dispatch.BatchProcessingEnvImpl a;
  try
  {
    start = System.currentTimeMillis();
    System.out.println("Compilation arguments:");
    for (i=0;i<args.length;i++) System.out.println(args[i]);
    System.out.println("");
    // call compiler
    // new Main(new PrintWriter(System.out), new PrintWriter(System.err), true/*systemExit*/, null/*options*/, null/*progress*/).compile(argv);
    ecjMain = new org.eclipse.jdt.internal.compiler.batch.Main(new PrintWriter(System.out), new PrintWriter(System.err), false/*noSystemExit*/, null/*options*/, null/*progress*/);
    ok = ecjMain.compile(args);
    if (ok) rc = 0;
    else rc = 3;
    if (ecjMain.globalWarningsCount>0) rc = 1;
    if (ecjMain.globalErrorsCount>0) rc = 2;
  }
  catch (Throwable t)
  {
    rc = 99;
    System.out.println("Error occurred!\n"+t.getMessage());
   Log.e("ASM","ECJError",t);
	t.printStackTrace();
  }
  System.out.println("\nDone in "+(System.currentTimeMillis()-start)/1000+" sec.\n");
  System.out.println("ExitValue: "+rc);
  return rc;
}//fnCompile
//===================================================================
public int fnDx (String commandLine) 
//===================================================================
{
  return fnDx (fnTokenize(commandLine));
}
//===================================================================
public int fnDx (String[] args)
//===================================================================
{
  long start=0;
  int i, rc=99;
  try
  {
    // show arguments
    start = System.currentTimeMillis();
    System.out.println("Dx arguments:");
    for (i=0;i<args.length;i++) System.out.println(args[i]);
    System.out.println("");
    // start dx
    /*rc = */com.android.dx.command.Main.main(args);
	rc = 1;
  }
  catch (Throwable t)
  {
    rc = 99;
    System.out.println("Error occurred!\n"+t.getMessage());
    t.printStackTrace();
  }
  System.out.println("\nDone in "+(System.currentTimeMillis()-start)/1000+" sec.\n");
  //System.out.println("ExitValue: "+rc);
  return rc;
} //fnDx
//===================================================================
public synchronized void fnLogOutput (StringWriterOutputStream swos)
//===================================================================
{
  boolean ok;
  FileWriter fw;
  String stOut;
  
  try
  {
    if (swos==null) return;
    ok = G.fnMakeLogDir(false);
    if (!ok)
    {
     // System.err.println(G.Rstring(R.string.err_mkdir)+" "+G.stWorkDir);
      return;
    }
    fw = new FileWriter (G.stWorkDir+"LogOutput.txt",false);
    stOut = swos.toString().replace("\n", "\r\n");
    fw.write(stOut);
    fw.close();
  }//try
  catch (Throwable t)
  {
    System.out.println("Error while saving output:\n"+t.getMessage());
  }//catch
} // fnLogOutput
//===================================================================
public void fnRedirectOutput (StringWriterOutputStream swos) 
//===================================================================
{
  // redirect stdout and stderr
  System.setOut(new PrintStream(swos));
  System.setErr(new PrintStream(swos));
}
//===================================================================
public void fnRunBeanshellScript (bsh.Interpreter i, String script) 
//===================================================================
{
  long start=0;
  try
  {
    start = System.currentTimeMillis();
    System.out.println("Running script "+script+"\n");
    if (new File(script).exists()) i.source(script);
    else System.out.println("Script does not exist.");
  }
  catch (Throwable t)
  {
    System.out.println("Error occurred!\n"+t.getMessage());
    t.printStackTrace();
  }
  finally
  {
    System.out.println("\nTotal script run time: "+(System.currentTimeMillis()-start)/1000+" sec.\n");
  }
}// fnRunBeanshellScript
//===================================================================
public int fnSignApk (String commandLine)
//===================================================================
{
  return fnSignApk(fnTokenize(commandLine));
} //
//===================================================================
public int fnSignApk (String[] args)
//===================================================================
{
  long start=0;
  int i, rc = 99;
  try
  {
    // show arguments
    start = System.currentTimeMillis();
    System.out.println("SignApk arguments:");
    for (i=0;i<args.length;i++) System.out.println(args[i]);
    System.out.println("");
    // start SignApk
    rc = SignApk.main(args);
  }
  catch (Throwable t)
  {
    rc = 99;
    System.out.println("Error occurred!\n"+t.getMessage());
    t.printStackTrace();
  }
  System.out.println("\nDone in "+(System.currentTimeMillis()-start)/1000+" sec.\n");
  return rc;
} //fnSignApk
//===================================================================
public static String[] fnTokenize (String commandLine) 
//===================================================================
{
  return org.eclipse.jdt.internal.compiler.batch.Main.tokenize(commandLine);
}
//===================================================================

} // IDE
//##################################################################
