package com.asm.util;

import com.asm.annotation.Nullable;
import com.asm.loader.Loader;
import com.asm.loader.Loadable;
import com.asm.loader.LoadConfig;

import com.lhw.util.TextUtils;

import java.io.Writer;
import java.io.PrintWriter;
import java.util.Map;




public abstract class Log
{
	/** 
	 * Priority constant for the println method; use Log.v. 
	 */
	public static final int VERBOSE = 2;
	
	/** 
	 * Priority constant for the println method; use Log.d. 
	 */
	public static final int DEBUG = 3; 
	
	/** 
	 * Priority constant for the println method; use Log.i. 
	 */
	public static final int INFO = 4; 
	
	/**
	 * Priority constant for the println method; use Log.w. 
	 */
	public static final int WARN = 5;
	
	/**
	 * Priority constant for the println method; use Log.e.
	 */
	public static final int ERROR = 6; 
	
	/**
	 * Priority constant for the println method.
	 */ 
	public static final int ASSERT = 7;
	
	
	
	private static Loader<Log> sLog;
	
	
	
	public PrintWriter getPrinter() {
		return new PrintWriter(new LogWriter());
	}
	
	
	public void d(String msg) {
		log(DEBUG, msg, null);
	}
	
	public void d(String msg, Throwable th) {
		log(DEBUG, msg, th);
	}
	
	public void v(String msg) {
		log(VERBOSE, msg, null);
	}
	
	public void v(String msg, Throwable th) {
		log(VERBOSE, msg, th);
	}
	
	public void i(String msg) {
		log(INFO, msg, null);
	}
	
	public void i(String msg, Throwable th) {
		log(INFO, msg, th);
	}
	
	public void w(String msg) {
		log(WARN, msg, null);
	}
	
	public void w(String msg, Throwable th) {
		log(WARN, msg, th);
	}
	
	public void e(String msg) {
		log(ERROR, msg, null);
	}
	
	public void e(String msg, Throwable th) {
		log(ERROR, msg, th);
	}
	
	
	public abstract void log(int level, String msg, @Nullable Throwable ex);
	
	
	public static Log get(String tag) {
		if(sLog == null)
			throw new IllegalStateException("logger not attached");

		return sLog.load(LoadConfig.mapFromList(tag));
	}
	
	
	public static void attachLog(Loader<Log> log) {
		sLog = log;
	}
	
	
	private class LogWriter extends Writer
	{
		private StringParser mParser = StringParser.small();
		
		
		@Override
		public void write(char[] buf, int offset, int len) {
			StringParser.Text text;
			
			while(!(text = mParser.append(TextUtils.asSequence(buf, offset, len)).peekUntil("\n")).isEmpty())
				d(text.str());
		}
		
		@Override
		public void flush() {
			
		}
		
		@Override
		public void close() {
			
		}
	}
	
	
	private static class AndroidLog extends Log implements Loadable
	{
		private String mTag;
		
		
		
		@Override
		public void log(int level, String msg, Throwable th) {
			// these levels are as same as android log level constants
			android.util.Log.println(level, mTag, msg);
			if(th != null)
				android.util.Log.println(level, mTag, android.util.Log.getStackTraceString(th));
		}
		
		
		// @Override
		protected static Loader<AndroidLog> getLoader() {
			return new Loader<AndroidLog>() {
				public AndroidLog load(Map<String, Object> args) {
					Object[] list = LoadConfig.listFromMap(args);
					AndroidLog log = new AndroidLog();
					log.mTag = (String) list[0];
					return log;
				}
			};
		}
	}
}
