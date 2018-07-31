package com.lhw.util;

import android.os.Parcel;
import android.os.Parcelable;


public class ExceptionParcelable implements Parcelable
{
	public static final Creator<ExceptionParcelable> CREATOR 	
	= new Creator<ExceptionParcelable>() {
		@Override
		public ExceptionParcelable createFromParcel(Parcel p) {
			return new ExceptionParcelable(p);
		}

		@Override
		public ExceptionParcelable[] newArray(int len) {
			return new ExceptionParcelable[len];
		}
	};
	
	
	private Exception mException;
	
	public ExceptionParcelable() {}
	
	public ExceptionParcelable(Exception e) {
		mException = e;
	}
	
	public ExceptionParcelable(Parcel p) {
		try {
			p.readException();
		} catch(Exception e) {
			mException = e;
		}
	}
	

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel p, int flags) {
		if(mException == null) p.writeNoException();
		else p.writeException(mException);
	}
	
	public void readFromParcel(Parcel p) {
		mException = (Exception) p.readSerializable();
	}
	
	public Exception get() {
		return mException;
	}
	
	public void set(Exception e) {
		mException = e;
	}
	
	public boolean has() {
		return mException != null;
	}
}
