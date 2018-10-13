package com.asm.gongbj.utils;

import android.content.*;
import android.net.*;
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
	
	
	public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
			.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        } 
        return TYPE_NOT_CONNECTED;
    }
}
