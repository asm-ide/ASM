package com.asm.text;

import java.util.ArrayList;


public class TextSelections extends TextPointers<TextSelection>
{
	public TextSelections() {
		super();
	}
	
	public TextSelections(int capacity) {
		super(capacity);
	}
	
	
	public TextSelection[] getStart(int pos) {
		ArrayList<TextSelection> found = new ArrayList<TextSelection>();
		for(TextSelection item : this)
			if(item.getPosition() == pos)
				found.add(item);
		
		return found.toArray(new TextSelection[0]);
	}
	
	public TextSelection[] getEnd(int pos) {
		ArrayList<TextSelection> found = new ArrayList<TextSelection>();
		for(TextSelection item : this)
			if(item.getPositionEnd() == pos)
				found.add(item);

		return found.toArray(new TextSelection[0]);
	}
	
	public TextSelection[] getIntersects(int pos) {
		ArrayList<TextSelection> found = new ArrayList<TextSelection>();
		for(TextSelection item : this)
			if(item.intersects(pos))
				found.add(item);
				
		return found.toArray(new TextSelection[0]);
	}
	
	public TextSelection[] getIntersects(TextSelection other) {
		ArrayList<TextSelection> found = new ArrayList<TextSelection>();
		for(TextSelection item : this)
			if(item.intersects(other))
				found.add(item);
		
		return found.toArray(new TextSelection[0]);
	}
}
