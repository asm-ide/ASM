package com.asm.io;

import com.lhw.util.ExceptionParcelable;


interface IRemoteStream
{
	void close();
	int read(long position);
	long length();
	void setLength(long newLength);
	void write(byte data);
	void getState(out ExceptionParcelable e);
	boolean isLengthAvailable();
}
