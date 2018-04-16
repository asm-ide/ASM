package com.asm.widget.codeedit;


public interface CodeStyleInterface
{
	public int getColor(String name);
	public int getColor(byte id);
	public byte getId(String name);
	public byte setColor(String name, int value);
}
