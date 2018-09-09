package com.asm.util;

import com.lhw.util.TextUtils;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.util.UnknownFormatConversionException;
import java.util.Objects;
import java.util.regex.Pattern;


public class StringParser extends Reader
{
	public static class Text implements CharSequence
	{
		private CharSequence mText;
		
		
		public Text(CharSequence text) {
			mText = text;
		}
		
		
		public int toInt() {
			return Integer.parseInt(toString());
		}
		
		public int toInt(int radix) {
			return Integer.parseInt(toString(), radix);
		}
		
		public float toFloat() {
			return Float.parseFloat(toString());
		}
		
		public double toDouble() {
			return Double.parseDouble(toString());
		}
		
		public boolean toBoolean() {
			return Boolean.parseBoolean(toString());
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof CharSequence? TextUtils.equals(this, (CharSequence) obj) : false;
		}
		
		@Override
		public int length() {
			return mText.length();
		}
		
		@Override
		public char charAt(int pos) {
			return mText.charAt(pos);
		}
		
		@Override
		public CharSequence subSequence(int start, int end) {
			return mText.subSequence(start, end);
		}
		
		@Override
		public String toString() {
			return str();
		}
		
		public String str() {
			return mText.toString();
		}
		
		public String strOrDefault(String defaultStr) {
			if(isEmpty())
				return defaultStr;
			
			return toString();
		}
		
		public boolean isEmpty() {
			return TextUtils.isEmpty(mText);
		}
	}
	
	
	
	private int mPos;
	private int mLeft;
	private StringBuilder mBuffer;
	private Reader mReader;
	private int mPeeks;
	
	
	public StringParser(String str) {
		this(str, 32);
	}
	
	public StringParser(CharSequence text, int capacity) {
		if(capacity != -1)
			mBuffer = new StringBuilder(capacity);
		
		set(text);
	}
	
	public StringParser() {
		this(32);
	}
	
	public StringParser(int capacity) {
		if(capacity != -1)
			mBuffer = new StringBuilder(capacity);
	}
	
	
	public StringParser set(CharSequence text) {
		mReader = new CharReader(text);
		mPeeks = 1;
		mPos = 0;
		mLeft = 0;
		clearBuffer();
		
		return this;
	}
	
	
	private static class CharReader extends Reader
	{
		private CharSequence mText;
		private int pos = 0;
		
		
		public CharReader(CharSequence text) {
			mText = text;
		}
		
		
		@Override
		public int read(char[] buf, int offset, int len) {
			for(int i = 0; i < len; i++) {
				if(pos == mText.length())
					return -1;
				buf[offset + i] = mText.charAt(pos);
				pos++;
			}
			
			return len;
		}
		
		@Override
		public void reset() {
			pos = 0;
		}
		
		@Override
		public boolean ready() {
			return true;
		}
		
		@Override
		public void close() {}
		
		public CharSequence sequence() {
			return mText;
		}
	}
	
	
	public StringParser append(CharSequence text) {
		return append(text, 0, text.length());
	}
	
	public StringParser append(CharSequence text, int offset, int len) {
		builder().append(text, offset, len);
		return this;
	}
		
	public StringBuilder builder() {
		if(mReader instanceof BuilderReader)
			return ((BuilderReader) mReader).builder();
		if(mReader instanceof CharReader) {
			BuilderReader br = new BuilderReader(((CharReader) mReader).sequence());
			mReader = br;
			return br.builder();
		}
		BuilderReader br = new BuilderReader(readAll(mReader));
		mReader = br;
		return br.builder();
	}
	
