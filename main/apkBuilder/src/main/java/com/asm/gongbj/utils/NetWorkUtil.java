package com.asm.gongbj.utils;

import android.os.*;
import android.util.*;
import java.io.*;
import java.net.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

public class NetWorkUtil
{
	public static void enableNetWorkAtMainThread(){
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	public static InputStream getInputStreamFromUrl(String url) {
		InputStream content = null; 
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(url));
			content = response.getEntity().getContent();
		} catch (Exception e) {
			Log.e("[GET REQUEST]", "Network exception", e);
		}
		return content;
	}
	public static String getStringFromUrl(String url){
		String fullString = "";
		
		try
		{
			URL u = new URL(url);
			BufferedReader reader = new BufferedReader(new InputStreamReader(u.openStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				fullString += line;
			}
			reader.close();
		}
		catch (Exception e)
		{
			return e.toString();
		}
		return fullString;
	}
	private static String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try
        {
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally{

            try
            {
                is.close();

            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }
}
