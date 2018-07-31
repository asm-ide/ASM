package com.asm.io;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ArrayList;

import com.lhw.util.Bits;
import com.lhw.util.TextUtils;


public abstract class Stream implements Closeable, DataInput, DataOutput
{
	public static final int MODE_READ = 0x0 << 31;
	public static final int MODE_WRITE = MODE_READ << 1;
	public static final int MODE_SYNC = MODE_WRITE << 1;
	
	private String mName;
	private int mMode;
	private boolean mClosed = false;
	private long mPosition = 0L;
	
	
	public Stream(String name, int mode) throws IOException {
		mName = name;
		mMode = mode;
	}
	
	
	public String getName() {
		return mName;
	}
	
	public int getMode() {
		return mMode;
	}
	
	public boolean isReadable() {
		return (mMode & MODE_READ) == MODE_READ;
	}
	
	public boolean isWritable() {
		return (mMode & MODE_WRITE) == MODE_WRITE;
	}
	
	public boolean isSync() {
		return (mMode & MODE_SYNC) == MODE_SYNC;
	}

	@Override
	public void close() throws IOException {
		if(mClosed) return;
		onClose();
		mClosed = true;
	}
	
	public boolean isClosed() {
		return mClosed;
	}
	
	@SuppressWarnings("unchecked")
	protected void onClose() throws IOException {}
	
	
	
	// Read methods
	
	
	public int read() throws IOException {
		if(!isReadable())
			throw new SecurityException("read mode was not setted");
		
		long pos = position();
		if(pos >= length())
			throw new EOFException();
		
		
		int data = onRead(pos);
		skipBytes(1);
		return data;
	}
	
	public int peekNext() throws IOException {
		long pos = position();
		if(pos >= length()) throw new EOFException();
		if(!isReadable()) throw new SecurityException("read mode was not set");
		int data = onRead(pos);
		return data;
	}
	
	public long length() throws IOException {
		if(!isReadable()) throw new SecurityException("read mode was not set");
		if(!isLengthAvailable()) throw new SecurityException("this stream was not allowed to check length");
		return onLength();
	}
	
	protected abstract int onRead(long position) throws IOException;
	
	/**
	 * Not according to available, if reached at end, return -1.
	 */
	protected abstract long onLength() throws IOException, UnsupportedOperationException;
	
	protected abstract void onSetLength(long len) throws IOException, UnsupportedOperationException;
	
	public abstract boolean isLengthAvailable();
	
	
	public long position() {
		return mPosition;
	}
	
	public boolean isLeft() throws IOException {
		return onLength() != -1;
	}
	
	public long leftBytes() throws IOException {
		return length() - mPosition;
	}
	
	public void seek(long pos) throws IOException, EOFException {
		if(pos > length()) throw new EOFException();
		mPosition = pos;
	}
	
	public byte[] read(int len) throws IOException {
		byte[] data = new byte[len];
		readFully(data);
		return data;
	}
	
	public int read(byte[] data) throws IOException {
		return read(data, 0, data.length);
	}
	
	public int read(byte[] data, int offset, int len) throws IOException {
		if(offset < 0)
			throw new IndexOutOfBoundsException("offset < 0");
		else if(len < 0)
			throw new IndexOutOfBoundsException("len < 0");
		
		for(int i = 0; i < len; i++) {
			try {
				int d = read();
				data[i + offset] = (byte) d;
			} catch(EOFException e) {
				return i;
			}
		}
		return len;
	}
	
	public byte[] readAll() throws IOException {
		if(isLengthAvailable()) {
			long len = leftBytes();
			if(len > Integer.MAX_VALUE - 1)
				throw new OutOfMemoryError();
			
			return read((int) len);
		} else {
			byte[] data = new byte[0];
			byte[] buf = new byte[128];
			int len;
			
			while((len = read(buf)) != -1) {
				int lastlen = data.length;
				data = Arrays.copyOf(data, lastlen + len);
				System.arraycopy(buf, 0, data, lastlen, len);
			}
			
			return data;
		}
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return readShort();
	}
	
	@Override
	public char readChar() throws IOException {
		return Bits.getChar(read(2), 0);
	}
	
	@Override
	public String readLine() throws IOException {
		StringBuilder sb = new StringBuilder();
		int c = -1;
		boolean end = false;
		while(!end) {
			c = read();
			
			switch(c) {
				case -1:
				case '\n':
					end = true;
					break;
				
				case '\r':
					end = true;
					long cur = position();
					if(read() != '\n')
						seek(cur);
					break;
				
				default:
					sb.append((char) c);
					break;
			}
		}
		if(c == -1 || sb.length() == 0)
			return null;
		
		return sb.toString();
	}
	
