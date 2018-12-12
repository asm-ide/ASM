package com.asm.widget.codeedit;


/**
 * Base highlightable interface for mark highlighted object.
 */
public interface Highlightable
{
	public void color(byte typeid, int start, int end);
	public byte[] array();
}
