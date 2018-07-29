package com.asm.io;

import com.lhw.util.ExceptionParcelable;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import java.io.IOException;


public class RemoteStream extends Stream implements Parcelable
{
	public static final Creator<RemoteStream> CREATOR = new Creator<RemoteStream>() {
		@Override
		public RemoteStream createFromParcel(Parcel p) {
			try {
				return new RemoteStream(p);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public RemoteStream[] newArray(int len) {
			return new RemoteStream[len];
		}
	};
	
	private class MyRemoteStream extends IRemoteStream.Stub
	{
		private IOException mState;
		
		
		@Override
		public void close() throws RemoteException {
			try {
				mStream.close();
			} catch(IOException e) {
				onException(e);
			}
		}
		
		@Override
		public int read(long position) throws RemoteException {
			try {
				mStream.seek(position);
				return mStream.read();
			} catch(IOException e) {
				onException(e);
			}
			return -2;
		}
		
		@Override
		public boolean isLengthAvailable() {
			return mStream.isLengthAvailable();
		}
		
		@Override
		public long length() throws RemoteException {
			try {
				return mStream.length();
			} catch(IOException e) {
				onException(e);
				return -1;
			}
		}
		
		@Override
		public void setLength(long newLength) throws RemoteException {
			try {
				mStream.setLength(newLength);
			} catch(IOException e) {
				onException(e);
			}
		}
		
		@Override
		public void write(byte data) throws RemoteException {
			try {
				mStream.write(data);
			} catch(IOException e) {
				onException(e);
			} // TODO : write array
		}
		
		@Override
		public void getState(ExceptionParcelable e) throws RemoteException {
			if(mState == null) {
				e.set(null);
			} else {
				e.set(mState);
			}
		}
		
		private void onException(IOException e) {
			mState = e;
		}
	}
	
	
	private boolean mLocal; // true to get command
	private IRemoteStream mBinder;
	private Stream mStream;
	private boolean mIsThrowRemoteException = true;
	
	// unlocal cache
	private long mLength = -1; // when write mode was not set
	private boolean mLengthAvailable;
	private boolean mLengthAvailableInited = false;
	
	
	public RemoteStream(Stream stream) throws IOException {
		super(stream.getName(), stream.getMode());
		mLocal = true;
		mStream = stream;
	}
	
	protected RemoteStream(Parcel p) throws IOException {
		super("RemoteStream", p.readInt());
	}
	
	public void setThrowRemoteException(boolean on) {
		mIsThrowRemoteException = on;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel p, int len) {
		if(mBinder == null) {
			mBinder = new MyRemoteStream();
		}
		
		p.writeInt(mStream.getMode());
	}
	
	@Override
	protected void onClose() throws IOException {
		ensureIsNotLocal();
		try {
			mBinder.close();
			checkException();
		} catch(RemoteException e) {
			onException(e);
		}
	}
	
	@Override
	protected int onRead(long position) throws IOException {
		ensureIsNotLocal();
		try {
			int data = mBinder.read(position);
			checkException();
			return data;
		} catch(RemoteException e) {
			onException(e);
			return 0;
		}
	}
	
	@Override
	protected long onLength() throws IOException {
		ensureIsNotLocal();
		try {
			if(!isWritable()) {
				if(mLength == -1)
					return mLength = mBinder.length();
				else return mLength;
			} else {
				long len = mBinder.length();
				checkException();
				return len;
			}
		} catch(RemoteException e) {
			onException(e);
			return -1;
		}
	}
	
	@Override
	protected void onSetLength(long len) throws IOException {
		ensureIsNotLocal();
		try {
			mBinder.setLength(len);
			checkException();
		} catch(RemoteException e) {
			onException(e);
		}
	}
	
	@Override
	public boolean isLengthAvailable() {
		if(!mLengthAvailableInited) {
			try {
				mLengthAvailable = mBinder.isLengthAvailable();
				mLengthAvailableInited = true;
			} catch(RemoteException e) {
				onException(e);
			}
		}
		
		return mLengthAvailable;
	}
	
	@Override
	protected void onWrite(byte data) throws IOException {
		ensureIsNotLocal();
		try {
			mBinder.write(data);
			checkException();
		} catch(RemoteException e) {
			onException(e);
		}
	}
	
	private void ensureIsNotLocal() {
		if(mLocal)
			throw new IllegalStateException("this method should not be called from local.");
	}
	
	private void checkException() throws RemoteException, IOException {
		ExceptionParcelable ep = new ExceptionParcelable();
		mBinder.getState(ep);
		if(ep.has())
			throw new IOException(ep.get());
	}
	
	private void onException(RemoteException e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
}


class IORemoteException extends RemoteException
{
	public IORemoteException() {}
	
	public IORemoteException(String msg) {
		super(msg);
	}
	
	public IORemoteException(Exception e) {
		this(e.getMessage());
	}
}
