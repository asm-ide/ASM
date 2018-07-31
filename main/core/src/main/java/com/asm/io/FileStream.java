package com.asm.io;

import java.io.RandomAccessFile;
import java.io.IOException;


public class FileStream extends Stream
{
	private RandomAccessFile mFile;
	
	
	public FileStream(String name, int mode) throws IOException {
		super(name, mode);
		
		mFile = new RandomAccessFile(name, getStringMode());
	}
	
	
	@Override
	protected void onClose() throws IOException {
		mFile.close();
	}
	
	@Override
	protected int onRead(long position) throws IOException {
		mFile.seek(position);
		return mFile.read();
	}
	
	@Override
	protected long onLength() throws IOException {
		return mFile.length();
	}
	
	@Override
	protected void onSetLength(long len) throws IOException {
		mFile.setLength(len);
	}
	
	@Override
	public boolean isLengthAvailable() {
		return true;
	}
	
	@Override
	protected void onWrite(byte data) throws IOException {
		mFile.write(data);
	}
	
	private String getStringMode() {
		StringBuilder sb = new StringBuilder();
		if(isReadable()) sb.append("r");
		if(isWritable()) sb.append("w");
		if(isSync()) sb.append("s");
		return sb.toString();
	}
}
