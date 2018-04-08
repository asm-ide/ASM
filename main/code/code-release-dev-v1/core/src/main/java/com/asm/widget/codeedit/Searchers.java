package com.asm.widget.codeedit;

import java.util.ArrayList;

import static com.asm.widget.codeedit.Words.*;


public class Searchers
{
	//UNUSED
	public static final Searcher MATCH = new Searcher() {
		@Override
		public void find(ArrayList<Integer> target, Words words, TextPart p)
		{
			int mode = words.getArg("mode", 0);
			switch(mode & FLAG_MASKED) {
				case 0:
					//"todo"
					break;
			}
		}
	};
	
	public static final Searcher RANGE = new Searcher() {
		@Override
		public void find(ArrayList<Integer> target, Words words, TextPart p)
		{
			int mode = words.getArg("mode", 0);
			switch(mode & FLAG_MASKED) {
			}
		}
	};
}
