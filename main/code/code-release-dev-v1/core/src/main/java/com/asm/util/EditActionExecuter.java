package com.asm.util;

import com.asm.widget.codeedit.ScrollingTextView;
import java.util.ArrayList;


class EditActionExecuter
{
	public ArrayList<Executor> list;
	
	
	public static interface Executor {
		public String getName();
		public void exec(ScrollingTextView text, String cmd);
	}
	
	
	public EditActionExecuter() {
		list.add(new Executor() {
				public String getName() { return ""; }
				
				public void exec(ScrollingTextView text, String cmd)
				{
					
				}
			});
	}
	
	public void exec(ScrollingTextView text, String cmd) {
		for(int i = 0; i < list.size(); i++) {
			Executor e = list.get(i);
			if(cmd.startsWith(e.getName())) {
				e.exec(text, cmd);
			}
		}
	}
}