	private static CharSequence readAll(Reader reader) {
		StringBuilder builder = new StringBuilder();
		char[] buf = new char[32];
		int count;
		
		try {
			while((count = reader.read(buf)) != -1) {
				builder.append(buf, 0, count);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		return builder;
	}
	
	
	private static class BuilderReader extends Reader
	{
		private StringBuilder mText;
		private int pos = 0;
		
		
		public BuilderReader(CharSequence text) {
			mText = new StringBuilder(text);
		}
		
		
		@Override
		public int read(char[] buf, int offset, int len) {
			for(int i = 0; i < len; i++) {
				if(pos == mText.length())
					return -1;
				buf[offset + i] = mText.charAt(pos);
				pos++;
			}
			
			return len;
		}
		
		@Override
		public void reset() {
			pos = 0;
		}
		
		@Override
		public boolean ready() {
			return true;
		}
		
		@Override
		public void close() {}
		
		public StringBuilder builder() {
			return mText;
		}
	}
	
	
	public StringParser peekHow(int peeks) {
		mPeeks = peeks;
		return this;
	}
	
	/**
	 * Returns if next string equals {@code text}.
	 * If not equals, it just peeks, and return.
	 * If equals, increase position as the text length and returns true.
	 * @param text text to compare
	 * @return if next string equals {@code text}
	 */
	public boolean nextEquals(CharSequence text) {
		return nextEquals(text, 0, text.length());
	}
	
	/**
	 * Returns if next string equals {@code text}.
	 * If not equals, it just peeks, and return.
	 * If equals, increase position as the text length and returns true.
	 * @param text text to compare
	 * @param offset compare {@code text.charAt(offset)} ~ ...
	 * @param len compare ... ~ {@code text.charAt(offset + len)}
	 * @return if next string equals {@code text}
	 */
	public boolean nextEquals(CharSequence text, int offset, int len) {
		for(int i = 0; i < len; i++) {
			if(peek(i + mPeeks) != text.charAt(offset + i))
				return false;
		}
		removePeek(len + mPeeks - 1); // TODO: ???
		return true;
	}
	
	public boolean nextCharMatches(Pattern p) {
		int next = peek(mPeeks);
		if(next == -1) return false;
		boolean is = p.matcher(TextUtils.asSequence((char) next)).matches();
		if(is) removePeek(mPeeks);
		return is;
	}
	
	
	public Task tReadUntil(CharSequence find) {
		return new ReadUntilTask(find);
	}
	
	private class ReadUntilTask extends Task<CharSequence>
	{
		CharSequence find;
		
		ReadUntilTask(CharSequence pFind) {
			find = pFind;
		}
		
		@Override
		CharSequence onRun() {
			return readUntil(find);
		}
	}
	
	
	public Text readUntil(CharSequence find) {
		StringBuilder builder = new StringBuilder();
		
		char[] buf = new char[find.length()];
		
		while(true) {
			// TODO: if find is large, will take many times.
			if(peek(mPeeks, buf) == 0)
				return new Text(null);
			
			if(TextUtils.equals(buf, find))
				break;
			
			builder.append(read());
		}
		
		skip(buf.length);
		return new Text(builder);
	}
	
	public Text peekUntil(CharSequence find) {
		StringBuilder builder = new StringBuilder();
		
		char[] buf = new char[find.length()];
		
		int i = mPeeks;
		
		while(true) {
			int read;
			if((read = peek(i, buf)) == 0)
				return new Text(null);

			if(TextUtils.equals(buf, find))
				break;
			
			builder.append(read);
		}
		
		skip(i + find.length());
		return new Text(builder);
	}
	
	public Task tExpect(CharSequence text) {
		return tExpect(text, 0, text.length());
	}
	
	public Task tExpect(CharSequence text, int offset, int len) {
		return new ExpectTask(text, offset, len);
	}
	
	private class ExpectTask extends Task<Void>
	{
		CharSequence text;
		int offset;
		int len;
		
		ExpectTask(CharSequence pText, int pOffset, int pLen) {
			text = pText;
			offset = pOffset;
			len = pLen;
		}
		
		Void onRun() {
			expect(text, offset, len);
			return null;
		}
	}
	
	
	public StringParser expect(CharSequence text) {
		return expect(text, 0, text.length());
	}
	
	public StringParser expect(CharSequence text, int offset, int len) {
		if(!nextEquals(text, offset, len))
			throw new UnknownFormatConversionException("expected " 
				+ TextUtils.lightSubSequence(text, offset, len)
				+ "but got " + subSequencePeek(mPeeks, len)
		);
		
		return this;
	}
	
	
	public abstract class Task<T>
	{
		abstract T onRun();
		
		
		public StringParser run() {
			onRun();
			return cancel();
		}
		
		public StringParser repeat(int times) {
			for(int i = 0; i < times; i++)
				onRun();
			
			return cancel();
		}
		
		public StringParser untilEquals(T value) {
			while(Objects.equals(value, onRun())) {}
			return cancel();
		}
		
		public StringParser untilTrue() {
			while((Boolean) onRun()) {}
			return cancel();
		}
		
		public StringParser untilFalse() {
			while(!(Boolean) onRun()) {}
			return cancel();
		}
		
		public T untilEqualsAndLast(T value) {
			T last;
			while(Objects.equals(value, last = onRun())) {}
			return last;
		}
		
		public StringParser untilNeq(T value) {
			while(!Objects.equals(value, onRun())) {}
			return cancel();
		}
		
		public T untilNeqAndLast(T value) {
			T last;
			while(!Objects.equals(value, last = onRun())) {}
			return last;
		}
		
		public StringParser cancel() {
			return StringParser.this;
		}
	}
	
	
	public Text left() {
		int count;
		char[] buf = new char[32];
		StringBuilder builder = new StringBuilder();
		while((count = read(buf)) != -1)
			builder.append(buf, 0, count);
		
		return new Text(builder);
	}
	
	
	@Override
	public int read() {
		if(mLeft == 0) {
			clearBuffer();
			
			try {
				return mReader.read();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			// mBuffer never be null, as mLeft != 0
			if(mPos == mBuffer.capacity())
				mPos = 0;
			
			char text = mBuffer.charAt(mPos);
			mPos++;
			mLeft--;
			
			return text;
		}
	}
	
	@Override
	public long skip(long n) {
		for(long i = 0L; i < n; i++) {
			if(read() == -1)
				return i;
		}
		
		return n;
	}
	
	@Override
	public int read(char[] buf, int offset, int len) {
		for(int i = 0; i < len; i++) {
			int data = read();
			
			if(data == -1)
				return i;
			
			buf[offset + i] = (char) data;
		}
		return len;
	}
	
	@Override
	public int read(char[] cbuf) {
		return read(cbuf, 0, cbuf.length);
	}
	
	public int voidRead(int len) {
		for(int i = 0; i < len; i++) {
			int data = read();
			
			if(data == -1)
				return i;
		}
		return len;
	}
	
	public int peek(int delta) {
		if(delta <= 0)
			throw new IllegalArgumentException("delta <= 0");
		
		if(delta <= mLeft)
			return mBuffer.charAt(mPos + delta);
		
		int read = peekTo(delta);
		
		int pos = delta - mLeft;
		
		if(read < pos)
			return -1;
		
		return mBuffer.charAt(pos);
	}
	
	public Text subSequencePeek(int delta, int len) {
		char[] text = new char[len];
		peek(delta, text);
		return new Text(TextUtils.fromChars(text));
	}
	
	public int peek() {
		return peek(1);
	}
	
	public int peek(int delta, char[] buf) {
		return peek(delta, buf, 0, buf.length);
	}
	
	public int peek(int delta, char[] buf, int offset, int len) {
		for(int i = 0; i < len; i++) {
			int data = peek(delta + i);
			
			if(data == -1)
				return i;
			
			buf[offset + i] = (char) data;
		}
		return len;
	}
	
	public int peekAll() {
		needBuffer(32);
		char[] buf = new char[32];
		int nexts;
		int startLeft = mLeft;
		
		try {
			while((nexts = mReader.read(buf)) != -1) {
				mBuffer.append(buf, 0, nexts);
				mLeft += nexts;
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		return mLeft - startLeft;
	}
	
	public int peekTo(int delta) {
		needBuffer(delta);
		int charsToRead = delta - mLeft;
		
		if(charsToRead <= 0)
			return 0;
		
		try {
			for(int i = 0; i < charsToRead; i++) {
				int data = mReader.read();
				
			if(data == -1) return i;
				mBuffer.append((char) data);
				mLeft++;
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		return charsToRead;
	}
	
	private void needBuffer(int cap) {
		if(mBuffer == null)
			mBuffer = new StringBuilder(cap);
		
		if(mBuffer.capacity() < cap)
			mBuffer.ensureCapacity(cap);
	}
	
	private void clearBuffer() {
		if(mBuffer == null) return;
		if(mLeft == 0 && mBuffer.length() != 0) {
			mPos = 0;
			mBuffer.setLength(0);
			
			if(mBuffer.capacity() > 64)
				mBuffer.trimToSize();
		}
	}
	
	public int removePeek(int len) {
		int lastLeft = mLeft;
		mLeft -= len;
		if(mLeft < 0)
			mLeft = 0;
		
		int removed = lastLeft - len;
		mPos += removed;
		clearBuffer();
		
		return removed;
	}
	
	
	@Override
	public void close() {
		mBuffer = null;
		try {
			mReader.close();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static StringParser noBuf() {
		return new StringParser(-1);
	}
	
	public static StringParser noBuf(CharSequence text) {
		return new StringParser(text, -1);
	}
	
	public static StringParser small() {
		return new StringParser(8);
	}
	
	public static StringParser small(CharSequence text) {
		return new StringParser(text, 8);
	}
	
	public static StringParser big() {
		return new StringParser(128);
	}
	
	public static StringParser big(CharSequence text) {
		return new StringParser(text, 128);
	}
	
	public static StringParser asCap(CharSequence text) {
		return new StringParser(text, text.length());
	}
}