	@Override
	public String readUTF() throws IOException {
		ByteBuffer bbuf = ByteBuffer.wrap(readAll());
		CharBuffer cbuf = Charset.forName("UTF-8").decode(bbuf);
		char[] arr = cbuf.array();
		return String.valueOf(arr);
	}
	
	@Override
	public int readInt() throws IOException {
		return Bits.getInt(read(4), 0);
	}
	
	@Override
	public void readFully(byte[] data, int offset, int len) throws IOException {
		read(data, offset, len);
	}
	
	@Override
	public void readFully(byte[] data) throws IOException {
		read(data, 0, data.length);
	}
	
	@Override
	public byte readByte() throws IOException {
		int next = read();
		if(next == -1) throw new EOFException();
		return (byte) next;
	}
	
	@Override
	public boolean readBoolean() throws IOException {
		return Bits.getBoolean(read(1), 0);
	}
	
	@Override
	public int readUnsignedByte() throws IOException {
		return readByte();
	}
	
	@Override
	public long readLong() throws IOException {
		return Bits.getLong(read(8), 0);
	}
	
	@Override
	public short readShort() throws IOException {
		return Bits.getShort(read(2), 0);
	}
	
	@Override
	public double readDouble() throws IOException {
		return Bits.getDouble(read(8), 0);
	}
	
	@Override
	public float readFloat() throws IOException {
		return Bits.getFloat(read(4), 0);
	}

	@Override
	public int skipBytes(int bytes) throws IOException {
		if(bytes < 0) return 0;
		long pos = mPosition;
		long len = length();
		long newPos = pos + bytes;
		if(newPos > len) newPos = len;
		
		seek(newPos);
		
		return (int) (newPos - pos);
	}
	
	
	
	// Write methods
	
	
	protected abstract void onWrite(byte data) throws IOException;
	
	
	public void setLength(long newLength) throws IOException {
		if(!isWritable()) throw new SecurityException("write mode was not set");
		onSetLength(newLength);
	}
	
	@Override
	public void write(byte[] data, int offset, int len) throws IOException {
		if(offset < 0)
			throw new IndexOutOfBoundsException("offset < 0");
		else if(len < 0)
			throw new IndexOutOfBoundsException("len < 0");
		int limit = offset + len;
		
		for(int i = offset; i < limit; i++) {
			write(data[i]);
		}
	}
	
	@Override
	public void write(byte[] data) throws IOException {
		write(data, 0, data.length);
	}
	
	@Override
	public void write(int data) throws IOException {
		if(!isWritable())
			throw new SecurityException("write mode was not setted");

		long pos = position();
		if(pos >= length())
			throw new EOFException();

		onWrite((byte) data);
		skipBytes(1);
	}

	@Override
	public void writeBoolean(boolean data) throws IOException {
		byte[] arr = new byte[1];
		Bits.putBoolean(arr, 0, data);
		write(arr);
	}
	
	@Override
	public void writeByte(int data) throws IOException {
		write(data);
	}
	
	@Override
	public void writeLong(long data) throws IOException {
		byte[] arr = new byte[8];
		Bits.putLong(arr, 0, data);
		write(arr);
	}

	@Override
	public void writeFloat(float data) throws IOException {
		byte[] arr = new byte[4];
		Bits.putFloat(arr, 0, data);
		write(arr);
	}
	
	@Override
	public void writeDouble(double data) throws IOException {
		byte[] arr = new byte[8];
		Bits.putDouble(arr, 0, data);
		write(arr);
	}
	
	public void writeChar(int data) throws IOException {
		byte[] arr = new byte[2];
		Bits.putChar(arr, 0, (char) data);
		write(arr);
	}
	
	@Override
	public void writeShort(int data) throws IOException {
		byte[] arr = new byte[2];
		Bits.putShort(arr, 0, (short) data);
		write(arr);
	}
	
	@Override
	public void writeInt(int data) throws IOException {
		byte[] arr = new byte[4];
		Bits.putInt(arr, 0, data);
		write(arr);
	}
	
	@Override
	public void writeChars(String data) throws IOException {
		for(int i = 0; i < data.length(); i++) {
			writeChar(data.charAt(i));
		}
	}

	@Override
	public void writeUTF(String data) throws IOException {
		TextUtils.writeUTF(data, this);
	}
	
	@Override
	public void writeBytes(String data) throws IOException {
		writeBytes(data);
	}
	
	
	
	// Other methods
	
	
	@Override
	protected void finalize() {
		if(!mClosed) {
			System.err.println("Stream not closed");
			try {
				close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
}
