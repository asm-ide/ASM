package com.asm.widget.codeedit;


public interface CodeStyleInterface
{
	public int getColor(String name);
	public int getColor(String type, String name);
	public int getColor(short id);
	public short getId(String name);
	public short setColor(String name, int value);
}
